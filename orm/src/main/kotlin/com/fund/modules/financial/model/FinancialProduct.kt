package com.fund.modules.financial.model

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
 * 理财产品
 * </p>
 *
 * @author 书记
 * @since 2025-10-27
 */
@Schema(description = "理财产品实体")
@TableName("financial_product")
class FinancialProduct : Serializable {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 产品名称(多语言,存json)
     */
    @Schema(description = "产品名称(多语言JSON)")
    @TableField("product_name")
    var productName: String? = null

    /**
     * 产品代码
     */
    @Schema(description = "产品代码")
    @TableField("product_code")
    var productCode: String? = null

    /**
     * 产品类型(多选,逗号分割)(1-活期 2-定期)
     */
    @Schema(description = "产品类型(逗号分隔，1-活期 2-定期)")
    @TableField("product_type")
    var productType: String? = null

    /**
     * 活期年利率
     */
    @Schema(description = "活期年利率")
    @TableField("current_rate")
    var currentRate: BigDecimal? = null

    /**
     * 定期利率(存json)
     */
    @Schema(description = "定期利率(多语言JSON)")
    @TableField("term_rate")
    var termRate: String? = null

    /**
     * 自申购日起n天计息
     */
    @Schema(description = "自申购日起N天计息")
    @TableField("interest_day")
    var interestDay: String? = null

    /**
     * 最小申购金额(存json)
     */
    @Schema(description = "最小申购金额(多语言JSON)")
    @TableField("min_amount")
    var minAmount: String? = null

    /**
     * 最大申购金额(存json)
     */
    @Schema(description = "最大申购金额(多语言JSON)")
    @TableField("max_amount")
    var maxAmount: String? = null

    /**
     * 产品状态(1-可申购 2-下架)
     */
    @Schema(description = "产品状态(1-可申购 2-下架)")
    @TableField("product_status")
    var productStatus: String? = null

    /**
     * 产品说明
     */
    @Schema(description = "产品说明")
    @TableField("product_tip")
    var productTip: String? = null

    /**
     * 排序
     */
    @Schema(description = "排序值，越大越靠前")
    @TableField("sort")
    var sort: Int? = null

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @TableField("create_by")
    var createBy: String? = null

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 修改者
     */
    @Schema(description = "修改者")
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    var remark: String? = null

    override fun toString(): String {
        return "FinancialProduct(id=$id, productName=$productName, productCode=$productCode, productType=$productType, currentRate=$currentRate, termRate=$termRate, interestDay=$interestDay, minAmount=$minAmount, maxAmount=$maxAmount, productStatus=$productStatus, productTip=$productTip, sort=$sort, createTime=$createTime, createBy=$createBy, updateTime=$updateTime, updateBy=$updateBy, remark=$remark)"
    }

}
