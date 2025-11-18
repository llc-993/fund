package com.fund.modules.wallet.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Schema(description = "用户提现订单视图对象")
class AppUserCashOutOrderVO : Serializable {

    @Schema(description = "订单主键ID", example = "1001", nullable = true)
    var id: Long? = null

    @Schema(description = "关联的用户ID", example = "20001", nullable = true)
    var userId: Long? = null

    @Schema(description = "用户分组标识（0=正常用户，1=虚拟用户）", example = "0", nullable = true)
    var userGroup: Int? = null

    @Schema(description = "用户账号/登录名", example = "jerry123", nullable = true)
    var userAccount: String? = null

    @Schema(description = "上级代理用户ID", example = "30001", nullable = true)
    var topUserId: Long? = null

    @Schema(description = "提现请求发起IP地址", example = "192.168.1.10", nullable = true)
    var ip: String? = null

    @Schema(description = "提现订单编号", example = "CO202310120001", nullable = true)
    var orderNo: String? = null

    @Schema(description = "提现申请时间", example = "2025-10-18T10:15:00", nullable = true)
    var applyTime: LocalDateTime? = null

    @Schema(description = "用户申请提现金额", example = "100.50", nullable = true)
    var applyAmount: BigDecimal? = null

    @Schema(description = "平台实际打款金额（扣除手续费后）", example = "95.50", nullable = true)
    var actualAmount: BigDecimal? = null

    @Schema(description = "提现手续费金额", example = "5.00", nullable = true)
    var fee: BigDecimal? = null

    @Schema(description = "用户实名信息", example = "张三", nullable = true)
    var fullName: String? = null

    @Schema(description = "提现网络或链路，例如 Tron、ETH", example = "TRON", nullable = true)
    var netWork: String? = null

    @Schema(description = "提现到账钱包地址", example = "TXYZ1234567890", nullable = true)
    var address: String? = null

    @Schema(description = "用户绑定的提现手机号", example = "+8613712345678", nullable = true)
    var mobilePhone: String? = null

    @Schema(description = "用户提交的备注信息", example = "请尽快处理", nullable = true)
    var remark: String? = null

    @Schema(description = "订单审核/打款时间", example = "2025-10-18T11:00:00", nullable = true)
    var remitTime: LocalDateTime? = null

    @Schema(description = "提现订单状态（1=待处理，2=已锁定，3=已取消，4=已拒绝，5=已成功）", example = "1", nullable = true)
    var cashStatus: Int? = null

    @Schema(description = "失败原因说明", example = "账户信息不完整", nullable = true)
    var reason: String? = null

    @Schema(description = "区块链交易哈希", example = "0xabc123...", nullable = true)
    var hash: String? = null

    @Schema(description = "后台处理该订单的管理员账号", example = "admin01", nullable = true)
    var operatorUser: String? = null

    @Schema(description = "订单最后更新时间", example = "2025-10-18T12:00:00", nullable = true)
    var updateTime: LocalDateTime? = null

    // 扩展字段
    @Schema(description = "一级代理用户ID", example = "40001", nullable = true)
    var parentId: Long? = null

    @Schema(description = "一级代理账号", example = "agent001", nullable = true)
    var parentAccount: String? = null

    @Schema(description = "一级代理手机号", example = "+8613712345678", nullable = true)
    var parentMobilePhone: String? = null

    @Schema(description = "用户注册手机号", example = "+8613712345678", nullable = true)
    var registerPhone: String? = null

    @Schema(description = "今日提现次数", example = "3", nullable = true)
    var cashOutCountToday: Long? = null

    @Schema(description = "今日订单数", example = "10", nullable = true)
    var idx: Long? = null
}

