package com.medianet.tools.form

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import androidx.exifinterface.media.ExifInterface
import com.makeramen.roundedimageview.RoundedImageView
import com.medianet.tools.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException


class FormImageView(
    context: Context,
    attrs: AttributeSet? = null
) : RoundedImageView(context, attrs) {

    var attribute : String? =null
    var imageFrom : Int
    var imagePicker : Int

    init {

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FormImageView, 0, 0
        )
        attribute = a.getString(R.styleable.FormImageView_attribute)
        imageFrom = a.getInt(R.styleable.FormImageView_imageFrom,0)
        imagePicker = a.getInt(R.styleable.FormImageView_image_picker,0)

         if (attribute != null) tag = attribute
        a.recycle()
    }


    fun setImageFromAttribute( data : Any? ){
        if (data != null)
        when(imageFrom){
            0 -> {
                setImage(data as String)
            }
            1 -> {
                setImage(File(data as String))
            }
            2 -> {
                setImage(data as Int)
            }
        }
    }

    fun setImage( url : String ) {
       Picasso.get().load(url).fit().centerCrop().into(this)
    }

    fun setImage( uri : Uri , rotation : Float ) {

        Picasso.get().load(uri).fit().centerCrop().rotate(rotation).into(this)
    }

    fun setImage( file : File ) {
        Picasso.get().load(file).fit().centerCrop().into(this)
    }

    fun setImage( resourceId : Int ) {
        Picasso.get().load(resourceId).fit().centerCrop().into(this)
    }

    fun getImageValue(): String {
        val bitmap = viewToImage(this)
        return if (bitmap != null) bitmapToString(bitmap)?:"" else ""
    }

}