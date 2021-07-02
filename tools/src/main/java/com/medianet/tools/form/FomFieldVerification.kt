package com.medianet.tools.form

import android.util.Log
import android.util.Patterns
import android.widget.RadioGroup
import androidx.core.view.children
import com.medianet.tools.R
import java.lang.NumberFormatException
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaField

interface FomFieldVerification {

    var formContainerLayout: FormContainerLayout
    fun resultStatusListener(resultStatus: ResultStatus)

    fun checkErrors(kClass: KClass<*> ) {


            Thread {
                try {

                    val fields = ArrayList(kClass.declaredMemberProperties.toList())
                    fields.addAll(kClass.superclasses.flatMap { it.declaredMemberProperties }.toList())

                    formContainerLayout.children.filter { it.tag is String }.map { it.tag as String }.toList().forEach {tag ->


                    val propriety =
                        fields.find { it.name == tag.split('/')[0] }

                    if(propriety != null){
                        checkProprety(
                            propriety.javaField,
                            tag
                        )
                    }

                }
                resultStatusListener(ResultSuccess())

                } catch (e:  FieldErrorAnnotationException) {
                        resultStatusListener(e.data)
                    }

            }.run()

    }


    private fun checkProprety(
        propriety: Field?,
        tag: String,
        level: Int=1
    ): Boolean {

        if (propriety?.declaredAnnotations?.isNotEmpty() == true) {

            if (propriety.isAnnotationPresent(EmbendedError::class.java)) {

                val f = propriety.type
                val field =  f.declaredFields.find { it.name == tag.split('/')[level] }
                if (field!=null) checkProprety(field,tag,level+1)




            }
            else {
                val maxLength = (propriety.getAnnotation( Length::class.java)?.max ?: 0)
                val minLength = (propriety.getAnnotation( Length::class.java)?.min ?: 0)
                val length = (propriety.getAnnotation( Length::class.java)?.value ?: 0)

                if (isNotValidLength(maxLength) || isNotValidLength(minLength) || isNotValidLength(
                        length
                    )
                ) {
                    throw FieldDevelopperErrorAnnotationException(
                        "Length of ' ${propriety.name} ' must be positive"
                    )
                }

                if (maxLength != UNDEFINED_LENGTH && minLength != UNDEFINED_LENGTH && maxLength < minLength) {
                    throw FieldDevelopperErrorAnnotationException(
                        "max length must be greater than min length in '${propriety.name} ${propriety.javaClass.name}'"
                    )

                }

                val maxmin = maxLength + minLength
                if (maxmin > 0 && length > 0 && length != maxLength) {
                    throw FieldDevelopperErrorAnnotationException(
                        "error in lenght declaration '${propriety.name} ${propriety.javaClass.name}' exception : remove min max or lenght"
                    )
                }

                if (propriety.isAnnotationPresent(IP_V4_ADDRESS::class.java)){
                    if (propriety.type != String::class.java){
                        throw FieldDevelopperErrorAnnotationException(
                            "propriety name : ${propriety.name} must be String"
                        )
                    }
                }
                propriety.declaredAnnotations.forEach { annotation ->
                    when (annotation) {
                        is NotEmpty -> {
                            val data = checkIsFieldNotEmpty(tag)
                            if (data != null) {
                                throw FieldErrorAnnotationException(
                                    data
                                )
                            }
                        }

                        is Password ->  {
                            val data = checkIsFielPasswordCorrect(tag)
                            if (data != null) {
                                throw FieldErrorAnnotationException(
                                    data
                                )
                            }
                        }
                        is Email -> {
                            val data = checkIsFieldEmail(propriety, tag)
                            if (data != null) {
                                throw FieldErrorAnnotationException(
                                    data
                                )
                            }
                        }

                        is IP_V4_ADDRESS -> {
                            val data = checkIfFieldIsIpV4( tag)
                            if (data != null) {
                                throw FieldErrorAnnotationException(
                                    data
                                )
                            }
                        }

                        is Phone -> {
                            val data = checkPhoneField(
                                tag,
                                annotation.value
                            )
                            if (data != null) {
                                throw FieldErrorAnnotationException(
                                    data
                                )
                            }
                        }

                        is Length -> {
                            val data = checkLengthField(
                                tag,
                                annotation.value,
                                annotation.min,
                                annotation.max
                            )
                            if (data != null) {
                                throw FieldErrorAnnotationException(
                                    data
                                )
                            }
                        }

                    }
                }
            }
        }
        return true
    }



