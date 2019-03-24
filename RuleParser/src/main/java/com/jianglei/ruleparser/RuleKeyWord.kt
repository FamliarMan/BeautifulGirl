package com.jianglei.ruleparser

/**
 * @author jianglei on 3/16/19.
 */
class RuleKeyWord {
    companion object {

        const val CLASS = "@class"
        const val ID = "@id"
        const val LABEL = "@label"
        const val REGX = "@regex"
        const val PROPERTY = "@property"
        const val TEXT = "@text"

        const val HAS_CLASS = "@hasClass"
        const val NO_CLASS = "@noClass"
        const val HAS_ID = "@hasId"
        const val NO_ID = "@noId"
        const val HAS_LABEL = "@hasLabel"
        const val NO_LABEL = "@noLabel"
        const val EQUALS = "@=="
        const val NOT_EQUAL = "@!="
        const val HAS_TEXT = "@hasText"

        const val JSON_ARR ="@jsonArr"
        const val JSON_OBJ = "@jsonObj"
        const val JSON_VALUE = "@jsonValue"

        //代表当前页面
        const val BASE_URL = "{baseUrl}"
        //页码占位
        const val PAGE = "{page}"
        //搜索占位
        const val SEARCH_TXT = "{searchTxt}"

    }
}