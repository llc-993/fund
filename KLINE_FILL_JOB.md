# K线数据填充任务说明

## 功能概述

K线数据填充任务（`KlineFillJob`）用于解决股票在某些时间段内没有实时交易数据导致K线图出现空白的问题。

### 核心问题
- 某些股票在交易时间内可能没有成交数据
- 导致对应时间周期的K线缺失
- K线图表出现空白或断线

### 解决方案
- 定时任务每分钟执行一次
- 检测交易时间内没有数据的股票
- 使用上一个周期的收盘价填充当前周期
- 成交量设置为 0

## 工作流程

### 1. 定时执行
```
每分钟触发 (cron: 0 */1 * * * ?)
    ↓
获取所有活跃股票列表
    ↓
分批处理（默认 50 只/批）
    ↓
协程并发执行
```

### 2. 单个股票处理流程
```
检查股票数据有效性
    ↓
判断是否在交易时间内
    ↓ (否) 
跳过
    ↓ (是)
获取当前分钟对齐的时间戳
    ↓
检查 Redis 中是否已有数据
    ↓ (有)
跳过（说明有实时数据）
    ↓ (无)
获取上一个周期的K线数据
    ↓
复制并创建新K线
    - open = 上一根收盘价
    - high = 上一根收盘价
    - low = 上一根收盘价
    - close = 上一根收盘价
    - volume = 0
    ↓
添加到 Redis
```

## 配置项

### application.yaml
```yaml
kline:
  fill:
    enabled: true        # 是否启用填充任务（默认: true）
    batch-size: 50       # 每批处理的股票数量（默认: 50）
```

### application.properties
```properties
kline.fill.enabled=true
kline.fill.batch-size=50
```

## 交易时间判断

### 支持的市场
| 市场 | 时区 | 上午交易时间 | 下午交易时间 |
|------|------|------------|------------|
| US | America/New_York | 09:30-12:00 | 13:00-16:00 |
| CN | Asia/Shanghai | 09:30-11:30 | 13:00-15:00 |
| IN | Asia/Kolkata | 09:15-11:30 | 14:00-15:30 |
| DE | Europe/Berlin | 09:00-12:00 | 13:00-17:30 |

### 交易时间配置
交易时间通过 `app_config` 表动态配置：
- `US_MORNING_OPEN`, `US_MORNING_CLOSE`
- `US_AFTERNOON_OPEN`, `US_AFTERNOON_CLOSE`
- 其他市场类似

## 协程并发处理

### 架构设计
```kotlin
CoroutineScope(Dispatchers.IO + SupervisorJob())
    ├── 使用 IO 调度器（适合 I/O 密集型任务）
    ├── SupervisorJob（一个子任务失败不影响其他）
    └── 分批并发处理
```

### 性能特点
- **并发处理**: 多个股票同时处理，提高效率
- **批量控制**: 避免一次性处理过多任务导致资源耗尽
- **异步非阻塞**: 不影响主线程和其他任务
- **错误隔离**: 单个股票处理失败不影响其他股票

### 处理容量估算
| 股票数量 | 批次大小 | 批次数 | 预计耗时 |
|---------|---------|-------|---------|
| 100 | 50 | 2 | ~200ms |
| 500 | 50 | 10 | ~1s |
| 1000 | 50 | 20 | ~2s |
| 5000 | 50 | 100 | ~10s |

## 数据填充示例

### 场景：股票在某分钟内无成交

#### 时间轴
```
14:23:00 - 有数据 (实时推送)
  ↓
  K线: open=100, high=101, low=99, close=100.5, volume=1000
  
14:24:00 - 无数据 (没有成交)
  ↓ (定时任务填充)
  K线: open=100.5, high=100.5, low=100.5, close=100.5, volume=0
  
14:25:00 - 有数据 (恢复成交)
  ↓
  K线: open=100.5, high=102, low=100, close=101, volume=800
```

### 填充前后对比

#### 填充前（有空白）
```
时间        | 开   | 高   | 低   | 收   | 量
14:20:00   | 100  | 101  | 99   | 100  | 1000
14:21:00   | 100  | 100.5| 99.5 | 100.2| 500
14:22:00   | (空白)
14:23:00   | (空白)
14:24:00   | 100.2| 102  | 100  | 101  | 800
```

