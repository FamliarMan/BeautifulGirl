package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.RuleKeyWord

/**
 * @author jianglei on 4/4/19.
 */
class HandlerFactroy {
    companion object {
        fun create(rule:String):AbstractRuleHandler{
            val trimRule = rule.trim()
            return when{
                trimRule.startsWith(RuleKeyWord.CLASS)-> ClassHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.ID)-> IdHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.LABEL)-> LabelHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.REGX)-> RegexHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.PROPERTY)-> PropertyHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.TEXT)-> TextHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.HAS_CLASS)-> HasClassHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.HAS_ID)-> HasIdHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.NO_CLASS)-> NoClassHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.NO_ID)-> NoIdHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.HAS_LABEL)-> HasLabelHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.NO_LABEL)-> NoLabelHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.EQUALS)-> EqualTextHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.NOT_EQUAL)-> NotEqualTextHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.HAS_TEXT)-> HasTextHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.NO_TEXT)-> NoTextHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.JSON_ARR)-> JsonArrHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.JSON_OBJ)-> JsonObjHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.JSON_VALUE)-> JsonValueHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.CONTAIN_TEXT)->ContainTextHandler(trimRule)
                trimRule.startsWith(RuleKeyWord.NOT_CONTAIN_TEXT) ->NotContainTextHandler(trimRule)
                else-> throw IllegalSyntaxException("非法规则：$rule")
            }
        }
    }
}