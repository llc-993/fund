package com.fund.modules.financial.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 理财产品
 * </p>
 *
 * @author 书记
 * @since 2025-10-27
 */
@TableName("financial_product")
class FinancialProduct : Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 产品名称(多语言,存json)
     */
    @TableField("product_name")
    var productName: String? = null

    /**
     * 产品代码
     */
    @TableField("product_code")
    var productCode: String? = null

    /**
     * 产品类型(多选,逗号分割)(1-活期 2-定期)
     */
    @TableField("product_type")
    var productType: String? = null

    /**
     * 活期年利率
     */
    @TableField("current_rate")
    var currentRate: BigDecimal? = null

    /**
     * 定期利率(存json)
     */
    @TableField("term_rate")
    var termRate: String? = null

    /**
     * 自申购日起n天计息
     */
    @TableField("interest_day")
    var interestDay: String? = null

    /**
     * 最小申购金额(存json)
     */
    @TableField("min_amount")
    var minAmount: String? = null

    /**
     * 最大申购金额(存json)
     */
    @TableField("max_amount")
    var maxAmount: String? = null

    /**
     * 产品状态(1-可申购 2-下架)
     */
    @TableField("product_status")
    var productStatus: String? = null

    /**
     * 产品说明
     */
    @TableField("product_tip")
    var productTip: String? = null

    /**
     * 排序
     */
    @TableField("sort")
    var sort: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 创建者
     */
    @TableField("create_by")
    var createBy: String? = null

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 修改者
     */
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    override fun toString(): String {
        return "FinancialProduct{" +
        "id=" + id +
        ", productName=" + productName +
        ", productCode=" + productCode +
        ", productType=" + productType +
        ", currentRate=" + currentRate +
        ", termRate=" + termRate +
        ", interestDay=" + interestDay +
        ", minAmount=" + minAmount +
        ", maxAmount=" + maxAmount +
        ", productStatus=" + productStatus +
        ", productTip=" + productTip +
        ", sort=" + sort +
        ", createTime=" + createTime +
        ", createBy=" + createBy +
        ", updateTime=" + updateTime +
        ", updateBy=" + updateBy +
        ", remark=" + remark +
        "}"
    }
}
