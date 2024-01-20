package com.dtu.deltecdtu.receiver

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.dtu.deltecdtu.Util.Utility
import java.io.File


class DownloadCompletedReceiver() : BroadcastReceiver() {
    private lateinit var downloadManager: DownloadManager

    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(DownloadManager::class.java)!!
        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L) {
//                Log.e("Download in receiver", "Download Complete")
                Toast.makeText(context, "Download complete!", Toast.LENGTH_SHORT).show()
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadFilePath =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val uriString = cursor.getString(uriIndex)
                    val fileOne = File(Uri.parse(uriString).path)
                    val filePath = fileOne.absolutePath
                    val mimeType = Utility.getMimeType(uriIndex.toString())

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val file = File(filePath)
                        val uri = FileProvider.getUriForFile(
                            context,
                            context.applicationContext.packageName + ".provider",
                            file
                        )
                        val intentFile = Intent(Intent.ACTION_VIEW)
                        intentFile.setDataAndType(uri, mimeType)
                        intentFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intentFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        try {
                            context.startActivity(intentFile)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No App installed to open file", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        var intentFile = Intent(Intent.ACTION_VIEW)
                        intentFile.setDataAndType(Uri.parse(downloadFilePath), mimeType)
                        intentFile = Intent.createChooser(intent, "Select App")
                        intentFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intentFile)
                    }

                }
                cursor.close()
            }

        }
    }
}


