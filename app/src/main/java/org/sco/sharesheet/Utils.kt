package org.sco.sharesheet

import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.*

class Utils(private val context: Context) {
    companion object {
        private const val TAG = "SHARING"
    }

    fun getClipDataThumbnail(uri: Uri, contentResolver: ContentResolver): ClipData? {
        return try {
            ClipData.newUri(contentResolver, null, uri)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "getClipDataThumbnail: oops", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "getClipDataThumbnail: oops", e)
            null
        }
    }

    @Throws(IOException::class)
    fun saveThumbnail(drawable: Int, resources: Resources, imageCacheDir: String, imageFile: String, fileProviderAuthority: String): Uri {
        val bm: Bitmap? = ResourcesCompat.getDrawable(resources, drawable, null)?.toBitmap()
        val cachePath = File(context.cacheDir, imageCacheDir)
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/$imageFile")
        bm?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val imagePath = File(context.cacheDir, imageCacheDir)
        val newFile = File(imagePath, imageFile)
        return FileProvider.getUriForFile(context, fileProviderAuthority, newFile)
    }

    // Below this unused for now
    private fun getImageUri(): Uri {
        val cacheFilesDir = context.cacheDir
        val theimage = cacheFilesDir?.listFiles()?.get(0)
        return FileProvider.getUriForFile(
                context,
                "org.sco.sharesheet.fileprovider",
                theimage!!
        )
    }

    fun copyAssets() {
        val assetManager = context.assets
        var files: Array<String>? = null
        try {
            files = assetManager.list("")
        } catch (e: IOException) {
            Log.e(TAG, "copyAssets: failed to get assets", e)
        }

        if (files != null) {
            for (file: String in files) {
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    inputStream = assetManager.open(file)
                    val outfile = File(context.cacheDir, file)
                    outputStream = FileOutputStream(outfile)
                    copyFile(inputStream, outputStream)
                } catch (e: IOException) {
                    Log.e(TAG, "copyAssets: failed to copy asset file $file", e)
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                            inputStream = null
                        } catch (e: IOException) {
                            Log.e(TAG, "copyAssets: failed to close input stream", e)
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.flush()
                            outputStream.close()
                            outputStream = null
                        } catch (e: IOException) {
                            Log.e(TAG, "copyAssets: failed to close output stream", e)
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
    }

}