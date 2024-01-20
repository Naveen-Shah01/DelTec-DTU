package com.dtu.deltecdtu.Util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import android.webkit.MimeTypeMap
import com.dtu.deltecdtu.AndroidDownloader
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import java.io.File
import java.io.IOException

object Utility {
    fun generateBitmap(context: Context, url: String, type: String): Bitmap? {
        val bitmap: Bitmap?
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val fileName = getFileNameFromUrl(url)
        if (type == "pdf") {
            val pdfFile = File(directory, fileName)
            bitmap = try {
                val fileDescriptor =
                    ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = android.graphics.pdf.PdfRenderer(fileDescriptor)
                val page = pdfRenderer.openPage(0)

                val bitmapPdf = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(
                    bitmapPdf, null, null,
                    android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                page.close()
                pdfRenderer.close()
                fileDescriptor.close()
                bitmapPdf
            } catch (e: IOException) {
                Log.e("PDF Thumbnail", "Error generating thumbnail", e)
                null
            }
        } else {
            val filePath = File(directory, fileName).path
            bitmap = BitmapFactory.decodeFile(filePath)
        }
        if (bitmap != null) {
            Bitmap.createScaledBitmap(bitmap, 80, 80, false)
        }
        return bitmap
    }

    fun getFileNameFromUrl(url: String): String {
        val extractedString = url.substringAfter("http://dtu.ac.in/")
        return extractedString.replace('/', '_')
    }

    fun setFilename(url: String): String {
        return url.substringAfterLast("/")
    }

    fun downloadFile(
        context: Context, url: String, position: Int, dtuNoticeAdapter: DTUNoticeAdapter
    ): Long {
//        Log.e("Download in Utility","Download clicked")
//        Log.e("Download in Utility","$url")
//        Log.e("Download in Utility","$position")
//        Log.e("Download in Utility","$dtuNoticeAdapter")


        val downloader = AndroidDownloader(context)
//        Log.e("Download in Utility","context passed")

        return downloader.downloadFileFromDTUNoticeAdapter(url, position, dtuNoticeAdapter)
    }

    fun isDownloaded(context: Context, url: String): Boolean {
        val fileName = getFileNameFromUrl(url)
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, fileName)
//        Log.e("checking","${file.exists()}")
        return file.exists()
    }

    fun isDownloadable(url: String): Boolean {
        val type = getFileTypeFromUrl(url)
        return type == "image" || type == "pdf"
    }

    fun getFileTypeFromUrl(url: String): String {
        return when {
            url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(
                ".bmp"
            ) || url.endsWith(".webp") || url.endsWith(".tiff") || url.endsWith(".ico") || url.endsWith(
                ".svg"
            ) -> {
                "image"
            }

            url.endsWith(".pdf") -> {
                "pdf"
            }

            else -> {
                "unknown"
            }
        }
    }

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
//            Log.e("download mimeType", "$type")
        }
        return type

    }
}