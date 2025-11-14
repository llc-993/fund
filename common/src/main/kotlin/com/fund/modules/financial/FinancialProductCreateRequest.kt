package com.fund.modules.financial

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/**
 * 理财产品创建请求
 */
@Schema(description = "理财产品创建请求")
data class FinancialProductCreateRequest(
    @Schema(description = "产品唯一编码", required = true, example = "FP20251110001")
    val productCode: String,
    
    @Schema(description = "产品标题", required = true, example = "稳健理财产品")
    val title: String,
    
    @Schema(description = "产品图标", example = "https://example.com/icon.png")
    val iconUrl: String? = null,
    
    @Schema(description = "周期/天数（活期可为空）", example = "30")
    val days: Int? = null,
    
    @Schema(description = "利率类型：1-活期 2-固定", example = "2")
    val rateType: Byte = 2,
    
    @Schema(description = "默认收益率(例如违约/目标年化)", example = "5.5")
    val defaultRate: BigDecimal? = null,
    
    @Schema(description = "最低收益率或浮动下限", example = "4.5")
    val minRate: BigDecimal? = null,
    
    @Schema(description = "最高收益率或浮动上限", example = "6.5")
    val maxRate: BigDecimal? = null,
    
    @Schema(description = "限购类型：0-不限 1-限时 2-限量", example = "0")
    val timeLimit: Byte = 0,
    
    @Schema(description = "单笔最小申购金额", example = "1000")
    val limitMinAmount: BigDecimal? = null,
    
    @Schema(description = "单笔最大申购金额", example = "100000")
    val limitMaxAmount: BigDecimal? = null,
    
    @Schema(description = "结算币种", example = "USD")
    val coin: String = "USD",
    
    @Schema(description = "自定义分类标签", example = "0")
    val classify: String? = null,
    
    @Schema(description = "是否热门：0-否 1-是", example = "0")
    val isHot: Byte = 0,
    
    @Schema(description = "排序值，越大越靠前", example = "100")
    val sort: Int? = null,
    
    @Schema(description = "面向等级/用户组", example = "0")
    val level: Byte = 0,
    
    @Schema(description = "基础筹资额（平台自有或基准）", example = "1000000")
    val basicInvestAmount: BigDecimal? = null,
    
    @Schema(description = "目标筹资总额", example = "5000000")
    val totalInvestAmount: BigDecimal? = null,
    
    @Schema(description = "平台违约率/风险提示用", example = "0.5")
    val platformRiskRate: BigDecimal? = null,
    
    @Schema(description = "参考日收益率", example = "0.015")
    val dailyRate: BigDecimal? = null,
    
    @Schema(description = "产品介绍", example = "这是一款稳健型理财产品，适合保守型投资者...")
    val productIntro: String? = null,
    
    @Schema(description = "问答/风险提示等", example = "Q: 如何计算收益？A: 收益=投资金额*年化收益率/365*投资天数")
    val faq: String? = null,
    
    @Schema(description = "备注/公告", example = "新客专享")
    val remark: String? = null
)
