package com.dtu.deltecdtu.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

import android.widget.Toast
import com.dtu.deltecdtu.Downloader
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import com.example.deltecdtu.Util.Constants.Companion.DOWNLOAD_FAILED
import com.example.deltecdtu.Util.Constants.Companion.DOWNLOAD_SUCCESS
import com.dtu.deltecdtu.adapter.SubListAdapter
import com.example.deltecdtu.Util.Constants.Companion.DOWNLOAD_TAG

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class AndroidDownloader(private val context: Context) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private lateinit var query: DownloadManager.Query
    private var totalBytes = 1

    fun downloadFromSubListAdapter(
        url: String, position: Int, subListAdapter: SubListAdapter
    ): Long {

        val fileName = Utility.getFileNameFromUrl(url)
        val mimeType = Utility.getMimeType(url)
        Timber.tag(DOWNLOAD_TAG).e("downloading $fileName")
        val request = DownloadManager.Request(Uri.parse(url))
            .setMimeType(mimeType)
            .setTitle(fileName)
            .setDescription("Downloading...")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
        val downloadId = downloadManager.enqueue(request)
        Timber.tag(DOWNLOAD_TAG).e("downloadId is $downloadId")
        checkStatus(downloadId, url, position, subListAdapter)

        return downloadId
    }

    private fun checkStatus(
        downloadId: Long,
        url: String,
        position: Int,
        subListAdapter: SubListAdapter
    ) {

        query = DownloadManager.Query()
        query.setFilterById(downloadId)

        CoroutineScope(Dispatchers.IO).launch {
            val status = run(downloadId)
            if (status == DOWNLOAD_SUCCESS) {
                val type = subListAdapter.noticesList[position].type
                Timber.tag(DOWNLOAD_TAG).e("in coroutines type is $type")

                subListAdapter.noticesList[position].isDownloaded = true
                withContext(Dispatchers.Main) {
                    val bitmap = Utility.generateBitmap(context, url, type)


                    if (bitmap != null) {
                        subListAdapter.noticesList[position].bitmap = bitmap
                        subListAdapter.notifyItemChanged(position)

                    }
                }

            } else if (status == DOWNLOAD_FAILED) {
                subListAdapter.notifyItemChanged(position)
                Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun downloadFileFromDTUNoticeAdapter(
        url: String,
        position: Int,
        dtuNoticeAdapter: DTUNoticeAdapter
    ): Long {

        Timber.tag(DOWNLOAD_TAG).e("Download clicked")
        Timber.tag(DOWNLOAD_TAG).e("url: $url")
        Timber.tag(DOWNLOAD_TAG).e("position $position")
        Timber.tag(DOWNLOAD_TAG).e("adapter $dtuNoticeAdapter")

        val fileName = Utility.getFileNameFromUrl(url)
        val mimeType = Utility.getMimeType(url)

        Timber.tag(DOWNLOAD_TAG).e("file name: $fileName")
        Timber.tag(DOWNLOAD_TAG).e("Mimetype: $mimeType")
        val request = DownloadManager.Request(Uri.parse(url))
            .setMimeType(mimeType)
            .setTitle(fileName)
            .setDescription("Downloading...")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )

        val downloadId = downloadManager.enqueue(request)

        callForStatus(downloadId, url, position, dtuNoticeAdapter)
        return downloadId
    }

    private fun callForStatus(
        downloadId: Long, url: String, position: Int, dtuNoticeAdapter: DTUNoticeAdapter
    ) {
        Timber.tag(DOWNLOAD_TAG).e("status called")
        Timber.tag(DOWNLOAD_TAG).e("url: $url")
        Timber.tag(DOWNLOAD_TAG).e("position $position")
        Timber.tag(DOWNLOAD_TAG).e("adapter $dtuNoticeAdapter")
        Timber.tag(DOWNLOAD_TAG).e("download id: $downloadId")

        query = DownloadManager.Query()
        query.setFilterById(downloadId)

        CoroutineScope(Dispatchers.IO).launch {
            val status = run(downloadId)
            if (status == DOWNLOAD_SUCCESS) {
                Timber.tag(DOWNLOAD_TAG).e("in coroutines Success")
                val type = dtuNoticeAdapter.noticesList[position].type
                Timber.tag(DOWNLOAD_TAG).e("in coroutines type is $type")
                dtuNoticeAdapter.noticesList[position].isDownloaded = true
                Timber.tag(DOWNLOAD_TAG).e("in coroutines isDownloaded is True")

                withContext(Dispatchers.Main) {
                    val bitmap = Utility.generateBitmap(context, url, type)
                    if (bitmap != null) {
                        dtuNoticeAdapter.noticesList[position].bitmap = bitmap
                        dtuNoticeAdapter.notifyItemChanged(position)
                        Timber.tag(DOWNLOAD_TAG).e("in coroutines Adapter updated")
                    }
                }

            } else if (status == DOWNLOAD_FAILED) {
                dtuNoticeAdapter.notifyItemChanged(position)
                Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("Range")
    private suspend fun run(downloadId: Long): Long {
        while (downloadId > 0) {
            delay(800)
            downloadManager.query(query).use { cursor ->
                if (cursor.moveToFirst()) {
                    if (totalBytes <= 0)
                        totalBytes =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val bytesDownloadSoFar =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    when (downloadStatus) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Timber.tag(DOWNLOAD_TAG).e("Download in run Downloaded")
                            return DOWNLOAD_SUCCESS
                        }

                        DownloadManager.STATUS_FAILED -> {
                            return DOWNLOAD_FAILED
                        }

                        else -> {
                            Timber.tag(DOWNLOAD_TAG).e("Download in run running")
                            val downloadProgress = bytesDownloadSoFar * 100L / totalBytes
                        }
                    }
                }
            }
        }
        return -1
    }
}