#### 填充后（连续）
```
时间        | 开   | 高   | 低   | 收   | 量    | 来源
14:20:00   | 100  | 101  | 99   | 100  | 1000  | 实时
14:21:00   | 100  | 100.5| 99.5 | 100.2| 500   | 实时
14:22:00   | 100.2| 100.2| 100.2| 100.2| 0     | 填充
14:23:00   | 100.2| 100.2| 100.2| 100.2| 0     | 填充
14:24:00   | 100.2| 102  | 100  | 101  | 800   | 实时
```

## 日志输出

### 正常执行
```
INFO  - 开始执行K线数据填充任务
INFO  - K线数据填充任务完成，处理 523 只股票，耗时 1245ms
```

### 跳过处理
```
INFO  - 没有找到活跃的股票数据
```

### 错误日志
```
ERROR - 处理股票K线填充失败: AAPL
ERROR - K线数据填充任务执行失败
```

## 监控指标

### 关键指标
1. **任务执行时间**: 每次任务耗时（应 < 30秒）
2. **处理股票数量**: 每次处理的股票总数
3. **填充数据量**: 实际填充的K线条数
4. **失败次数**: 处理失败的股票数量

### 告警阈值建议
- 任务执行时间 > 30秒：需要调整 batch-size 或优化代码
- 失败率 > 5%：需要检查股票数据或网络问题

## 与实时数据的协作

### 优先级
```
实时数据 > 填充数据
```

### 冲突处理
```kotlin
// 检查当前周期是否已有数据
val latestKline = klineRedisManager.getLatestKline(market, symbol, interval)

// 如果当前周期已有数据，不需要填充
if (latestKline != null && latestKline.timestamp == alignedTimestamp) {
    return  // 跳过填充
}
```

### 时序保证
1. 实时数据通过 Disruptor 立即处理
2. 填充任务每分钟执行一次
3. 填充前先检查是否已有实时数据
4. 避免覆盖实时数据

## 最佳实践

### 1. 调整批次大小
```yaml
# 股票数量较少（< 500）
kline.fill.batch-size: 50

# 股票数量较多（500-2000）
kline.fill.batch-size: 100

# 股票数量很多（> 2000）
kline.fill.batch-size: 200
```

### 2. 临时禁用
```yaml
# 维护或测试时可以禁用
kline.fill.enabled: false
```

### 3. 监控任务执行
```kotlin
// 添加指标收集
@Scheduled(cron = "0 */1 * * * ?")
fun fillKlineData() {
    val startTime = System.currentTimeMillis()
    // ... 执行逻辑
    val duration = System.currentTimeMillis() - startTime
    metricsService.recordFillJobDuration(duration)
}
```

## 性能优化建议

### 1. 数据库索引
确保 `stock` 表有适当的索引：
```sql
CREATE INDEX idx_stock_flag_symbol ON stock(flag, symbol);
CREATE INDEX idx_stock_status ON stock(status);
```

### 2. Redis 连接池
```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

### 3. 协程调优
```kotlin
// 根据服务器性能调整线程数
val coroutineScope = CoroutineScope(
    Dispatchers.IO.limitedParallelism(100) + SupervisorJob()
)
```

## 常见问题

### Q1: 为什么只填充 1min 周期？
A: 其他周期（5min, 1h, 1day等）会从 1min 数据自动聚合生成，无需单独填充。

### Q2: 填充的数据会不会覆盖实时数据？
A: 不会。填充前会检查当前周期是否已有数据，如果有则跳过。

### Q3: 非交易时间会执行吗？
A: 任务会执行，但会判断是否在交易时间内，非交易时间跳过处理。

### Q4: 如何验证填充是否成功？
A: 查看日志中的处理数量和 Redis 中的数据：
```bash
redis-cli
> ZCARD kline:US_AAPL_1min
> ZRANGE kline:US_AAPL_1min 0 5 WITHSCORES
```

### Q5: 任务执行时间过长怎么办？
A: 
1. 增加 `batch-size` 提高并发度
2. 优化网络和数据库连接
3. 考虑分布式部署（多实例分片处理）

## 扩展功能

### 1. 数据修复
```kotlin
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点
fun repairHistoricalData() {
    // 扫描历史数据，修复缺失的K线
}
```

### 2. 数据质量检查
```kotlin
@Scheduled(cron = "0 0 3 * * ?")  // 每天凌晨3点
fun checkDataQuality() {
    // 检查K线数据的连续性和完整性
}
```

### 3. 异常股票告警
```kotlin
// 如果某只股票连续多次填充，可能有问题
if (consecutiveFillCount > 10) {
    alertService.sendAlert("Stock ${symbol} has no real data for 10 minutes")
}
```

---

**文档版本**: 1.0  
**最后更新**: 2024-01-15  
**维护者**: 开发团队

