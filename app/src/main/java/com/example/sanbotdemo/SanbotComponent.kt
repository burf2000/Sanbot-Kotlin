package com.example.sanbotdemo

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@SanbotScope
@Component
abstract class SanbotComponent {
    abstract val sanbot: Sanbot

    protected val SanbotImpl.bind: Sanbot
        @Provides get() = this

    companion object
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class SanbotScope