package com.medianet.tools.form

import java.lang.Exception

const val UNDEFINED_LENGTH = -438

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class EmbendedError


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class NotEmpty

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Email

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Password


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Phone(val value : Int= 8)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Length(val value : Int= UNDEFINED_LENGTH, val min : Int = UNDEFINED_LENGTH, val max : Int= UNDEFINED_LENGTH)


interface FormResultListener<T>{

    fun formResultSuccessListener(result : T)

    fun formResultErrorListener(msg : String , id : Int)

}

sealed class ResultStatus
data class ResultError(val field : String, val msg: String) : ResultStatus()
class ResultSuccess : ResultStatus()

data class FieldDevelopperErrorAnnotationException(val msg : String) : Exception(msg)

data class FieldErrorAnnotationException(val data : ResultError) : Exception()