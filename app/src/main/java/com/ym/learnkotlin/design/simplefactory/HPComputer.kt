package com.ym.learnkotlin.design.simplefactory

class HPComputer:Computer() {
    override fun start() {
        println("HP computer")
    }
}