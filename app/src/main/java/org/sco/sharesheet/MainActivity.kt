package org.sco.sharesheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SHARING"
    }

    private lateinit var button: Button
    private lateinit var imageView: ImageView
    private lateinit var imageUri: Uri
    private lateinit var utils: Utils
    private val imageCacheDir = "cache"
    private val imageFile = "image.png"
    private val fileProviderAuthority = "org.sco.sharesheet.fileprovider"
    private var shareUrl = "https://developer.android.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utils = Utils(applicationContext)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.shareButton)
        imageView = findViewById(R.id.image)
        imageUri = utils.saveThumbnail(R.mipmap.ic_launcher, resources, imageCacheDir, imageFile, fileProviderAuthority)
        imageView.setImageURI(imageUri)
        button.setOnClickListener {
            val intent = createIntent()
            val shareIntent = Intent.createChooser(intent, "Share This")
            startActivity(shareIntent)
        }
    }

    private fun createIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Subject Line")
            putExtra(Intent.EXTRA_TEXT, "Check out this cool thing \n$shareUrl\n You might learn something.")
            putExtra(Intent.EXTRA_TITLE, "Share this!")
            val thumbnail = utils.getClipDataThumbnail(imageUri, contentResolver)
            thumbnail?.let {
                this.clipData = it
                this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }


}