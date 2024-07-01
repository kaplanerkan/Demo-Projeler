package com.lotuspecas.a03_fetchdownloader

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lotuspecas.a03_fetchdownloader.databinding.ActivityMainBinding
import com.tonyodev.fetch2.DefaultFetchNotificationManager
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.DownloadBlock


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var fetch: Fetch
    private var i = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {

        val fetchConfiguration =
            FetchConfiguration.Builder(this).setDownloadConcurrentLimit(3).setNamespace("sdsd")
                .setNotificationManager(object : DefaultFetchNotificationManager(this) {

                    override fun getFetchInstanceForNamespace(namespace: String): Fetch {
                        return fetch
                    }
                }).build()

        fetch = getInstance(fetchConfiguration)

        binding.btnStartDownload.setOnClickListener {
            i += 1
            startDownload(i)
        }


        val fetchListener: FetchListener = object : FetchListener {
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

                /* if (request.getId() === download.id) {
                     showDownloadInList(download)
                 }*/

            }

            override fun onCompleted(download: Download) {
                Log.e("onCompleted", "onCompleted ${download.url}")
            }

            fun onError(download: Download) {
                val error: Error = download.error
            }

            override fun onProgress(
                download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long
            ) {
                /* if (request.getId() === download.id) {
                     updateDownload(download, etaInMilliSeconds)
                 }*/
                val progress = download.progress
                Log.d("Progress ${download.id}", "$progress")
            }

            override fun onPaused(download: Download) {
                Log.e("Paused", "Paused $download.id.toString()")
            }
            override fun onResumed(download: Download) {
                Log.e("Resumed", "Resumed $download.id.toString()")
            }
            override fun onStarted(
                download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int
            ) {
            }

            override fun onWaitingNetwork(download: Download) {
            }

            override fun onAdded(download: Download) {
            }

            override fun onCancelled(download: Download) {}
            override fun onRemoved(download: Download) {}
            override fun onDeleted(download: Download) {}
            override fun onDownloadBlockUpdated(download: Download,
                                                downloadBlock: DownloadBlock,
                                                totalBlocks: Int) {
                Log.e("HAHA","onDownloadBlockUpdated")
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {

            }
        }

        fetch.addListener(fetchListener)
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Write External Storage permission allows us to save files. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100
            )
        }

    }


    private fun startDownload(i: Int) {
        val url =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val file = getSaveDir() + "/test${i}.mp4"
        val request = Request(url, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        fetch.enqueue(request, {
            Log.e("Request", it.url)
        }, {
            Log.e("Error", it.name)
        })
    }


    private fun getSaveDir(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
    }


}