package com.jianglei.processor

import com.google.auto.service.AutoService
import com.jianglei.annotation.WebSource
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * @author jianglei on 1/19/19.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Process::class)
class WebProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(WebSource::class.java.canonicalName)
    }
    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        println("ni hao --------------------------------")
//        p1?.getElementsAnnotatedWith(WebSource::class.java)
//            ?.forEach {
//
//            }
        val webSource = ClassName("com.jianglei.beautifulgirl.data", "DataSource")
        val arrayList = ClassName("kotlin.collections", "ArrayList")
        val arrayListOfWebSource = arrayList.parameterizedBy(webSource)
        val normalWebSource = PropertySpec.builder("normalWebSources", MutableList::class)
            .initializer("%T()", arrayListOfWebSource)
            .build()
        val vpnWebSource = PropertySpec.builder("vpnWebSources", MutableList::class)
            .initializer("%T()", arrayListOfWebSource)
            .build()
        val initFun = FunSpec.builder("init").build()
        val name = TypeSpec.objectBuilder("WebSourceCenter")
            .addProperty(normalWebSource)
            .addProperty(vpnWebSource)
            .addFunction(initFun)
            .build()
        val file = FileSpec.builder("", "WebSourceCenter")
            .addType(name)
            .build()
        println("文本process")
        val filePath = File("$KAPT_KOTLIN_GENERATED_OPTION_NAME.name").apply { mkdir() }
        file.writeTo(filePath)
        return false
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}