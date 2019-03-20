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

        const val JSON_ARR ="@jsonArr"
        const val JSON_OBJ = "@jsonObj"
        const val JSON_VALUE = "@jsonValue"

    }
}