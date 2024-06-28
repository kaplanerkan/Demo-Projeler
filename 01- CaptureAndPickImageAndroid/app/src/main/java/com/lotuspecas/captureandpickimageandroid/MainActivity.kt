package com.lotuspecas.captureandpickimageandroid

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lotuspecas.captureandpickimageandroid.databinding.ActivityMainBinding
import org.apache.commons.io.FileUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageAdapter
    private var selectedPaths = mutableListOf<ImageModel>()

    /**
     * Startet die Aktivität zur Auswahl von Bildern
     */
    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                selectedPaths = mutableListOf()

                val data: Intent? = result.data
                //Wenn mehrere Bilder ausgewählt sind
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        val file = getImageFromUri(imageUri)
                        file?.let {
                            val image = ImageModel(it.absolutePath)
                            selectedPaths.add(image)
                        }
                    }

                    imageAdapter.updateList(selectedPaths)

                }
                //Wenn nur ein Bild ausgewählt ist
                else if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    val file = getImageFromUri(imageUri)
                    file?.let {
                        val image = ImageModel(it.absolutePath)

                        selectedPaths.add(image)

                    }

                    imageAdapter.updateList(selectedPaths)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initviews()
    }


    /**
     * Initialisiert die Views
     */
    private fun initviews() {
        imageAdapter = ImageAdapter()
        binding.rvImages.adapter = imageAdapter


        /**
         * Öffnet den Dateiauswahldialog
         */
        binding.btnSelectImages.setOnClickListener {

            try {

                deleteTempFiles()

                val intent = Intent(ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "*/*"
                selectImagesActivityResult.launch(intent)


            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "Error deleting temp files")
            }

        }

    }


    /**
     * Gibt die Datei aus einem Uri zurück
     */
    private fun getImageFromUri(imageUri: Uri?): File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(this@MainActivity, uri)
            mimeType?.let {
                val file = createTmpFileFromUri(this, imageUri, "temp_image", ".$it")
                file?.let { Log.e("image Url = ", file.absolutePath) }
                return file
            }
        }
        return null
    }


    /**
     * Gibt den Mimetyp einer Datei zurück
     */
    private fun getMimeType(context: Context, uri: Uri): String? {
        //Konvertiert den Uri in eine Dateiendung
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //Wenn das Schema ein Inhalt ist
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //Wenn das Schema ein Dateischema ist
            //Diese wird verwendet, um die Dateiendung zu erhalten
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { File(it) }).toString())
        }
        return extension
    }

    /**
     * Erstellt eine temporäre Datei aus einem Uri
     */
    private fun createTmpFileFromUri(
        context: Context, uri: Uri, fileName: String, mimeType: String
    ): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile(fileName, mimeType, cacheDir)
            FileUtils.copyInputStreamToFile(stream, file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Löscht temporäre Dateien
     */
    private fun deleteTempFiles(file: File = cacheDir): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteTempFiles(f)
                    } else {
                        f.delete()
                    }
                }
            }
        }
        return file.delete()
    }

}