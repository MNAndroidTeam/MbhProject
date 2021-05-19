package com.medianet.tools.form

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.medianet.tools.R
import java.text.SimpleDateFormat
import java.util.*

class FormContainerLayout  (
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) , View.OnClickListener {

    var supportError : Boolean? = false
    var startDate : Int = 0
    var endDate : Int = 0
    var date : Int = 0
    var dateTime : Int = 0
    var defaultPopup : Boolean
    var differenceBetweenDates : Int = 0
    var imageUri: Uri? = null
    var pathFromCamera = ""
    var activity: Activity? = null
    var pathFromGallerie = ""
    var selectedImageView : FormImageView? = null

    // 0 : if camera
    // 1 : gallerie
    var methodeToCallAfterPermissionsGranted  : Int ? = null

    var appPermission = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    companion object{
        var PERMISSIONS_REQUEST_CODE = 100
    }


    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FormContainerLayout, 0, 0
        )
        supportError = a.getBoolean(R.styleable.FormContainerLayout_supportError,false)
        endDate = a.getResourceId(R.styleable.FormContainerLayout_endDate,0)
        date = a.getResourceId(R.styleable.FormContainerLayout_date,0)
        startDate = a.getResourceId(R.styleable.FormContainerLayout_startDate,0)
        dateTime = a.getInt(R.styleable.FormContainerLayout_dateTimePicker,0)
        defaultPopup = a.getBoolean(R.styleable.FormImageView_defaultPopup,true)
        differenceBetweenDates = a.getInt(R.styleable.FormContainerLayout_differenceBetweenDates,0)

        a.recycle()

    }

    inline fun <reified T> formContainerVerification(
        activity: Activity,
        model : T?=null ,
        formatter : SimpleDateFormat?=null,
        formResultListener : FormResultListener<T>? = null
    ) {

        this.activity = activity
        children.forEach {
            if (it is FormImageView){
                if (it.imagePicker != 0) it.setOnClickListener(this)
            }
            if (it is FormTextInputLayout) it.initData(T::class.java)
            if (model != null )initFields(it, model)
        }

        findViewWithTag<View>("submit")?.setOnClickListener {
            if (formResultListener != null) checkForm(model,formResultListener)
        }
        setOnClickListener(this)

        if (date != 0){
            findViewById<FormTextInputLayout>(date).also {
                it.dateTime = dateTime
                it.enableDateTimeMode(formatter,  object :
                    FormTextInputLayout.FormTextInputLayoutListener{
                    override fun onDatePickedListener(date: String) {
                        val calendar1 = Calendar.getInstance()
                        calendar1.add(Calendar.HOUR,differenceBetweenDates )

                        val calendar2 = Calendar.getInstance()
                        calendar2.time = formatter?.parse(date)

                        if (differenceBetweenDates > 0){

                            if (calendar1.before(calendar2)) {
                                findViewById<FormTextInputLayout>(endDate).error = context.getString(R.string.verif_date)
                            } else {
                                findViewById<FormTextInputLayout>(endDate).error = null
                            }
                        }else{

                            if (calendar1.after(calendar2)) {
                                findViewById<FormTextInputLayout>(endDate).error = context.getString(R.string.verif_date)
                            } else {
                                findViewById<FormTextInputLayout>(endDate).error = null
                            }
                        }





                    }
                })

            }
        }

        if (startDate != 0){
            findViewById<FormTextInputLayout>(startDate).also {
                it.dateTime = dateTime
                it.enableDateTimeMode(formatter,  object :
                    FormTextInputLayout.FormTextInputLayoutListener{
                    override fun onDatePickedListener(date: String) {
                        verifDate()
                    }
                })

            }
        }
        if (endDate != 0){

            findViewById<FormTextInputLayout>(endDate).also {
                it.dateTime = dateTime
                it.enableDateTimeMode(formatter = formatter, formTextInputLayoutListener = object :
                    FormTextInputLayout.FormTextInputLayoutListener{
                    override fun onDatePickedListener(date: String) {
                        verifDate()
                    }
                } )
            }
        }
    }

     fun initFields(view: View, model: Any , level : Int = 0) {
        try {

            val field =  model::class.java.declaredFields.find { it.name == view.tag.toString().split('/')[level] }


            if (field != null){
                field.isAccessible = true
                if (field.isAnnotationPresent(EmbendedError::class.java)){
                    initFields(view, model, level+1)
                }

                else when(view) {
                    is FormTextInputLayout -> {
                        view.editText?.setText(field.get(model)?.toString()?:"")
                    }

                    is TextView  -> {
                        view.text = field.get(model)?.toString()?:""
                    }

                    is FormImageView -> {
                        view.setImageFromAttribute(field.get(model))
                    }

                    is RadioGroup -> {
                        val radioButton = view.findViewWithTag<RadioButton>(field.get(model)?.toString()?:"").id
                        view.check(radioButton)
                    }

                    is FormSpinner -> {
                        val data = field.get(model)
                        if (data != null)view.selectItem(data)
                    }
                }
            }


        }catch (e : java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun verifDate() {
        try {

        val dateEntree = findViewById<FormTextInputLayout>(startDate).getDateValue()
        val dateSortie = findViewById<FormTextInputLayout>(endDate).getDateValue()

        if (dateSortie != null) {
            findViewById<FormTextInputLayout>(endDate).error = null
        }

        if (dateEntree != null && dateSortie!= null ) {


            Log.e("ccccccc","$differenceBetweenDates")
            val calendar1 = Calendar.getInstance()
            calendar1.time = dateEntree
            calendar1 .add(Calendar.HOUR,differenceBetweenDates )

            val calendar2 = Calendar.getInstance()
            calendar2.time = dateSortie

            if (calendar1.after(calendar2)) {
                findViewById<FormTextInputLayout>(endDate).error = context.getString(R.string.verif_date)
            } else {
                findViewById<FormTextInputLayout>(endDate).error = null
            }

        }

        } catch (e: Exception) {
            findViewById<FormTextInputLayout>(endDate).error = context.getString(R.string.verif_date)
        }
    }

    inline fun <reified T> checkForm(model : T ?=null,formResultListener: FormResultListener<T>){
        object : FomFieldVerification{
            override var formContainerLayout: FormContainerLayout = this@FormContainerLayout

            override fun resultStatusListener(resultStatus: ResultStatus) {
                when(resultStatus){
                    is ResultError -> {

                        if (supportError == true){
                            val editText = findFormEditTextByAttribute(resultStatus.field)
                            if (editText != null){
                                findFormEditTextByAttribute(resultStatus.field)?.setError(resultStatus.msg)
                            }
                        }

                        val id = (children.find { it.tag == resultStatus.field }?.id )?:-1

                        formResultListener.formResultErrorListener(resultStatus.msg,id)

                    }
                    is ResultSuccess -> {

                        val condition =  findViewWithTag<View>("condition")

                        val accepted = if (condition == null) true else
                            when(  condition){
                                is CheckBox -> {
                                    condition.isChecked
                                }
                                else -> {
                                    condition.isSelected
                                }
                            }


                        if (accepted){
                            val result = model ?: T::class.java.newInstance()
                            createModelInstance(result)

                            formResultListener.formResultSuccessListener(result)
                        }else{
                            formResultListener.formResultErrorListener(context.getString(R.string.accept_terms_exception),condition.id)
                        }

                    }
                }
            }

        }.checkErrors(
            T::class
        )
    }

    fun createModelInstance( clazz: Any? , fieldParent : String= ""  )  {
        if (clazz != null){

           clazz::class.java.declaredFields.forEach {
                it.isAccessible = true
                val tag = if (fieldParent=="") it.name else fieldParent+"/"+it.name

                 when(it.type){
                    String::class.java -> {
                       if (getFieldViewType(tag) != null)  it.set(clazz,getFieldViewValue(tag))
                    }
                    Int::class.java ->{
                        if (getFieldViewType(tag) != null)  it.setInt(clazz,getFieldViewValue(tag)?.toString()?.toInt()?:0)
                    }
                    Float::class.java ->{
                        if (getFieldViewType(tag) != null)   it.setFloat(clazz,getFieldViewValue(tag)?.toString()?.toFloat()?:0f)
                    }
                    Double::class.java ->{
                        if (getFieldViewType(tag) != null) it.setDouble(clazz,getFieldViewValue(tag)?.toString()?.toDouble()?:0.0)
                    }

                    else -> {
                        if (it.isAnnotationPresent(EmbendedError::class.java)){

                            if (children.filter { it.tag is String }.find { it.tag.toString().split('/').get(0) == tag } != null){
                                val embendded =  Class.forName(it.type.name).newInstance()
                                it.set(clazz ,embendded)
                                createModelInstance(embendded , tag )
                            }

                        }else{
                            if (getFieldViewType(tag) != null)   it.set(clazz,getFieldViewValue(tag))
                        }
                    }

                }
            }
        }
    }

    fun findFormEditTextByAttribute(attribute : String) : FormTextInputLayout?{
        return children.filter { (it is FormTextInputLayout)  }.map { it as FormTextInputLayout }.find { it.attribute == attribute }
    }

    fun editTextNotEmpty(tag: String) : Boolean{
        return  findFormEditTextByAttribute(tag)?.editText?.text?.isEmpty() == true
    }

    fun editTextValue(tag: String) : String{
        return  findFormEditTextByAttribute(tag)?.editText?.text?.toString()?.trim()?:""
    }

    fun findIdByTag(tag: String) = findFormEditTextByAttribute(tag)?.id?:-1

    fun getFieldViewType(attribute : String) : View? {
        return children.find { it.tag == attribute }
    }

    private fun getFieldViewValue(attribute : String) : Any? {
        when(val fieldView = getFieldViewType(attribute)){
            is FormTextInputLayout -> {
               return fieldView.retriveText()
            }

            is RadioGroup-> {
                return findViewById<RadioButton>(fieldView.checkedRadioButtonId).tag.toString()
            }

            is FormSpinner -> {
                return fieldView.getSpinnerSelectedItem()
            }

            is FormImageView -> {
                return fieldView.getImageValue()
            }

        }
        return ""
    }

    fun showPicturePopup() {
        methodeToCallAfterPermissionsGranted =2

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermession()) {
                return
            }
        }
        val inflater1 = LayoutInflater.from(context.applicationContext)
        val viewPopup: View = inflater1.inflate(R.layout.popup_picker_photo, null)
        val popup1 = PopupWindow(context.applicationContext)
        val animatedView = viewPopup.findViewById<View>(R.id.popup_view)
        animatedView.setAnimation(
            AnimationUtils.loadAnimation(
                context.applicationContext,
                R.anim.popup_in
            )
        )

        popup1.contentView = viewPopup
        popup1.height = WindowManager.LayoutParams.MATCH_PARENT
        popup1.width = WindowManager.LayoutParams.MATCH_PARENT
        popup1.isOutsideTouchable = false
        popup1.isFocusable = true

        viewPopup.findViewById<View>(R.id.btn_fermer)
            .setOnClickListener { dismissPopup(popup1, animatedView) }

        viewPopup.findViewById<View>(R.id.touch_outside)
            .setOnClickListener { dismissPopup(popup1, animatedView) }

        viewPopup.findViewById<View>(R.id.popup_view)
            .setOnClickListener { }

        popup1.showAtLocation(this, Gravity.CENTER, 0, 0)
        viewPopup.findViewById<View>(R.id.btnGalerie)
            .setOnClickListener {

                choosePhotoFromGallary()
                dismissPopup(popup1, animatedView)
            }
        viewPopup.findViewById<View>(R.id.btnCamera)
            .setOnClickListener {
                dismissPopup(popup1, animatedView)
                takePhotoFromCamera()
            }
    }

    fun checkPermession(): Boolean {
        val listPermissionNeeded: MutableList<String> =
            ArrayList()
        for (perm in appPermission) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(perm)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity?.requestPermissions(listPermissionNeeded.toTypedArray(), PERMISSIONS_REQUEST_CODE)
            }
            Log.e("mmmmmmm","sssss")
            return false
        }
        return true
    }

    fun choosePhotoFromGallary(imageView : FormImageView? = null) {
        methodeToCallAfterPermissionsGranted = 1
        if (imageView != null) selectedImageView = imageView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermession()) {
                return
            }
        }

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity?.startActivityForResult(galleryIntent, GALLERIE_PICKER)
    }

    fun takePhotoFromCamera(imageView : FormImageView? = null) {
        methodeToCallAfterPermissionsGranted = 0
        if (imageView != null) selectedImageView = imageView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermession()) {
                return
            }
        }

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = context.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        pathFromCamera = getRealPathFromUri(context, imageUri)?:""
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activity?.startActivityForResult(intent, CAMERA_PICKER)

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERIE_PICKER) {
            if (data != null) {
                try {
                    val contentURI = data.data
                    pathFromGallerie = FileChooser.getPath(context, contentURI)
                    selectedImageView?.path = pathFromGallerie

                    if (contentURI != null) selectedImageView?.setImage(contentURI, getCameraPhotoOrientation(pathFromGallerie))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } else if (requestCode == CAMERA_PICKER) {
            try {
              if (imageUri != null){
                  selectedImageView?.path = pathFromCamera
                  selectedImageView?.setImage(imageUri!! , getCameraPhotoOrientation(pathFromCamera))
              }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onRequestPermissionsResultGranted() : Boolean{

        if (methodeToCallAfterPermissionsGranted != null ){
            if (methodeToCallAfterPermissionsGranted == 0) takePhotoFromCamera()
            else if (methodeToCallAfterPermissionsGranted == 1) choosePhotoFromGallary()
            else if (methodeToCallAfterPermissionsGranted == 2) showPicturePopup()
            return true
        }
        return false


    }

    fun dismissPopup(popup: PopupWindow, animatedView: View) {

        val animation = AnimationUtils.loadAnimation(
            context.getApplicationContext(),
            R.anim.popup_out
        )
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                popup.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        animatedView.startAnimation(animation)
    }

    override fun onClick(view : View) {
        when (view) {
            is FormImageView -> {
                selectedImageView = view
                when (view.imagePicker) {

                    1 ->  takePhotoFromCamera()
                    2 ->  choosePhotoFromGallary()
                    3 ->  if (defaultPopup) showPicturePopup()
                }
            }
        }

    }


}