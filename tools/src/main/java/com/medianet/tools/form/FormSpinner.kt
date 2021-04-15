package com.medianet.tools.form

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import com.medianet.tools.R


class FormSpinner(
    context: Context,
    attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatSpinner(context, attrs) {

    var attribute : String? =null
    var spinnerAttribute : String? =null
    var spinnerClass : Class<*>? =null
    var data : List<*>? = null
    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FormSpinner, 0, 0
        )
        attribute = a.getString(R.styleable.FormSpinner_attribute)
        spinnerAttribute = a.getString(R.styleable.FormSpinner_spinnerAttribute)


         if (attribute != null) tag = attribute
        a.recycle()

    }


    inline fun <reified T> initData(data : List<T> ){

        this.data = data
        val adapterData = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item ,data)
        adapter = adapterData
        adapterData.notifyDataSetChanged()
        spinnerClass = T::class.java
    }


    fun  selectItem (item : Any){
        if (data != null){
            val itemToSelect =
                if(spinnerAttribute.isNullOrEmpty()){
                    data?.find { item == it }
                }
                else   {
                    data?.find {
                        if (it != null){
                            val field =  it::class.java.declaredFields.find { it.name == spinnerAttribute }
                            if (field != null){
                                field.isAccessible = true
                                return@find field.get(it) == item
                            }
                        }

                        false
                    }
                }


            if (itemToSelect != null ) setSelection(data!!.indexOf(itemToSelect),true)
        }



    }

     fun getSpinnerSelectedItem() : Any? {
        if (selectedItemPosition > -1){
            val item = data?.get(selectedItemPosition)
            if (item != null)
            if (spinnerAttribute != null){
                val field=  spinnerClass?.declaredFields?.find { it.name == spinnerAttribute }
                field?.isAccessible = true
                return  field?.get(item)
            }else{
                return item
            }
        }
         return null
    }

}