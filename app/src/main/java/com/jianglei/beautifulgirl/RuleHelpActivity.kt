package com.jianglei.beautifulgirl

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_rule_help.*

class RuleHelpActivity :BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule_help)
        wvHelp.loadUrl("file:////android_asset/rule.html")
    }
}
