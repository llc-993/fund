package com.fund.modules.wallet.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户提现订单表
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
@Schema(description = "用户提现订单信息")
@TableName("app_user_cash_out_order")
class AppUserCashOutOrder : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "订单主键ID", example = "1001", nullable = true)
    var id: Long? = null

    /**
     * 用户id
     */
    @TableField("user_id")
    @Schema(description = "关联的用户ID", example = "20001", nullable = true)
    var userId: Long? = null

    /**
     * 正常 0 假人 1
     */
    @TableField("user_group")
    @Schema(description = "用户分组标识（0=正常用户，1=虚拟用户）", example = "0", nullable = true)
    var userGroup: Int? = null

    /**
     * 用户名
     */
    @TableField("user_account")
    @Schema(description = "用户账号/登录名", example = "jerry123", nullable = true)
    var userAccount: String? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    @Schema(description = "上级代理用户ID", example = "30001", nullable = true)
    var topUserId: Long? = null

    /**
     * ip
     */
    @TableField("ip")
    @Schema(description = "提现请求发起IP地址", example = "192.168.1.10", nullable = true)
    var ip: String? = null

    /**
     * 订单编号
     */
    @TableField("order_no")
    @Schema(description = "提现订单编号", example = "CO202310120001", nullable = true)
    var orderNo: String? = null

    /**
     * 申请时间
     */
    @TableField("apply_time")
    @Schema(description = "提现申请时间", example = "2025-10-18T10:15:00", nullable = true)
    var applyTime: LocalDateTime? = null

    /**
     * 申请提现金额
     */
    @TableField("apply_amount")
    @Schema(description = "用户申请提现金额", example = "100.50", nullable = true)
    var applyAmount: BigDecimal? = null

    /**
     * 实际打款金额
     */
    @TableField("actual_amount")
    @Schema(description = "平台实际打款金额（扣除手续费后）", example = "95.50", nullable = true)
    var actualAmount: BigDecimal? = null

    /**
     * 手续费
     */
    @TableField("fee")
    @Schema(description = "提现手续费金额", example = "5.00", nullable = true)
    var fee: BigDecimal? = null

    /**
     * 真实姓名
     */
    @TableField("full_name")
    @Schema(description = "用户实名信息", example = "张三", nullable = true)
    var fullName: String? = null

    /**
     * 提现网络 Tron Eth
     */
    @TableField("net_work")
    @Schema(description = "提现网络或链路，例如 Tron、ETH", example = "TRON", nullable = true)
    var netWork: String? = null

    /**
     * 钱包地址
     */
    @TableField("address")
    @Schema(description = "提现到账钱包地址", example = "TXYZ1234567890", nullable = true)
    var address: String? = null

    /**
     * 提现手机号
     */
    @TableField("mobile_phone")
    @Schema(description = "用户绑定的提现手机号", example = "+8613712345678", nullable = true)
    var mobilePhone: String? = null

    /**
     * 用户备注
     */
    @TableField("remark")
    @Schema(description = "用户提交的备注信息", example = "请尽快处理", nullable = true)
    var remark: String? = null

    /**
     * 审核时间
     */
    @TableField("remit_time")
    @Schema(description = "订单审核/打款时间", example = "2025-10-18T11:00:00", nullable = true)
    var remitTime: LocalDateTime? = null

    /**
     * 订单类型 1待处理 2已锁定 3已取消 4已拒绝 5已成功
     */
    @TableField("cash_status")
    @Schema(description = "提现订单状态（1=待处理，2=已锁定，3=已取消，4=已拒绝，5=已成功）", example = "1", nullable = true)
    var cashStatus: Int? = null

    /**
     * 失败原因,如果有
     */
    @TableField("reason")
    @Schema(description = "失败原因说明", example = "账户信息不完整", nullable = true)
    var reason: String? = null

    /**
     * hash,如果有
     */
    @TableField("hash")
    @Schema(description = "区块链交易哈希", example = "0xabc123...", nullable = true)
    var hash: String? = null

    /**
     * 订单处理--操作人账号
     */
    @TableField("operator_user")
    @Schema(description = "后台处理该订单的管理员账号", example = "admin01", nullable = true)
    var operatorUser: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "订单最后更新时间", example = "2025-10-18T12:00:00", nullable = true)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppUserCashOutOrder{" +
        "id=" + id +
        ", userId=" + userId +
        ", userGroup=" + userGroup +
        ", userAccount=" + userAccount +
        ", topUserId=" + topUserId +
        ", ip=" + ip +
        ", orderNo=" + orderNo +
        ", applyTime=" + applyTime +
        ", applyAmount=" + applyAmount +
        ", actualAmount=" + actualAmount +
        ", fee=" + fee +
        ", fullName=" + fullName +
        ", netWork=" + netWork +
        ", address=" + address +
        ", mobilePhone=" + mobilePhone +
        ", remark=" + remark +
        ", remitTime=" + remitTime +
        ", cashStatus=" + cashStatus +
        ", reason=" + reason +
        ", hash=" + hash +
        ", operatorUser=" + operatorUser +
        ", updateTime=" + updateTime +
        "}"
    }
}
