package com.fund.common

object Constants {

    const val success = "success"
    const val fail = "fail"

    const val ADMIN_INFO = "adminInfo"

    // 市场的英为的国家id
    val MARKET_LIST = listOf<Int>(5, 37, 17, 14)

    // 国家货币符号
    val MARKET_COIN_MAP = mapOf<String, String>(
        "CN" to "CNY",
        "US" to "USD",
        "IN" to "INR",
        "DE" to "EUR",
    )

    private const val FIELDS =
        "id,name,symbol,isCFD,high,low,last,lastPairDecimal,change,changePercent,volume,time,isOpen,url,flag,countryNameTranslated,exchangeId,performanceDay,performanceWeek,performanceMonth,performanceYtd,performanceYear,performance3Year,technicalHour,technicalDay,technicalWeek,technicalMonth,avgVolume,fundamentalMarketCap,fundamentalRevenue,fundamentalRatio,fundamentalBeta,pairType"

    // country-id=%d, page=%d, page-size=%d
    const val API_URL_TEMPLATE_BASE = "https://api.investing.com/api/financialdata/assets/equitiesByCountry/default"
    val API_URL_TEMPLATE: String =
        "${API_URL_TEMPLATE_BASE}?fields-list=${FIELDS}&country-id=%d&filter-domain=&page=%d&page-size=%d&limit=0&include-additional-indices=false&include-major-indices=false&include-other-indices=false&include-primary-sectors=false&include-market-overview=false"
}