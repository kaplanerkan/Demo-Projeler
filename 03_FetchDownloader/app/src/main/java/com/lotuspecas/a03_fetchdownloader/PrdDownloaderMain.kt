package com.lotuspecas.a03_fetchdownloader

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.lotuspecas.a03_fetchdownloader.databinding.ActivityMainBinding
import java.util.Locale

class PrdDownloaderMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDownloading = false
    private var totalBytes: Long = 0
    private var totalMegabytes: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_main)


        initViews()


    }


    private fun initViews() {
        binding.rl1.visibility = View.GONE
        binding.rl2.visibility = View.GONE


        binding.btnStartDownload.setOnClickListener {

            binding.btnStartDownload.visibility = View.INVISIBLE

            binding.rl1.visibility = View.VISIBLE
            binding.rl2.visibility = View.VISIBLE

            val downloadId: Int = PRDownloader.download(
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                getSaveDir(),
                "BigBuckBunny.mp4"
            ).build().setOnStartOrResumeListener {

                Log.e("HAHA", "Download Started")
                setListenerStatus("Download Started")
            }.setOnPauseListener {
                Log.e("HAHA", "Download Paused")
                setListenerStatus("Download Paused")
            }.setOnCancelListener {
                Log.e("HAHA", "Download Cancelled")
                setListenerStatus("Download Cancelled")
            }.setOnProgressListener { progress ->

                if (!isDownloading) {
                    totalBytes = progress.totalBytes
                    totalMegabytes = totalBytes.toDouble() / (1024 * 1024)
                }

                val currentBytes: Long = progress.currentBytes
                val currentMegabytes = currentBytes.toDouble() / (1024 * 1024)
                val value = (currentMegabytes * 100 / totalMegabytes).toInt()
                Log.e("VALUE", "Value: $value")

                binding.progressBar.progress = value
                binding.progressBar2.progress = value

                binding.progressBarinsideText.text = String.format(
                    Locale.GERMAN, "%.2f MB", currentMegabytes
                )

                binding.tvStatus.text = String.format(
                    Locale.GERMAN, "%.2f MB", currentMegabytes
                )


                isDownloading = true

            }.start(object : OnDownloadListener {

                override fun onDownloadComplete() {
                    Log.e("HAHA", "Download Complete")
                    binding.rl1.visibility = View.GONE
                    binding.rl2.visibility = View.GONE

                    setListenerStatus("Download Complete")

                    // Handler ve Runnable kullanarak gizleme işlemi
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        // Buraya gizleme işlemlerini yapabilirsiniz
                        binding.tvListenerResult.visibility = View.GONE
                        binding.btnStartDownload.visibility = View.VISIBLE
                    }, 5000) // 5000 milisaniye = 5 saniye

                }

                override fun onError(error: com.downloader.Error?) {
                    binding.rl1.visibility = View.GONE
                    binding.rl2.visibility = View.GONE
                    Log.e("HAHA", error.toString())
                    setListenerStatus(error.toString())
                    binding.btnStartDownload.visibility = View.VISIBLE
                }

            })
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Write External Storage permission allows us to save files. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100
            )
        }


    }


    private fun getSaveDir(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
    }

    private fun setListenerStatus(status: String) {
        binding.tvListenerResult.visibility = View.VISIBLE
        binding.tvListenerResult.text = status
    }

}