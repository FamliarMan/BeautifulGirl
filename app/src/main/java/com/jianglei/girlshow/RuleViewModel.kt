package com.jianglei.girlshow

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.jianglei.girlshow.rule.WebRule
import com.jianglei.girlshow.storage.DataStorage
import com.jianglei.girlshow.storage.RuleRecord
import org.jetbrains.anko.doAsync
import utils.JsonUtils

/**
 * 管理所有的
 *@author longyi created on 19-3-28
 */
class RuleViewModel : ViewModel() {
    private lateinit var ruleData: LiveData<List<WebRule>>
    private lateinit var ruleRecordsData: LiveData<List<RuleRecord>>

    init {
        getRules()
    }

    fun getRuleData(): LiveData<List<WebRule>> {
        return ruleData
    }

    fun getRuleRecodeData(): LiveData<List<RuleRecord>> {
        return ruleRecordsData
    }

    private fun getRules() {
        ruleRecordsData = DataStorage.db.ruleDao().getAllRules()
        ruleData = Transformations.map(ruleRecordsData, object : Function<List<RuleRecord>, List<WebRule>> {
            override fun apply(t: List<RuleRecord>): List<WebRule> {
                return t.map {
                    JsonUtils.parseJsonWithGson(it.rule, WebRule::class.java)!!
                }

            }
        }) as MutableLiveData<List<WebRule>>

    }

    fun updateRule(rule: RuleRecord) {
        doAsync {
            DataStorage.db.ruleDao().updateRule(rule)
        }
    }

    fun deleteRule(rule: RuleRecord) {
        doAsync {
            DataStorage.db.ruleDao().deleteRule(rule)
        }
    }

    fun addRule(rule: RuleRecord) {
        doAsync {
            DataStorage.db.ruleDao().addRule(rule)
        }
    }

}