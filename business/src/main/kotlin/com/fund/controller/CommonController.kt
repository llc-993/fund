package com.fund.controller

import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.HeaderConstants
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.banner.model.AppBanner
import com.fund.modules.banner.service.AppBannerService
import com.fund.modules.conf.dto.BaseConfig
import com.fund.modules.conf.dto.IpoConfig
import com.fund.modules.conf.dto.RisingFallingConfig
import com.fund.modules.conf.dto.StockMarketConfig
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.doc.model.AppDoc
import com.fund.modules.doc.service.AppDocService
import com.fund.modules.notice.model.AppNotice
import com.fund.modules.notice.service.AppNoticeService
import com.fund.modules.sys.model.SysCsLink
import com.fund.modules.sys.service.SysCsLinkService
import com.fund.modules.sys.service.UploadService
import com.fund.modules.sys.vo.FileVO
import com.fund.utils.ImageUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = ["/common"])
@Tag(name = "公共接口", description = "上传图片、配置信息 等相关接口")
class CommonController(
    private val uploadService: UploadService,
    private val appConfigService: AppConfigService,
    private val csLinkService: SysCsLinkService,
    private val bannerService: AppBannerService,
    private val docService: AppDocService,
    private val appNoticeService: AppNoticeService,
) {

    @Operation(
        summary = "上传图片",
        description = "上传图片文件并返回访问URL"
    )
    @ApiResponse(responseCode = "200", description = "上传成功")
    @PostMapping("/uploadImg")
    fun uploadImg(
        @Parameter(description = "要上传的图片文件", required = true)
        @RequestParam("file") file: MultipartFile
    ): R<FileVO> {
        if (file.isEmpty) {
            throw BusinessException("Upload file cannot be empty")
        }
        val fileName = file.originalFilename
        if (!ImageUtil.isImage(fileName)) {
            throw BusinessException("error")
        }
        val fileVO = uploadService.storeFile(file)
        return R.success(fileVO)
    }

    @GetMapping("/config")
    @Operation(
        summary = "获取基础配置",
        description = "获取系统基础配置信息，包括注册奖励、手续费率等"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    fun selectBaseConfig(): R<BaseConfig> {
        val config = appConfigService.getConfig(BaseConfig::class.java)
        return R.success(config)
    }

    @GetMapping("stockMarketConfig")
    @Operation(
        summary = "获取股票市场配置",
        description = "获取各股票市场的交易时间和配置信息"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    fun selectStockMarketConfig(): R<StockMarketConfig> {
        val config = appConfigService.getConfig(StockMarketConfig::class.java)
        return R.success(config)
    }

    @GetMapping("ipoConfig")
    @Operation(
        summary = "获取IPO配置",
        description = "获取新股申购相关配置信息"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    fun selectIpoConfig(): R<IpoConfig> {
        val config = appConfigService.getConfig(IpoConfig::class.java)
        return R.success(config)
    }

    @GetMapping("risingFallingConfig")
    @Operation(
        summary = "获取涨跌板块配置",
        description = "获取涨跌板块相关配置信息"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    fun selectRisingFallingConfig(): R<RisingFallingConfig> {
        val config = appConfigService.getConfig(RisingFallingConfig::class.java)
        return R.success(config)
    }

    @Operation(
        summary = "获取客服链接",
        description = "获取客服链接相关配置信息"
    )
    @GetMapping("csLink")
    fun getCsLink(): R<SysCsLink> {
        val csLink = csLinkService.getOne(
            KtQueryWrapper(SysCsLink::class.java)
                .eq(SysCsLink::enable, true)
                .last(" limit 1")
        )
        return R.success(csLink)
    }

    @Operation(summary = "轮播图列表")
    @GetMapping("/banner")
    fun banner(
        @Parameter(description = "国际化标识编码（可选）", required = false, example = "banner_home_top")
        @RequestParam(required = false) i18nCode: String?
    ): R<List<AppBanner>> {
        val list = bannerService.list(
            KtQueryWrapper(AppBanner::class.java)
                .eq(!i18nCode.isNullOrBlank(), AppBanner::i18nCode, i18nCode)
                .eq(AppBanner::bannerStatus, true)
                .orderByDesc(AppBanner::sortBy)
        )
        return R.success(list)
    }


    @GetMapping("/getDoc")
    @Operation(summary = "获取文案")
    fun getDoc(
        @RequestHeader(value = HeaderConstants.Lang, required = false, defaultValue = "en_US")
        lang: String,
        @RequestParam(value = "usedFor", required = true)
        @Parameter(name = "用途 参考用途列表")
        usedFor: String
    ): R<AppDoc?> {
        // en-US -> en_US
        val i18nCode = lang.replace("-", "_")
        val doc = docService.getOne(
            KtQueryWrapper(AppDoc())
                .eq(AppDoc::usedFor, usedFor)
                .eq(AppDoc::i18nCode, i18nCode)
                .orderByAsc(AppDoc::sortBy, AppDoc::createTime)
                .last("limit 1")
        )
        return R.success(doc)
    }

    @GetMapping("/getNotice")
    @Operation(summary = "获取信息公告栏")
    fun getNotice(
        @RequestHeader(value = HeaderConstants.Lang, required = false, defaultValue = "en_US")
        lang: String
    ): R<List<AppNotice>> {
        val i18nCode = lang.replace("-", "_")
        val appNotices = appNoticeService.list(
            KtQueryWrapper(AppNotice::class.java)
                .eq(AppNotice::language, i18nCode)
                .orderByDesc(AppNotice::id)
        )
        return R.success(appNotices)
    }

}