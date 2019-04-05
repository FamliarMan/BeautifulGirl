package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.RuleKeyWord

/**
 * @author jianglei on 4/4/19.
 */
class HandlerFactroy {
    companion object {
        fun create(rule: String): AbstractRuleHandler {
            val index = rule.indexOf(":")
            var trimRule:String
            if(index == -1){
                trimRule = rule
            }else{
                trimRule = rule.substring(0,index).trim()
            }
            return when {
                trimRule.equals(RuleKeyWord.CLASS) -> ClassHandler(rule.trim())
                trimRule.equals(RuleKeyWord.ID) -> IdHandler(rule.trim())
                trimRule.equals(RuleKeyWord.LABEL) -> LabelHandler(rule.trim())
                trimRule.equals(RuleKeyWord.REGX) -> RegexHandler(rule.trim())
                trimRule.equals(RuleKeyWord.PROPERTY) -> PropertyHandler(rule.trim())
                trimRule.equals(RuleKeyWord.TEXT) -> TextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.HAS_CLASS) -> HasClassHandler(rule.trim())
                trimRule.equals(RuleKeyWord.HAS_ID) -> HasIdHandler(rule.trim())
                trimRule.equals(RuleKeyWord.NO_CLASS) -> NoClassHandler(rule.trim())
                trimRule.equals(RuleKeyWord.NO_ID) -> NoIdHandler(rule.trim())
                trimRule.equals(RuleKeyWord.HAS_LABEL) -> HasLabelHandler(rule.trim())
                trimRule.equals(RuleKeyWord.NO_LABEL) -> NoLabelHandler(rule.trim())
                trimRule.equals(RuleKeyWord.EQUALS) -> EqualTextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.NOT_EQUAL) -> NotEqualTextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.HAS_TEXT) -> HasTextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.NO_TEXT) -> NoTextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.JSON_ARR) -> JsonArrHandler(rule.trim())
                trimRule.equals(RuleKeyWord.JSON_OBJ) -> JsonObjHandler(rule.trim())
                trimRule.equals(RuleKeyWord.JSON_VALUE) -> JsonValueHandler(rule.trim())
                trimRule.equals(RuleKeyWord.CONTAIN_TEXT) -> ContainTextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.NOT_CONTAIN_TEXT) -> NotContainTextHandler(rule.trim())
                trimRule.equals(RuleKeyWord.REGEX_ALL) -> RegexAllHandler(rule.trim())
                else -> throw IllegalSyntaxException("非法规则：$rule")
            }
        }
    }
}