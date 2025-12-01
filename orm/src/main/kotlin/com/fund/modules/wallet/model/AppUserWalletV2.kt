package com.fund.modules.wallet.model

import com.baomidou.mybatisplus.annotation.*
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户钱包表V2
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Schema(description = "用户钱包信息")
@TableName("app_user_wallet")
class AppUserWalletV2 : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "钱包主键ID", example = "30001", nullable = true)
    var id: Long? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "关联用户ID", example = "10001", nullable = true)
    var userId: Long? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    @Schema(description = "上级代理用户ID", example = "20001", nullable = true)
    var topUserId: Long? = null

    /**
     * 钱包类型：0-主钱包，1-交易钱包，2-冻结钱包
     */
    @TableField("wallet_type")
    @Schema(description = "钱包类型（0=主钱包，1=交易钱包，2=冻结钱包）", example = "0", nullable = true)
    var walletType: Int? = null

    /**
     * 币种代码
     */
    @TableField("currency_code")
    @Schema(description = "钱包对应币种代码", example = "USDT", nullable = true)
    var currencyCode: String? = null

    /**
     * 可用余额
     */
    @TableField("available_balance")
    @Schema(description = "可用余额", example = "1200.50", nullable = true)
    var availableBalance: BigDecimal? = null

    /**
     * 冻结余额
     */
    @TableField("frozen_balance")
    @Schema(description = "冻结余额", example = "300.00", nullable = true)
    var frozenBalance: BigDecimal? = null

    /**
     * 总余额（数据库计算字段，不参与插入和更新）
     */
    @TableField(value = "total_balance", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @Schema(description = "总余额（可用 + 冻结）", example = "1500.50", nullable = true)
    var totalBalance: BigDecimal? = null

    /**
     * 信誉分 0-100
     */
    @TableField("credit_score")
    @Schema(description = "信誉分（0-100）", example = "80", nullable = true)
    var creditScore: Int? = null

    /**
     * 状态：0-禁用，1-正常，2-冻结
     */
    @TableField("status")
    @Schema(description = "钱包状态（0=禁用，1=正常，2=冻结）", example = "1", nullable = true)
    var status: Int? = null

    /**
     * 版本号（乐观锁）
     */
    @JsonIgnore
    @TableField("version", update ="%s+1" )
    @Schema(description = "版本号（用于乐观锁）", example = "5", nullable = true)
    var version: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2025-01-27T12:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "最近更新时间", example = "2025-10-18T10:30:00", nullable = true)
    var updateTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remark")
    @Schema(description = "备注信息", example = "系统自动创建", nullable = true)
    var remark: String? = null

    @TableField(exist = false)
    var flag: String? = null

    override fun toString(): String {
        return "AppUserWalletV2{" +
                "id=" + id +
                ", userId=" + userId +
                ", topUserId=" + topUserId +
                ", walletType=" + walletType +
                ", currencyCode='" + currencyCode + '\'' +
                ", availableBalance=" + availableBalance +
                ", frozenBalance=" + frozenBalance +
                ", totalBalance=" + totalBalance +
                ", creditScore=" + creditScore +
                ", status=" + status +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", remark='" + remark + '\'' +
                '}'
    }
}
