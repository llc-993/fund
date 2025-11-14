package com.fund.modules.conf.dto

/**
 * 亚马逊S3 oss配置
 */

class AwsS3OssConfig {

    /**
     *accessKey
     */
    var s3AccessKeyId: String? = null

    /**
     * accessKeySecret
     */
    var s3AccessKeySecret: String? = null

    /**
     * end point
     */
    var s3Endpoint: String? = null

    /**
     * bucketName
     */
    var s3BucketName: String? = null

    /**
     * 最大上传限制
     */
    var s3UploadMaxSize: Int? = null
}