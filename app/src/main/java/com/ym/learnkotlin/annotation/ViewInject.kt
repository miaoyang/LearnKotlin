package com.ym.learnkotlin.annotation

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewInject(val value:Int)
