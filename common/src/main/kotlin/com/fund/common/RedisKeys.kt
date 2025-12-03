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
    
    // K线消息队列
    const val KLINE_MESSAGE_QUEUE = "kline_message_queue"

    const val APPCONFIG = "app_config"

    const val CHANGE_PASSWORD_LIMIT = "change_password_limit:"

    const val COUNTRY_KEY = "country:"

    const val BUY_KEY = "buy:"
    const val SELL_KEY = "sell:"

    const val PENDING_ORDER_KEY = "pending_order:"
    
    // 订单检查相关
    const val CHECK_ORDER_KEY = "check_order:%s"
    const val CHECK_USER_POSITION_KEY = "check_user_position:%s"
    
    // 用户持仓对象缓存
    const val USER_POSITION_CACHE_KEY = "user_position_cache:%s"

    const val PROCESS_USER_POSITION_LOCK_KEY = "process_user_position_lock:"

    const val IPO_APPLY_LOCK_KEY = "ipo_apply_lock:"
    
    const val BLOCK_TRADE_APPLY_LOCK_KEY = "block_trade_apply_lock:"
    
    const val RISING_FALLING_SECTORS_APPLY_LOCK_KEY = "rising_falling_sectors_apply_lock:"

    const val LOCK_CASH_OUT_REQUEST = "lock:cash_out:request:"
    const val LOCK_CASH_IN_REQUEST = "lock:cash_in:request:"
    
    // 理财产品申购锁
    const val LOCK_FINANCIAL_PURCHASE = "lock:financial:purchase:"

    // 顶级代理与用户id映射
    const val TOP_AGENT_MAP_CACHE_KEY = "top_agent_maps"

    const val App_EMAIL_TEMPLATE_CONFIG = "app_email_template_config"

    const val STOCK_PID_KEY = "stock_pid"
}