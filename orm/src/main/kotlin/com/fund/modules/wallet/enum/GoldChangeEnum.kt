package com.fund.modules.wallet.enum

import com.fund.common.dto.Label

enum class GoldChangeEnum(var code: Int, var `enumName`: String, var prefix: String) {

    CASH_OUT_REQUEST(-201, "提现申请", "cor"), // 扣除余额，增加冻结余额
    CASH_OUT(-202, "提现", "co"), // 扣除冻结余额
    CASH_OUT_FAIL(202, "提现失败返还", "coc"), // 扣除冻结余额，增加余额
    CASH_IN(888, "充值", "ci"),  // 增加余额

    ADMIN_CHANGE_ADD(1, "后台上分", "add"), // 后台上下分
    ADMIN_CHANGE_SUB(-1, "后台下分", "sub"),

    REG_REWARD(2, "注册奖励", "rr"), // 注册奖励
    PROXY_REBATE(3, "代理佣金", "px"), // 代理佣金
    ADMIN_GIVE_BALANCE(141, "赠送", "gb"), // 赠送
    TASK(666, "交易下单", "tk"),  // 任务扣除
    TASK_CANCEL(777, "交易取消", "tc"), // 交易取消
    TASK_RETURN(699, "交易本金返还", "tn"), // 接单返还
    TASK_INCOME(688, "交易返佣", "te"), // 任务返佣
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