    private fun checkIsFieldNotEmpty(
        tag: String
    ):  ResultError? {


        when(val fieldView = formContainerLayout.getFieldViewType(tag)){
            is FormTextInputLayout -> {
                if (formContainerLayout.editTextNotEmpty(tag)) {
                    return ResultError(
                        tag,
                        formContainerLayout.context.getString(R.string.empty_exception)
                    )
                }
            }

            is RadioGroup-> {
                if (fieldView.checkedRadioButtonId == -1)
                    return ResultError(
                        tag,
                        formContainerLayout.context.getString(R.string.empty_exception)
                    )

            }
        }

        return null
    }


    private fun checkIsFielPasswordCorrect(
        tag: String
    ):  ResultError? {

        if (formContainerLayout.editTextNotEmpty(tag)) {
            return ResultError(
                tag,
                formContainerLayout.context.getString(R.string.empty_exception)
            )
        }

        Log.e("aaaaaaaaaa","aaaaaaaaaa")
        val tagConfPwd = "$tag${FormTextInputLayout.suffixPassword}"
        if (formContainerLayout.findIdByTag(tagConfPwd) != -1){
            if (formContainerLayout.editTextNotEmpty(tagConfPwd)) {
                return ResultError(
                    tagConfPwd,
                    formContainerLayout.context.getString(R.string.empty_exception)
                )
            }


            if (formContainerLayout.editTextValue(tagConfPwd) != formContainerLayout.editTextValue(tag)) {
                return ResultError(
                    tagConfPwd,
                    formContainerLayout.context.getString(R.string.pwd_exception)
                )
            }
        }
        return null
    }

    private fun checkIfFieldIsIpV4( tag: String) :  ResultError?{
        when(val fieldView = formContainerLayout.getFieldViewType(tag)){
            is FormTextInputLayout -> {
                if (formContainerLayout.editTextNotEmpty(tag)) {
                    return ResultError(
                        tag,
                        formContainerLayout.context.getString(R.string.empty_exception)
                    )
                }
                val value = formContainerLayout.editTextValue(tag)

                try {

                     value.replace(".","").toLong()
                    if (value.count { it == '.' } != 3 ){
                        return ResultError(
                            tag,
                            formContainerLayout.context.getString(R.string.addressIp)
                        )
                    }
                }catch (e : NumberFormatException){
                    return ResultError(
                        tag,
                        formContainerLayout.context.getString(R.string.addressIp)
                    )
                }

            }
        }
        return null
    }

    private fun checkIsFieldEmail(property: Field,tag : String): ResultError? {
        if (property.type != String::class.java || formContainerLayout.editTextNotEmpty(tag) || !Patterns.EMAIL_ADDRESS.matcher(
                (formContainerLayout.editTextValue(tag) )
            ).matches()
        ) {
            return ResultError(
                tag,
                formContainerLayout.context.getString(R.string.email_exception)
            )
        }
        return null

    }

    private fun checkLengthField(
        tag: String,
        length: Int,
        minLength: Int,
        maxLength: Int
    ): ResultError? {


        val fieldLength= formContainerLayout.editTextValue(tag).length
        if (length > 0) {

            if (fieldLength != length)
                return ResultError(
                    tag,
                    formContainerLayout.context.getString(R.string.length_exception)
                )

        } else {
            if (minLength > 0) {
                if (fieldLength < minLength)
                    return ResultError(
                        tag,
                        formContainerLayout.context.getString(R.string.min_length_exception)
                    )
            }
            if (maxLength > 0) {
                if (fieldLength > maxLength)
                    return ResultError(
                        tag,
                        formContainerLayout.context.getString(R.string.max_length_exception)
                    )
            }
        }


        return null
    }


    private fun checkPhoneField(
        tag: String,
        length: Int
    ): ResultError? {


        val fieldLength= formContainerLayout.editTextValue(tag).length
        if (fieldLength > 0 && fieldLength != length)
            return ResultError(
                tag,
                formContainerLayout.context.getString(R.string.length_exception)
            )

        return null
    }

    private fun isNotValidLength(length: Int): Boolean {
        return (length < 0 && length != UNDEFINED_LENGTH)
    }

}