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
 * 理财产品信息表
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@TableName("financial_product")
class FinancialProduct : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 产品唯一编码
     */
    @TableField("product_code")
    var productCode: String? = null

    /**
     * 产品标题
     */
    @TableField("title")
    var title: String? = null

    /**
     * 产品图标
     */
    @TableField("icon_url")
    var iconUrl: String? = null

    /**
     * 状态：1-上架 0-下架
     */
    @TableField("status")
    var status: Byte? = null

    /**
     * 周期/天数；活期可为空
     */
    @TableField("days")
    var days: Int? = null

    /**
     * 利率类型：1-活期 2-固定
     */
    @TableField("rate_type")
    var rateType: Byte? = null

    /**
     * 默认收益率(例如违约/目标年化)
     */
    @TableField("default_rate")
    var defaultRate: BigDecimal? = null

    /**
     * 最低收益率或浮动下限
     */
    @TableField("min_rate")
    var minRate: BigDecimal? = null

    /**
     * 最高收益率或浮动上限
     */
    @TableField("max_rate")
    var maxRate: BigDecimal? = null

    /**
     * 限购类型：0-不限 1-限时 2-限量等业务自定义
     */
    @TableField("time_limit")
    var timeLimit: Byte? = null

    /**
     * 单笔最小申购金额
     */
    @TableField("limit_min_amount")
    var limitMinAmount: BigDecimal? = null

    /**
     * 单笔最大申购金额
     */
    @TableField("limit_max_amount")
    var limitMaxAmount: BigDecimal? = null

    /**
     * 结算币种
     */
    @TableField("coin")
    var coin: String? = null

    /**
     * 自定义分类标签，如0-股票型
     */
    @TableField("classify")
    var classify: String? = null

    /**
     * 是否热门：0-否 1-是
     */
    @TableField("is_hot")
    var isHot: Byte? = null

    /**
     * 排序值，越大越靠前
     */
    @TableField("sort")
    var sort: Int? = null

    /**
     * 面向等级/用户组，可选
     */
    @TableField("level")
    var level: Byte? = null

    /**
     * 基础筹资额（平台自有或基准）
     */
    @TableField("basic_invest_amount")
    var basicInvestAmount: BigDecimal? = null

    /**
     * 目标筹资总额
     */
    @TableField("total_invest_amount")
    var totalInvestAmount: BigDecimal? = null

    /**
     * 已购金额
     */
    @TableField("purchased_amount")
    var purchasedAmount: BigDecimal? = null

    /**
     * 剩余可购金额
     */
    @TableField("remain_amount")
    var remainAmount: BigDecimal? = null

    /**
     * 当前平均收益率(展示用)
     */
    @TableField("avg_rate")
    var avgRate: BigDecimal? = null

    /**
     * 累计购买人数/笔数
     */
    @TableField("buy_purchase")
    var buyPurchase: Long? = null

    /**
     * 备注/公告
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 产品介绍
     */
    @TableField("product_intro")
    var productIntro: String? = null

    /**
     * 问答/风险提示等
     */
    @TableField("faq")
    var faq: String? = null

    /**
     * 平台违约率/风险提示用
     */
    @TableField("platform_risk_rate")
    var platformRiskRate: BigDecimal? = null

    /**
     * 参考日收益率(非必填)
     */
    @TableField("daily_rate")
    var dailyRate: BigDecimal? = null

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

    override fun toString(): String {
        return "FinancialProduct{" +
        "id=" + id +
        ", productCode=" + productCode +
        ", title=" + title +
        ", iconUrl=" + iconUrl +
        ", status=" + status +
        ", days=" + days +
        ", rateType=" + rateType +
        ", defaultRate=" + defaultRate +
        ", minRate=" + minRate +
        ", maxRate=" + maxRate +
        ", timeLimit=" + timeLimit +
        ", limitMinAmount=" + limitMinAmount +
        ", limitMaxAmount=" + limitMaxAmount +
        ", coin=" + coin +
        ", classify=" + classify +
        ", isHot=" + isHot +
        ", sort=" + sort +
        ", level=" + level +
        ", basicInvestAmount=" + basicInvestAmount +
        ", totalInvestAmount=" + totalInvestAmount +
        ", purchasedAmount=" + purchasedAmount +
        ", remainAmount=" + remainAmount +
        ", avgRate=" + avgRate +
        ", buyPurchase=" + buyPurchase +
        ", remark=" + remark +
        ", productIntro=" + productIntro +
        ", faq=" + faq +
        ", platformRiskRate=" + platformRiskRate +
        ", dailyRate=" + dailyRate +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}"
    }
}
