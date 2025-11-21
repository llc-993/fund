package com.fund.modules.wallet.enum

import com.fund.common.dto.Label

enum class GoldChangeEnum(var code: Int, var `enumName`: String, var prefix: String) {

    CASH_OUT_REQUEST(-201, "提现申请", "cor"), // 扣除余额，增加冻结余额
    CASH_OUT(-202, "提现", "co"), // 扣除冻结余额
    CASH_OUT_FAIL(202, "提现失败返还", "coc"), // 扣除冻结余额，增加余额
    CASH_IN(888, "充值", "ci"),  // 增加余额

    ADMIN_CHANGE_ADD(1, "后台上分", "add"), // 后台上下分
    ADMIN_CHANGE_SUB(-1, "后台下分", "sub"),

    BUY(666, "交易下单", "buy"),  // 交易下单
    SELL(667, "平仓","sell"),

    // IPO相关
    IPO_CONVERSION(667, "IPO转持仓", "ic"), // IPO转持仓

    // 大宗交易相关
    BLOCK_TRADE_CONVERSION(668, "大宗交易转持仓", "btc"), // 大宗交易转持仓

    // 涨跌板块相关
    RISING_FALLING_SECTORS_CONVERSION(669, "涨跌板块转持仓", "rfsc"), // 伦敦板块转持仓

    // 理财相关
    FINANCIAL_PURCHASE(670, "理财申购", "fp"), // 理财申购
    FINANCIAL_INTEREST(671, "理财收益", "fi"), // 理财收益
    FINANCIAL_EXPIRE(672, "理财到期", "fe"), // 理财到期
    FINANCIAL_REDEEM(673, "理财赎回", "fr"), // 理财赎回
    FINANCIAL_FORCE_REDEEM(674, "理财强制赎回", "ffr"), // 理财强制赎回
    ;

    companion object {
        fun toLabel(): List<Label<Int, String>> {
            return GoldChangeEnum.entries.map { c: GoldChangeEnum ->
                Label(
                    c.code,
                    c.enumName
                )
            }
                .toList()
        }
    }
}