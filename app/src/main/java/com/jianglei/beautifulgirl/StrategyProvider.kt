package com.jianglei.beautifulgirl

import com.jianglei.beautifulgirl.rule.WebStrategy

/**
 * @author jianglei on 3/23/19.
 */
class StrategyProvider {
    companion object {
        private var webStrategy: WebStrategy? = null

        fun updateCurStrategy(webStrategy: WebStrategy) {
            if (StrategyProvider.webStrategy != null) {
                StrategyProvider.webStrategy!!.cancel()
            }
            StrategyProvider.webStrategy = webStrategy
        }

        fun getCurStrategy(): WebStrategy? {
            return webStrategy
        }
    }
}