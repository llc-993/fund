package com.fund.modules.financial.model;

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
 * 理财产品信息表
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@Schema(description = "理财产品信息")
@TableName("financial_product")
class FinancialProduct : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "产品ID", example = "1001", nullable = true)
    var id: Long? = null

    /**
     * 产品唯一编码
     */
    @TableField("product_code")
    @Schema(description = "产品唯一编码", example = "FP202511", nullable = true)
    var productCode: String? = null

    /**
     * 产品标题
     */
    @TableField("title")
    @Schema(description = "产品标题", example = "30天稳健理财", nullable = true)
    var title: String? = null

    /**
     * 产品图标
     */
    @TableField("icon_url")
    @Schema(description = "产品展示图标URL", example = "https://cdn.example.com/icon.png", nullable = true)
    var iconUrl: String? = null

    /**
     * 状态：1-上架 0-下架
     */
    @TableField("status")
    @Schema(description = "产品状态（1=上架，0=下架）", example = "1", nullable = true)
    var status: Byte? = null

    /**
     * 周期/天数；活期可为空
     */
    @TableField("days")
    @Schema(description = "产品期限（天），活期可为空", example = "30", nullable = true)
    var days: Int? = null

    /**
     * 利率类型：1-活期 2-固定
     */
    @TableField("rate_type")
    @Schema(description = "利率类型（1=活期，2=固定）", example = "2", nullable = true)
    var rateType: Byte? = null

    /**
     * 默认收益率(例如违约/目标年化)
     */
    @TableField("default_rate")
    @Schema(description = "默认年化收益率", example = "0.12", nullable = true)
    var defaultRate: BigDecimal? = null

    /**
     * 最低收益率或浮动下限
     */
    @TableField("min_rate")
    @Schema(description = "浮动收益最小值或下限", example = "0.1", nullable = true)
    var minRate: BigDecimal? = null

    /**
     * 最高收益率或浮动上限
     */
    @TableField("max_rate")
    @Schema(description = "浮动收益最大值或上限", example = "0.15", nullable = true)
    var maxRate: BigDecimal? = null

    /**
     * 限购类型：0-不限 1-限时 2-限量等业务自定义
     */
    @TableField("time_limit")
    @Schema(description = "限购类型（0=不限，1=限时，2=限量）", example = "0", nullable = true)
    var timeLimit: Byte? = null

    /**
     * 单笔最小申购金额
     */
    @TableField("limit_min_amount")
    @Schema(description = "单笔最小申购金额", example = "100.00", nullable = true)
    var limitMinAmount: BigDecimal? = null

    /**
     * 单笔最大申购金额
     */
    @TableField("limit_max_amount")
    @Schema(description = "单笔最大申购金额", example = "10000.00", nullable = true)
    var limitMaxAmount: BigDecimal? = null

    /**
     * 结算币种
     */
    @TableField("coin")
    @Schema(description = "结算币种", example = "USDT", nullable = true)
    var coin: String? = null

    /**
     * 自定义分类标签，如0-股票型
     */
    @TableField("classify")
    @Schema(description = "产品分类标签", example = "股票型", nullable = true)
    var classify: String? = null

    /**
     * 是否热门：0-否 1-是
     */
    @TableField("is_hot")
    @Schema(description = "是否热门产品（0=否，1=是）", example = "1", nullable = true)
    var isHot: Byte? = null

    /**
     * 排序值，越大越靠前
     */
    @TableField("sort")
    @Schema(description = "排序值，越大越靠前", example = "100", nullable = true)
    var sort: Int? = null

    /**
     * 面向等级/用户组，可选
     */
    @TableField("level")
    @Schema(description = "面向等级/用户组", example = "1", nullable = true)
    var level: Byte? = null

    /**
     * 基础筹资额（平台自有或基准）
     */
    @TableField("basic_invest_amount")
    @Schema(description = "基础筹资额", example = "50000.00", nullable = true)
    var basicInvestAmount: BigDecimal? = null

    /**
     * 目标筹资总额
     */
    @TableField("total_invest_amount")
    @Schema(description = "目标筹资总额", example = "1000000.00", nullable = true)
    var totalInvestAmount: BigDecimal? = null

    /**
     * 已购金额
     */
    @TableField("purchased_amount")
    @Schema(description = "已购金额", example = "250000.00", nullable = true)
    var purchasedAmount: BigDecimal? = null

    /**
     * 剩余可购金额
     */
    @TableField("remain_amount")
    @Schema(description = "剩余可购金额", example = "750000.00", nullable = true)
    var remainAmount: BigDecimal? = null

    /**
     * 当前平均收益率(展示用)
     */
    @TableField("avg_rate")
    @Schema(description = "当前平均展示收益率", example = "0.118", nullable = true)
    var avgRate: BigDecimal? = null

    /**
     * 累计购买人数/笔数
     */
    @TableField("buy_purchase")
    @Schema(description = "累计购买人数/笔数", example = "3521", nullable = true)
    var buyPurchase: Long? = null

    /**
     * 备注/公告
     */
    @TableField("remark")
    @Schema(description = "产品备注或公告", example = "收益按自然日结算", nullable = true)
    var remark: String? = null

    /**
     * 产品介绍
     */
    @TableField("product_intro")
    @Schema(description = "产品介绍内容", example = "该产品主要投资于...", nullable = true)
    var productIntro: String? = null

    /**
     * 问答/风险提示等
     */
    @TableField("faq")
    @Schema(description = "问答或风险提示", example = "Q: 是否允许提前赎回?", nullable = true)
    var faq: String? = null

    /**
     * 平台违约率/风险提示用
     */
    @TableField("platform_risk_rate")
    @Schema(description = "平台违约率/风控指标", example = "0.02", nullable = true)
    var platformRiskRate: BigDecimal? = null

    /**
     * 参考日收益率(非必填)
     */
    @TableField("daily_rate")
    @Schema(description = "参考日收益率", example = "0.0003", nullable = true)
    var dailyRate: BigDecimal? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2025-10-01T10:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2025-10-05T08:30:00", nullable = true)
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
