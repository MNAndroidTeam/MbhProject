package com.medianet.tools.form

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import com.medianet.tools.R
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*


class FormTextInputLayout(
    context: Context,
    attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {


    var choosenFormatter = dateTimeFormatter


    var attribute : String? =null
    var maxDateByDay : Int = 0
    var minDateByDay : Int = -1
    var dateTime : Int = 0
    var formTextInputLayoutListener: FormTextInputLayoutListener?= null

    interface FormTextInputLayoutListener {
        fun onDatePickedListener(date : String)
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FormTextInputLayout, 0, 0
        )

        attribute = a.getString(R.styleable.FormTextInputLayout_attribute)
        minDateByDay = a.getInt(R.styleable.FormTextInputLayout_minDateByDay,-1)
        maxDateByDay = a.getInt(R.styleable.FormTextInputLayout_maxDateByDay,0)

         if (attribute != null) tag = attribute



        a.recycle()
    }

    fun initData(clazz: Class<*> ){
        removeErrorBoxListener()
        if (!attribute.isNullOrEmpty()){
            val field = clazz.declaredFields.find { it.name == attribute?.split('/')?.get(0)?:"" }

            if (field != null){
                if (Number::class.java.isAssignableFrom(field.type)){
                    editText?.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                }
                initField(field)
            }
        }
    }

    private fun initField(field: Field, level : Int = 0) {
        field.isAccessible = true
        field.annotations.forEach {
            when(it){

                is Email -> {
                    editText?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
                is Password -> {
                    editText?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    endIconMode = END_ICON_PASSWORD_TOGGLE
                }

                is IP_V4_ADDRESS -> {
                    editText?.filters = arrayOf(InputFilter.LengthFilter(15))
                }
                is Phone -> {
                    editText?.inputType =  InputType.TYPE_NUMBER_FLAG_SIGNED + InputType.TYPE_CLASS_NUMBER
                    editText?.filters = arrayOf(InputFilter.LengthFilter(it.value))
                }
                is Length -> {
                    if (it.value > 0){
                        editText?.filters = arrayOf(InputFilter.LengthFilter(it.max))
                    }else if (it.max > 0){
                        editText?.filters = arrayOf(InputFilter.LengthFilter(it.max))
                    }

                }
                is EmbendedError -> {

                    val embendedErrorField = Class.forName(field.type.name?:"").declaredFields.find { it.name == attribute?.split('/')?.get(level+1)?:""  }
                    embendedErrorField?.isAccessible = true
                    if (embendedErrorField != null){
                        initField(embendedErrorField,1)
                    }
                }
            }
        }
    }

    fun enableDateTimeMode(formatter : SimpleDateFormat?=null ,formTextInputLayoutListener: FormTextInputLayoutListener?=null){
        this.formTextInputLayoutListener = formTextInputLayoutListener

        if(dateTime != 0){
            if (dateTime == 1 || dateTime == 3){

                choosenFormatter = formatter ?:  if (dateTime == 1 ) dateFormatter  else dateTimeFormatter

                editText?.setOnClickListener{
                    getDate()
                }
            }else{
                choosenFormatter = formatter ?: timeFormatter
                editText?.setOnClickListener{
                    getTime(Calendar.getInstance())
                }
            }

           // editText?.setText(choosenFormatter.format(Calendar.getInstance().time))
        }
    }

    private fun getDate() {

        val c =  Calendar.getInstance().also {
            val time = getDateValue()
            if (time != null) it.time = time
        }
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { v, year, monthOfYear, dayOfMonth ->

                val calendar = Calendar.getInstance().also {
                    it.set(Calendar.DAY_OF_MONTH , dayOfMonth)
                    it.set(Calendar.MONTH , monthOfYear)
                    it.set(Calendar.YEAR , year)
                }
                Log.e("wwwwwwww","${choosenFormatter.format(calendar.time)}")
                editText?.setText(choosenFormatter.format(calendar.time))

                if (dateTime == 3 ){
                    getTime( calendar )
                }else{
                    formTextInputLayoutListener?.onDatePickedListener(editText?.text.toString())
                }
            }, mYear, mMonth, mDay
        )

        datePickerDialog.setCancelable(false)
        datePickerDialog.setTitle(null)


        if (minDateByDay > 0) datePickerDialog.datePicker.minDate = Calendar.getInstance().also {
            it.add(Calendar.DATE, minDateByDay * (-1))
        }.timeInMillis

        datePickerDialog.datePicker.maxDate = Calendar.getInstance().also {
            it.add(Calendar.DATE, maxDateByDay )
        }.timeInMillis



        datePickerDialog.show()

    }

    private fun getTime( cal : Calendar) {

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->


                val calendar = cal.also {
                    it.set(Calendar.HOUR , selectedHour)
                    it.set(Calendar.MINUTE , selectedMinute)
                }
                editText?.setText(choosenFormatter.format(calendar.time))
                formTextInputLayoutListener?.onDatePickedListener(editText?.text.toString())

            },
            hour,
            minute,
            true
        )//Yes 24 hour time
        mTimePicker.show()
        mTimePicker.setCancelable(false)

    }

    fun getDateValue() : Date? {
        var date = editText?.text.toString()
        if (date.isEmpty()) date = choosenFormatter.format(Calendar.getInstance().time)
        return if (dateTime != 0) choosenFormatter.parse(date) else null
    }

    fun setError(msg : String){
        isErrorEnabled = true
        error = msg
    }
}