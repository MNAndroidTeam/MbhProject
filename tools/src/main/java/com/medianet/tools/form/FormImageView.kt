package com.medianet.tools.form

import android.content.ContentResolver
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import com.makeramen.roundedimageview.RoundedImageView
import com.medianet.tools.R
import com.squareup.picasso.Picasso
import java.io.File


class FormImageView(
    context: Context,
    attrs: AttributeSet? = null
) : RoundedImageView(context, attrs) {

    var attribute : String? =null
    var imageFrom : Int
    var imageAs : Int
    var imagePicker : Int
    var path : String = ""
    var base64 : String = ""

    init {

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FormImageView, 0, 0
        )
        attribute = a.getString(R.styleable.FormImageView_attribute)
        imageFrom = a.getInt(R.styleable.FormImageView_imageFrom,0)
        imagePicker = a.getInt(R.styleable.FormImageView_image_picker,0)
        imageAs = a.getInt(R.styleable.FormImageView_imageAs,0)

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

        when(imageAs){
            0 -> {
               return base64
            }
            1-> {
                return path
            }
        }
        return ""
    }

    fun getString64FromURI(contentURI: Uri, contentResolver: ContentResolver) {
        try {
            contentURI.let {
                if(Build.VERSION.SDK_INT < 28) {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        contentURI
                    )
                    base64 = bitmapToString(bitmap)?:""

                } else {
                    val source = ImageDecoder.createSource(contentResolver, contentURI)
                    val bitmap = ImageDecoder.decodeBitmap(source)

                    base64 = bitmapToString(bitmap)?:""


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}