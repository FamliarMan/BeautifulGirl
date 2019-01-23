package com.jianglei.processor

import com.google.auto.service.AutoService
import com.jianglei.annotation.WebSource
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * @author jianglei on 1/19/19.
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class WebProcessor : AbstractProcessor() {


    private lateinit var mLogger: Logger
    private var elementUtils: Elements? = null
    /**
     * 是否处理过
     */
    private var hasProcess = false

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(WebSource::class.java.canonicalName)
    }

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        elementUtils = p0?.elementUtils
        mLogger = Logger(processingEnv.messager)
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        if (hasProcess) {
            return true
        }
        hasProcess = true
        val allSources = ArrayList<SourceVo>()
        p1?.getElementsAnnotatedWith(WebSource::class.java)
            ?.forEach {
                val anno = it.getAnnotation(WebSource::class.java)
                val enclosingElemnt = it as TypeElement
                val className = enclosingElemnt.asClassName()
                val sourceVo = SourceVo(className, anno.needVpn, anno.index)
                allSources.add(sourceVo)
            }

        val file = createFile(allSources)
        val filePath = File(kaptKotlinGeneratedDir)
        file.writeTo(filePath)
        return true
    }

    private fun createFile(sources: MutableList<SourceVo>): FileSpec {
        sources.sortBy { it.index }
        val webSource = ClassName("com.jianglei.beautifulgirl.data", "WebDataSource")
        val arrayList = ClassName("kotlin.collections", "ArrayList")
        val arrayListOfWebSource = arrayList.parameterizedBy(webSource)
        val normalWebSource = PropertySpec.builder("normalWebSources", arrayListOfWebSource)
            .initializer("%T()", arrayListOfWebSource)
            .build()
        val vpnWebSource = PropertySpec.builder("vpnWebSources", arrayListOfWebSource)
            .initializer("%T()", arrayListOfWebSource)
            .build()
        val name = TypeSpec.objectBuilder("WebSourceCenter")
            .addProperty(normalWebSource)
            .addProperty(vpnWebSource)
            .addInitializerBlock(createInit(sources))
            .addFunction(
                createGetWebSource()
            )
            .build()
        return FileSpec.builder("", "WebSourceCenter")
            .addType(name)
            .build()
    }

    private fun createGetWebSource(): FunSpec {

        val codeBlock = CodeBlock.Builder()
            .add(
                """
        normalWebSources.forEach{
            if(it.id.equals(id)){
                return it
            }
        }
        vpnWebSources.forEach {
            if(it.id.equals(id)){
                return it
            }
        }
        return null
            """.trimMargin()
            )
            .build()

        val webSource = ClassName("com.jianglei.beautifulgirl.data",
            "WebDataSource").copy(nullable = true)
        return FunSpec.builder("getWebSource")
            .addCode(codeBlock)
            .addModifiers(KModifier.PUBLIC)
            .addParameter("id",String::class.asTypeName().copy(nullable = true))
            .returns(webSource)
            .build()
    }

    private fun createInit(sources: MutableList<SourceVo>): CodeBlock {
        val initCodeBlockBuilder = CodeBlock.Builder()
        for (sourceVo in sources) {
            if (sourceVo.needVpn) {
                initCodeBlockBuilder.addStatement("vpnWebSources.add(%T());", sourceVo.className)
            } else {
                initCodeBlockBuilder.addStatement("normalWebSources.add(%T());", sourceVo.className)
            }
        }
        return initCodeBlockBuilder.build()
    }


    class SourceVo(val className: ClassName, val needVpn: Boolean, val index: Int)
}