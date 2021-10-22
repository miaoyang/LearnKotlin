package com.ym.learnkotlin

class LearnMap {
    fun main(){
        testMap()
    }

    fun testMap(){
        var nums = listOf<String>("a","b","c","d")
        nums.filter { it.startsWith("a") }
            .sortedBy { it }
            .map { it.toUpperCase() }
            .forEach{ println(it)}
    }
}