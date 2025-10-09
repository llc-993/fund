package com.fund.modules.kline.event

import com.lmax.disruptor.EventFactory

/**
 * K线事件工厂
 */
class KlineEventFactory : EventFactory<KlineEvent> {
    
    override fun newInstance(): KlineEvent {
        return KlineEvent()
    }
}

