package com.fund.modules.wallet.model

import com.baomidou.mybatisplus.annotation.*
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
@TableName("app_user_wallet_v2")
class AppUserWalletV2 : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 钱包类型：0-主钱包，1-交易钱包，2-冻结钱包
     */
    @TableField("wallet_type")
    var walletType: Int? = null

    /**
     * 币种代码
     */
    @TableField("currency_code")
    var currencyCode: String? = null

    /**
     * 可用余额
     */
    @TableField("available_balance")
    var availableBalance: BigDecimal? = null

    /**
     * 冻结余额
     */
    @TableField("frozen_balance")
    var frozenBalance: BigDecimal? = null

    /**
     * 总余额（计算字段）
     */
    @TableField("total_balance")
    var totalBalance: BigDecimal? = null

    /**
     * 信誉分 0-100
     */
    @TableField("credit_score")
    var creditScore: Int? = null

    /**
     * 状态：0-禁用，1-正常，2-冻结
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    var version: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

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
