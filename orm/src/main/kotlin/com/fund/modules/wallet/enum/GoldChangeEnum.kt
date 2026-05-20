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

    /** AI量化预约冻结本金（可用减少，量化冻结增加） */
    AI_QUANT_RESERVE_FREEZE(680, "AI量化预约冻结", "aqrf"),

    /** AI量化预约驳回或核定差额解冻（可用增加，量化冻结减少） */
    AI_QUANT_RESERVE_REJECT(681, "AI量化预约解冻", "aqrr"),

    /** AI量化周期完成释放本金（可用增加，量化冻结减少） */
    AI_QUANT_PRINCIPAL_RELEASE(682, "AI量化本金释放", "aqpr"),

    /** AI量化周期盈亏结算（正负均可，作用于可用余额） */
    AI_QUANT_PROFIT_SETTLE(683, "AI量化盈亏结算", "aqps"),

    /** AI量化盈利手续费扣减（从可用余额扣减） */
    AI_QUANT_FEE_DEDUCT(684, "AI量化手续费扣减", "aqfd"),

    /** 积存金买入扣本金（可用减少） */
    GOLD_ACC_BUY(701, "积存金买入", "gab"),

    /** 积存金买入手续费扣减（可用减少） */
    GOLD_ACC_BUY_FEE(702, "积存金买入手续费", "gabf"),

    /** 积存金卖出回款（可用增加，含本金回收与价差） */
    GOLD_ACC_SELL(703, "积存金卖出", "gas"),

    /** 积存金卖出手续费扣减（可用减少） */
    GOLD_ACC_SELL_FEE(704, "积存金卖出手续费", "gasf"),
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