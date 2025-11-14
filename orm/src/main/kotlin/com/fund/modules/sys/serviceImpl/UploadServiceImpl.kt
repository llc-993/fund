package com.fund.modules.sys.serviceImpl

import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.StrUtil
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.fund.exception.BusinessException
import com.fund.modules.conf.dto.AwsS3OssConfig
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.sys.service.UploadService
import com.fund.modules.sys.vo.FileVO
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class UploadServiceImpl(
    private val appConfigService: AppConfigService
) : UploadService {

    private val log = KotlinLogging.logger {}

    override fun storeFile(file: MultipartFile): FileVO {
        val fileName = file.originalFilename
        if(StrUtil.isEmpty(fileName)) {
            throw BusinessException("Upload file cannot be empty")
        }

        //检查图片格式
        /*val checkRule = imageSuffix(fileName)
        if (!checkRule) {
            throw BusinessException("请检查文件格式")
        }*/
        val size: Long = file.size
        // s3公用阿里云oss配置。
        val cfg: AwsS3OssConfig = appConfigService.getConfig(AwsS3OssConfig::class.java)
        if (size / (1024 * 1024) > cfg.s3UploadMaxSize!!) {
            throw BusinessException(
                StrUtil.format("图片大小不能超过{}M", cfg.s3UploadMaxSize)
            )
        }
        val s3: AmazonS3 = getS3(cfg)
        val vo = FileVO()

        //获取文件后缀
        try {
            val fileSuffix = fileName!!.substring(fileName.lastIndexOf(".") + 1)
            val key = StrUtil.format("{}_{}.{}", IdUtil.objectId(), fileName, fileSuffix.lowercase())
            val metadata = ObjectMetadata()
            metadata.contentType = file.contentType
            metadata.contentLength = file.size
            val putObjectRequest = PutObjectRequest(
                cfg.s3BucketName,
                key,
                file.inputStream,
                metadata
            )

            // 上传
            s3.putObject(putObjectRequest)
            vo.fileHost = "https://${cfg.s3BucketName}.s3.${cfg.s3Endpoint}/"
            vo.filePath = key
        } catch (e: Throwable) {
            log.error("", e)
            throw IOException(e.message)
        }
        return vo
    }

    private fun getS3(cfg: AwsS3OssConfig): AmazonS3 {
        // ap-east-1.amazonaws.com
        val endpoint: String = cfg.s3Endpoint!!
        val regionName = endpoint.substring(0, endpoint.indexOf("."))
        //东京区域
        val region: Region = Region.getRegion(Regions.fromName(regionName))
        val awsCredentials: AWSCredentials = BasicAWSCredentials(
            cfg.s3AccessKeyId,
            cfg.s3AccessKeySecret
        )
        val builder: AmazonS3ClientBuilder = AmazonS3ClientBuilder.standard()
            .withCredentials(
                AWSStaticCredentialsProvider(awsCredentials)
            )
        builder.region = region.name
        return builder.build()
    }
}