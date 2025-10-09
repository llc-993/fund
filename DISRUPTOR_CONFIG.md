# Disruptor K线处理配置说明

## 架构设计

### 模块分层
```
common 模块
    └── (基础工具类)

orm 模块
    ├── kline/
    │   ├── config/
    │   │   └── DisruptorConfig.kt        # Disruptor配置类（管理生命周期）
    │   ├── event/
    │   │   ├── KlineEvent.kt             # 事件对象
    │   │   ├── KlineEventFactory.kt      # 事件工厂
    │   │   └── KlineEventHandler.kt      # 事件处理器
    │   └── service/
    │       └── KlineService.kt            # K线业务逻辑
    └── stock/
        └── consumer/
            └── PositionUserUpdListener.kt # 股票消息监听器（使用RingBuffer）
```

### 设计优势
- ✅ **关注点分离**: 配置、事件、业务逻辑各司其职
- ✅ **依赖注入**: 通过 Spring Bean 管理 Disruptor 和 RingBuffer
- ✅ **模块清晰**: 避免了模块循环依赖
- ✅ **易于扩展**: 新增消费者只需注入 `klineRingBuffer` Bean
- ✅ **优雅关闭**: 实现 `DisposableBean` 接口，自动资源清理

## 配置项

在 `application.yaml` 或 `application.properties` 中添加以下配置：

### YAML 格式
```yaml
# K线 Disruptor 配置
kline:
  disruptor:
    enabled: true      # 是否启用 Disruptor（默认: true）
    buffer-size: 1024  # RingBuffer 缓冲区大小，必须是2的幂次方 (512, 1024, 2048, 4096等)

# 股票持仓监听器配置
stock:
  position:
    listener:
      enabled: true    # 是否启用监听器（默认: true）
      max-retry: 3     # 最大重试次数（默认: 3）
```

### Properties 格式
```properties
# K线 Disruptor 配置
kline.disruptor.enabled=true
kline.disruptor.buffer-size=1024

# 股票持仓监听器配置
stock.position.listener.enabled=true
stock.position.listener.max-retry=3
```

## 缓冲区大小选择建议

| 场景 | 建议大小 | 说明 |
|------|---------|------|
| 低频交易 | 512 | 适合交易频率较低的场景 |
| 中频交易 | 1024 (默认) | 适合大多数场景 |
| 高频交易 | 2048 - 4096 | 适合高并发、高吞吐量场景 |
| 超高频 | 8192+ | 适合极端高频场景，注意内存占用 |

**注意：** `buffer-size` 必须是 2 的幂次方，否则会初始化失败。

## 数据流向

```
Redis (STOCK_MESSAGE_QUEUE)
    ↓
PositionUserUpdListener.processStockUpdateMessage()
    ↓
publishToDisruptor(stock)
    ↓
RingBuffer (Disruptor)
    ↓
KlineEventHandler
    ↓
KlineService.processKlineMessage(stock)
    ↓
[K线数据处理逻辑]
```

## 性能特点

- **无锁设计**: Disruptor 使用 CAS 无锁算法，避免了传统队列的锁竞争
- **缓存友好**: 使用 RingBuffer 环形数组，提高 CPU 缓存命中率
- **批量处理**: 支持事件批量处理，提高吞吐量
- **异步处理**: 股票数据处理和 K线生成异步进行，互不阻塞

## 监控指标

通过日志可以观察：
- Disruptor 初始化状态
- 事件发布成功/失败
- 事件处理延迟
- 错误计数

## Spring Bean 说明

| Bean 名称 | 类型 | 作用 | 作用域 |
|----------|------|------|--------|
| `klineDisruptor` | `Disruptor<KlineEvent>` | Disruptor 实例 | Singleton |
| `klineRingBuffer` | `RingBuffer<KlineEvent>` | 事件发布器 | Singleton |

### 使用示例

```kotlin
@Component
class MyConsumer(
    private val klineRingBuffer: RingBuffer<KlineEvent>
) {
    fun publishStock(stock: Stock) {
        val sequence = klineRingBuffer.next()
        try {
            val event = klineRingBuffer[sequence]
            event.setData(stock)
        } finally {
            klineRingBuffer.publish(sequence)
        }
    }
}
```

## 故障排查

### 1. RingBuffer 满了导致阻塞
**症状**: 日志中看到大量 "发布 Stock 数据到 Disruptor 失败"  
**原因**: 消费速度 < 生产速度，RingBuffer 被填满  
**解决方案**:
- 增大 `buffer-size` (例如从 1024 → 2048)
- 优化 `KlineService.processKlineMessage` 的处理速度
- 检查是否有阻塞操作（如同步数据库写入）

### 2. 内存占用过高
**症状**: 应用内存持续增长  
**原因**: RingBuffer 中对象未被释放或有内存泄漏  
**解决方案**:
- 检查 `KlineEvent.clear()` 是否被正确调用
- 减小 `buffer-size`
- 使用内存分析工具检查是否有对象堆积

### 3. Disruptor 初始化失败
**症状**: 启动时抛出 `IllegalArgumentException`  
**原因**: `buffer-size` 不是 2 的幂次方  
**解决方案**:
```properties
# 错误示例
kline.disruptor.buffer-size=1000  # ❌ 不是2的幂次方

# 正确示例
kline.disruptor.buffer-size=1024  # ✅ 2^10
kline.disruptor.buffer-size=2048  # ✅ 2^11
```

### 4. Disruptor 未启用
**症状**: 日志中没有 "K线 Disruptor 初始化成功" 信息  
**原因**: 配置中禁用了 Disruptor  
**解决方案**:
```yaml
kline:
  disruptor:
    enabled: true  # 确保为 true 或删除此配置（默认为 true）
```

### 5. 循环依赖错误
**症状**: Spring 启动失败，提示循环依赖  
**原因**: Bean 依赖关系配置错误  
**解决方案**: 确保按照以下顺序依赖
```
KlineService → DisruptorConfig → klineRingBuffer → PositionUserUpdListener
```

