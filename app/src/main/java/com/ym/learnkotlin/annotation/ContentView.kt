package com.ym.learnkotlin.annotation


/**
 *  @MustBeDocumented 指定 ContentView 注解信息可以被文档生成工具读取。
 *  @Target(AnnotationTarget.CLASS) 指定 ContentView 注解用于修饰类 和 接口等类型。
 *  @Retention(AnnotationRetention.RUNTIME) 指定 ContentView 注解信息可以在运行时被读取。
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ContentView(val value:Int)
