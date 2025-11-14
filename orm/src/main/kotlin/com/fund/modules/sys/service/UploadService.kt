package com.fund.modules.sys.service

import com.fund.modules.sys.vo.FileVO
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

interface UploadService {

    @Throws(IOException::class)
    fun storeFile(file: MultipartFile): FileVO

}