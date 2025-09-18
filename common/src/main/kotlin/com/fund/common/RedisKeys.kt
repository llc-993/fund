package com.fund.common

object RedisKeys {

    const val LIMIT_IP_REG = "limit_ip_reg:"

    // 注册
    const val LOCK_REG = "lock:reg:"

    const val IP_CACHES = "ip_caches"
    const val STOCK_KEY = "stock:"
    
    // StockData完整数据存储
    const val STOCK_DATA_KEY = "stock_data:"
    
    // 股票消息队列
    const val STOCK_MESSAGE_QUEUE = "stock_message_queue"

    const val APPCONFIG = "app_config"

    const val CHANGE_PASSWORD_LIMIT = "change_password_limit:"

    const val COUNTRY_KEY = "country:"

    const val BUY_KEY = "buy:"
    
    // 订单检查相关
    const val CHECK_ORDER_KEY = "check_order:%s"
    const val CHECK_USER_POSITION_KEY = "check_user_position:%s"
    
    // 用户持仓对象缓存
    const val USER_POSITION_CACHE_KEY = "user_position_cache:%s"

    const val PROCESS_USER_POSITION_LOCK_KEY = "process_user_position_lock:"
}