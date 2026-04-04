package com.cookingapp.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHelper(private val context: Context) {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
        private const val REQUEST_IMAGE_CAPTURE = 1002
    }

    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null
    private var onPhotoCapturedListener: ((Uri, String) -> Unit)? = null

    fun checkAndRequestCameraPermission(activity: Activity): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            false
        }
    }

    fun launchCamera(activity: Activity, onPhotoCaptured: (Uri, String) -> Unit) {
        this.onPhotoCapturedListener = onPhotoCaptured

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            // Create a file to store the photo
            val photoFile = createImageFile()

            if (photoFile != null) {
                photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                } else {
                    Uri.fromFile(photoFile)
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                val filePath = currentPhotoPath ?: ""
                onPhotoCapturedListener?.invoke(uri, filePath)
            }
        }
    }

    fun savePhotoToGallery(uri: Uri): String? {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "CookingApp_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CookingApp")
                }
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    resolver.openInputStream(uri)?.use { inputStream ->
                        outputStream.write(inputStream.readBytes())
                    }
                }
                it.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getBitmapFromUri(uri: Uri, maxWidth: Int = 800, maxHeight: Int = 800): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            var scale = 1
            while ((options.outWidth / scale) > maxWidth && (options.outHeight / scale) > maxHeight) {
                scale++
            }

            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }

            val scaledStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(scaledStream, null, scaledOptions)
            scaledStream?.close()

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun compressAndSaveImage(sourcePath: String, quality: Int = 80): String? {
        return try {
            val bitmap = BitmapFactory.decodeFile(sourcePath)
            val compressedFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "compressed_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(compressedFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }

            compressedFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}