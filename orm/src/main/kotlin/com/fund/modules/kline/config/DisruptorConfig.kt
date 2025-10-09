package com.fund.modules.kline.config

import com.fund.modules.kline.event.KlineEvent
import com.fund.modules.kline.event.KlineEventFactory
import com.fund.modules.kline.event.KlineEventHandler
import com.fund.modules.kline.service.KlineService
import com.lmax.disruptor.RingBuffer
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.util.DaemonThreadFactory
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Disruptor 配置类
 * 负责创建和管理 K线处理的 Disruptor 实例
 */
@Configuration
class DisruptorConfig(
    private val klineService: KlineService
) : DisposableBean {

    private val logger = KotlinLogging.logger {}

    private val bufferSize: Int = 2 shl 13

    private lateinit var disruptor: Disruptor<KlineEvent>

    /**
     * 创建 K线 Disruptor 实例
     */
    @Bean("klineDisruptor")
    fun klineDisruptor(): Disruptor<KlineEvent> {
        logger.info("开始初始化 K线 Disruptor...")

        // 验证 buffer-size 是否为2的幂次方
        if (!isPowerOfTwo(bufferSize)) {
            throw IllegalArgumentException("Disruptor buffer-size 必须是2的幂次方，当前值: $bufferSize")
        }

        // 创建 Disruptor
        disruptor = Disruptor(
            KlineEventFactory(),
            bufferSize,
            DaemonThreadFactory.INSTANCE
        )

        // 设置事件处理器
        disruptor.handleEventsWith(KlineEventHandler(klineService))

        // 启动 Disruptor
        disruptor.start()

        logger.info("K线 Disruptor 初始化成功，缓冲区大小: $bufferSize")

        return disruptor
    }

    /**
     * 创建 K线 RingBuffer Bean
     * 用于向 Disruptor 发布事件
     */
    @Bean("klineRingBuffer")
    fun klineRingBuffer(klineDisruptor: Disruptor<KlineEvent>): RingBuffer<KlineEvent> {
        return klineDisruptor.ringBuffer
    }

    /**
     * 检查是否为2的幂次方
     */
    private fun isPowerOfTwo(value: Int): Boolean {
        return value > 0 && (value and (value - 1)) == 0
    }

    /**
     * 销毁资源
     */
    override fun destroy() {
        try {
            if (::disruptor.isInitialized) {
                logger.info("开始关闭 K线 Disruptor...")
                disruptor.shutdown()
                logger.info("K线 Disruptor 已优雅关闭")
            }
        } catch (e: Exception) {
            logger.error(e) { "关闭 K线 Disruptor 失败" }
        }
    }
}

