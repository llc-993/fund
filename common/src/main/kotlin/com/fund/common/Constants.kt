package com.fund.common

object Constants {

    const val success = "success"
    const val fail = "fail"

    const val ADMIN_INFO = "adminInfo"

    // 市场的英为的国家id
    val MARKET_LIST = listOf<Int>(14)

    // 国家货币符号
    val MARKET_COIN_MAP = mapOf<String, String>(
        "HK" to "HKD"
    )

    const val MARKET_THUMB = "/topic/market/thumb"
    const val MARKET_KLINE = "/topic/market/kline/"

    // https://api.investing.com/api/financialdata/assets/equitiesByCountry/default?fields-list=id%2Cname%2Csymbol%2CisCFD%2Chigh%2Clow%2Clast%2ClastPairDecimal%2Cchange%2CchangePercent%2Cvolume%2Ctime%2CisOpen%2Curl%2Cflag%2CcountryNameTranslated%2CexchangeId%2CperformanceDay%2CperformanceWeek%2CperformanceMonth%2CperformanceYtd%2CperformanceYear%2Cperformance3Year%2CtechnicalHour%2CtechnicalDay%2CtechnicalWeek%2CtechnicalMonth%2CavgVolume%2CfundamentalMarketCap%2CfundamentalRevenue%2CfundamentalRatio%2CfundamentalBeta%2CpairType&country-id=14&filter-domain=&page=0&page-size=100&limit=0&include-additional-indices=false&include-major-indices=false&include-other-indices=false&include-primary-sectors=false&include-market-overview=false
    private const val FIELDS =
        "id,name,symbol,isCFD,high,low,last,lastPairDecimal,change,changePercent,volume,time,isOpen,url,flag,countryNameTranslated,exchangeId,performanceDay,performanceWeek,performanceMonth,performanceYtd,performanceYear,performance3Year,technicalHour,technicalDay,technicalWeek,technicalMonth,avgVolume,fundamentalMarketCap,fundamentalRevenue,fundamentalRatio,fundamentalBeta,pairType"

    // country-id=%d, page=%d, page-size=%d
    const val API_URL_TEMPLATE_BASE = "https://api.investing.com/api/financialdata/assets/equitiesByCountry/default"
    val API_URL_TEMPLATE: String =
        "${API_URL_TEMPLATE_BASE}?fields-list=${FIELDS}&country-id=%d&filter-domain=&page=%d&page-size=%d&limit=0&include-additional-indices=false&include-major-indices=false&include-other-indices=false&include-primary-sectors=false&include-market-overview=false"
}