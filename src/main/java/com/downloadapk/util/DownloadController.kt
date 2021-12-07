package com.downloadapk.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.downloadapk.BuildConfig
import com.downloadapk.R
import java.io.File
import java.lang.Exception

class DownloadController(private val context: Context, private val url: String) {

    companion object {
        private const val FILE_NAME = "payloadapp.apk"
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun enqueueDownload() {

        var noName =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
        var destination = "/sdcard/Android/media/"
        destination += FILE_NAME

        val uri = Uri.parse("$FILE_BASE_PATH" + noName)

        val file = File(destination)
        var f = file.exists()

        val dir = File("/sdcard/Android/media/")
        var l = dir.exists()
        var x = dir.listFiles()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse("https://www.google.com/search?q=Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION&rlz=1C1ONGR_deDE962DE962&oq=Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION&aqs=chrome..69i57j0i19j0i5i19i30l3.527j0j7&sourceid=chrome&ie=UTF-8")
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        request.setTitle(context.getString(R.string.title_file_download))
        request.setDescription(context.getString(R.string.downloading))
        // set destination
        request.setDestinationUri(uri)

        showInstallOption(destination, uri, noName)
        // Enqueue a new download and same the referenceId
            downloadManager.enqueue(request)



    }

    /*private fun bla(){

        val file: File
        file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/Download/")
        }
        var allfiles: Array<File?>? = null
        allfiles = file.listFiles()

    }*/

    private fun showInstallOption(
        destination: String,
        uri: Uri,
        desNoName: String
    ) {

        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    var f = File("/sdcard/Android/media")
                    var ex = f.exists()
                    var y = f.listFiles()

                    var file = File(destination)
                    var fileExists = file.exists()

                    val res = context.getPackageManager().canRequestPackageInstalls()

                    val contentUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                        File(destination)
                    )
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                    // finish()
                } else {
                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(
                        uri,
                        APP_INSTALL_PATH
                    )
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                    // finish()
                }
            }
        }
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}
