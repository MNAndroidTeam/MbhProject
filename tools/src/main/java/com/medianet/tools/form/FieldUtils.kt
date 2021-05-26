package com.medianet.tools.form

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


var dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
var dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
var timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

var CAMERA_PICKER = 7276
var GALLERIE_PICKER = 4376

fun TextInputLayout.removeErrorBoxListener() {
    val it = this
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            it.error = null
            it.isErrorEnabled = false
        }

        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {

        }
    })
}


fun TextInputLayout.retriveText() : String{
    return this.editText?.text.toString()
}

data class Login(
    @Email var email: String ="",
    @Password var password : String=""
)


fun getRealPathFromUri(
    context: Context,
    contentUri: Uri?
): String {
    var cursor: Cursor? = null
    return try {
        val proj =
            arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } finally {
        cursor?.close()
    }
}


fun getCameraPhotoOrientation(imageFilePath: String): Float {
    var rotate = 0f
    try {
        val exif = ExifInterface(imageFilePath)
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270f
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180f
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90f
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return rotate
}

fun stringToBitMap(image: String?): Bitmap? {
    return try {
        val encodeByte =
            Base64.decode(image, Base64.DEFAULT)
        val inputStream: InputStream = ByteArrayInputStream(encodeByte)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.message
        null
    }
}

fun bitmapToString(bitmap: Bitmap): String? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}


  fun viewToImage(view: View): Bitmap? {
    val returnedBitmap =
        Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable: Drawable = view.background
      bgDrawable.draw(canvas)
    view.draw(canvas)
    return returnedBitmap
}

