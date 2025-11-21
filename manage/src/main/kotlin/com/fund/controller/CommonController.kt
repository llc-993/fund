package com.fund.controller

import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.sys.service.UploadService
import com.fund.modules.sys.vo.FileVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/common")
@Tag(name = "App 文案管理", description = "上传图片")
class CommonController (
    private val uploadService: UploadService
){

    @Operation(summary = "上传图片")
    @PostMapping("/uploadImg")
    fun upload(@RequestParam("file") file: MultipartFile) : R<FileVO> {
        if (file.isEmpty) {
            throw BusinessException("上传文件不能为空")
        }
        val fileVO = uploadService.storeFile(file)
        return R.success(fileVO)
    }

}