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

    /** AI量化周期用户锁，后缀 userId */
    const val LOCK_AI_QUANT_CYCLE = "lock:ai_quant:cycle:"

    /** AI量化订单/周期写锁，后缀 cycleId */
    const val LOCK_AI_QUANT_ORDER = "lock:ai_quant:order:"

    // 顶级代理与用户id映射
    const val TOP_AGENT_MAP_CACHE_KEY = "top_agent_maps"

    const val App_EMAIL_TEMPLATE_CONFIG = "app_email_template_config"

    const val STOCK_PID_KEY = "stock_pid"

    const val STOCK_INDEX = "stock_index"

    const val RISE_STOCK = "rise_stock"

    /** 积存金用户单渠道交易锁，后缀 userId:channelId */
    const val LOCK_GOLD_TRADE = "lock:gold:trade:"

    /** 积存金渠道行情写入锁，后缀 channelId */
    const val LOCK_GOLD_QUOTE = "lock:gold:quote:"

    /** 积存金 K 线写入锁，后缀 channelCode:interval */
    const val LOCK_GOLD_KLINE = "lock:gold:kline:"

    /** 积存金渠道实时金价缓存键，后缀 channelId（JSON 字符串） */
    const val CACHE_GOLD_QUOTE = "cache:gold:quote:"

    /** 积存金每日定时任务全局锁（避免多实例重复跑） */
    const val LOCK_GOLD_DAILY_JOB = "lock:gold:daily:job"
}