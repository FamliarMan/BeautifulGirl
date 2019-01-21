package com.jianglei.processor

import com.google.auto.service.AutoService
import com.jianglei.annotation.WebSource
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * @author jianglei on 1/19/19.
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class WebProcessor : AbstractProcessor() {


    private lateinit var mLogger: Logger
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(WebSource::class.java.canonicalName)
    }

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        mLogger = Logger(processingEnv.messager)
        mLogger.warning("init --------------------------------")
        println("init second --------------------------------")
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        print("ni hao --------------------------------")
//        p1?.getElementsAnnotatedWith(WebSource::class.java)
//            ?.forEach {
//
//            }
        val webSource = ClassName("com.jianglei.beautifulgirl.data", "DataSource")
        val arrayList = ClassName("kotlin.collections", "ArrayList")
        val arrayListOfWebSource = arrayList.parameterizedBy(webSource)
        val normalWebSource = PropertySpec.builder("normalWebSources", arrayListOfWebSource)
            .initializer("%T()", arrayListOfWebSource)
            .build()
        val vpnWebSource = PropertySpec.builder("vpnWebSources", arrayListOfWebSource)
            .initializer("%T()", arrayListOfWebSource)
            .build()
        val initFun = FunSpec.builder("init").build()
        val name = TypeSpec.objectBuilder("WebSourceCenter")
            .addProperty(normalWebSource)
            .addProperty(vpnWebSource)
            .addInitializerBlock()
            .addFunction(initFun)
            .build()
        val file = FileSpec.builder("", "WebSourceCenter")
            .addType(name)
            .build()
        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val filePath = File(kaptKotlinGeneratedDir)
        file.writeTo(filePath)
        mLogger.warning("exist:" + filePath.exists() + "-------------")
        return true
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}