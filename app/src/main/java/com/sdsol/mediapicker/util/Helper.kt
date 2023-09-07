package com.sdsol.mediapicker.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.exifinterface.media.ExifInterface
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.sdsol.mediapicker.MediaType
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

fun compressImage(
    context: Context,
    imagePath: String,
    maxHeight: Float = 1080.0f,
    maxWidth: Float = 1080.0f
): String? {
    var scaledBitmap: Bitmap?

    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true

    BitmapFactory.decodeFile(imagePath, options)

    val bmp: Bitmap?

    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight

    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
            imgRatio = maxHeight / actualHeight
            actualWidth = (imgRatio * actualWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (imgRatio > maxRatio) {
            imgRatio = maxWidth / actualWidth
            actualHeight = (imgRatio * actualHeight).toInt()
            actualWidth = maxWidth.toInt()
        } else {
            actualHeight = maxHeight.toInt()
            actualWidth = maxWidth.toInt()
        }
    }

    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
    options.inJustDecodeBounds = false
    options.inDither = false
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)

    try {
        bmp = BitmapFactory.decodeFile(imagePath, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
        return null
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        return null
    }

    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
        return null
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        return null
    }

    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f

    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    if (bmp != null) {
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(FILTER_BITMAP_FLAG)
        )
    }

    bmp?.recycle()

    val exif: ExifInterface
    try {
        exif = ExifInterface(imagePath)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }

    val out: FileOutputStream?
    val filepath = getFilename(context)
    try {
        out = FileOutputStream(filepath)
        scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    return filepath
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }

    return inSampleSize
}

private fun getFilename(context: Context): String {
    val mediaStorageDir =
        File("${Environment.getExternalStorageDirectory()}/Android/data/${context.applicationContext.packageName}/Files/Compressed")
    if (!mediaStorageDir.exists()) {
        mediaStorageDir.mkdirs()
    }

    val mImageName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
    return mediaStorageDir.absolutePath + "/" + mImageName
}

fun Context.getMediaType(uri: Uri): MediaType {
    val mimeType =
        this.contentResolver.getType(uri)?.toLowerCase(Locale.current) ?: return MediaType.Unknown
    return when {
        mimeType.startsWith("image/") -> MediaType.Image
        mimeType.startsWith("video/") -> MediaType.Video
        else -> MediaType.Unknown
    }
}

fun Context.getPathFromContentUri(contentUri: Uri, extension: String = ".jpg"): String? {
    val filePath =
        this.applicationInfo.dataDir +
                File.separator +
                System.currentTimeMillis() +
                (this.getFileExtension(contentUri) ?: extension)
    val file = File(filePath)
    try {
        val inputStream = contentResolver.openInputStream(contentUri) ?: return null
        val outputStream: OutputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
    } catch (ignore: IOException) {
        return null
    }
    return file.absolutePath
}

fun Context.createImageFile(): File? {
    val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(
            Date()
        )
    val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", ".jpg", storageDir
    )
}

fun Context.createVideoFile(): File? {
    val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(
            Date()
        )
    val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "Video_${timeStamp}_", ".mp4", storageDir
    )
}

fun Long.bytesToMegabytes(): Double {
    val number = this / 1e6
    return String.format("%.2f", number).toDouble()
}

fun Context.getFileSizeFromContentUri(uri: Uri?): Double {
    if (uri == null) return -1.0
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor.use { c ->
        if (c != null && c.moveToFirst()) {
            val sizeIndex = c.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                return c.getLong(sizeIndex).bytesToMegabytes()
            }
        }
    }
    return -1.0
}


fun Context.compressVideo(
    sourceUri: Uri,
    destinationFolder: String = "starfish-videos",
    videoQuality: VideoQuality = VideoQuality.MEDIUM,
    listener: CompressionListener
) {
    VideoCompressor.start(
        context = this,
        uris = listOf(sourceUri),
        isStreamable = false,
        appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
            subFolderName = destinationFolder
        ),
        configureWith = Configuration(
            videoNames = listOf("Video_${System.currentTimeMillis()}"),
            quality = videoQuality,
            isMinBitrateCheckEnabled = false
        ),
        listener = listener
    )
}

fun Context.getFileExtension(uri: Uri?): String? {
    if (uri == null) return null
    val contentResolver = this.contentResolver
    val mimeType = contentResolver.getType(uri)
    return if (mimeType != null) {
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    } else {
        val path = uri.path
        path?.substringAfterLast('.')
    }
}