package com.dtu.deltecdtu.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.webkit.MimeTypeMap
import com.bumptech.glide.request.RequestOptions
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import java.io.File
import java.io.IOException
import java.util.Calendar

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

                val generatedBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(
                    generatedBitmap, null, null,
                    android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                page.close()
                pdfRenderer.close()
                fileDescriptor.close()
                generatedBitmap
            } catch (error: IOException) {
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
        val downloader = AndroidDownloader(context)
        return downloader.downloadFileFromDTUNoticeAdapter(url, position, dtuNoticeAdapter)
    }

    fun isDownloaded(context: Context, url: String): Boolean {
        val fileName = getFileNameFromUrl(url)
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, fileName)
        return file.exists()
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
        }
        return type
    }

    fun currentDate(): String {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val mo = calender.get(Calendar.MONTH) + 1
        val day = calender.get(Calendar.DAY_OF_MONTH)
        var month = ""
        if (mo < 10) {
            month = "0$mo"
        }
        return "$day-$month-$year"
    }

    fun getSuitableDate(date: Int): String {
        return if (date < 10) {
            "0$date"
        } else {
            "$date"
        }
    }
    fun getGlideOptions(): RequestOptions {
        return RequestOptions().centerCrop().placeholder(R.drawable.person_image)
            .error(R.drawable.person_image)
    }
}