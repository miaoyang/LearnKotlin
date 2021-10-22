package com.ym.learnkotlin.design.factorymodel

abstract class ComputerFactory {
    abstract fun <T> createComputer(clz:Class<T>):T
}