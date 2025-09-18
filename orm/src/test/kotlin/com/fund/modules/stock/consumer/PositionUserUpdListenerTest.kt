package com.fund.modules.stock.consumer

import com.alibaba.fastjson2.JSON
import com.fund.common.RedisKeys.STOCK_MESSAGE_QUEUE
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.stock.service.StockService
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.I18nUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.redisson.api.RedissonClient
import org.redisson.api.RTopic
import org.redisson.api.RSet
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PositionUserUpdListenerTest {

}
