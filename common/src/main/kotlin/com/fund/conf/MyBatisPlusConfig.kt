package com.fund.conf

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor



//@Configuration
class MyBatisPlusConfig {

   // @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        // 乐观锁
        interceptor.addInnerInterceptor(OptimisticLockerInnerInterceptor())
       // interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.MYSQL))
        return interceptor
    }


}