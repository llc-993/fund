# K线数据处理说明文档

## 系统架构

### 数据流向
```
Stock 实时数据
    ↓
Disruptor (异步处理)
    ↓
KlineServiceImpl.processKlineMessage()
    ↓
生成 7 种时间周期的K线 (1min, 5min, 30min, 1h, 1day, 1week, 1month)
    ↓
Redis SortedSet (缓存最新 100 条)
    ↓
当 > 100 条时，最旧的 50 条 → MongoDB 持久化
```

## 核心组件

### 1. KlineAggregator（时间聚合工具类）

**功能**：
- 时间对齐：将实时时间戳对齐到K线周期的开始时间
- 时区处理：根据市场标识自动选择时区

**支持的时间周期**：
| 周期 | 代码 | 时间对齐规则 | 示例 |
|------|------|------------|------|
| 1分钟 | `1min` | 当前分钟开始 | 14:23:45 → 14:23:00 |
| 5分钟 | `5min` | 5的倍数分钟 | 14:23:45 → 14:20:00 |
| 30分钟 | `30min` | 30的倍数分钟 | 14:23:45 → 14:00:00 |
| 1小时 | `1h` | 当前小时开始 | 14:23:45 → 14:00:00 |
| 1天 | `1day` | 当天0点 | 2024-01-15 14:23 → 2024-01-15 00:00 |
| 1周 | `1week` | 本周周一0点 | 2024-01-15 → 2024-01-14 00:00 (周一) |
| 1月 | `1month` | 本月1日0点 | 2024-01-15 → 2024-01-01 00:00 |

**时区映射**：
| 市场 | 时区 |
|------|------|
| US | America/New_York |
| CN | Asia/Shanghai |
| IN | Asia/Kolkata |
| DE | Europe/Berlin |
| HK | Asia/Hong_Kong |
| JP | Asia/Tokyo |
| 其他 | UTC |

### 2. KlineRedisManager（Redis管理器）

**功能**：
- 使用 Redis SortedSet 存储K线数据
- Score = timestamp（时间戳，秒）
- Value = K线JSON字符串
- 自动管理数据量，触发持久化

**Redis Key 格式**：
```
kline:{market}_{symbol}_{interval}
```

**示例**：
- `kline:US_AAPL_1min` - 苹果股票1分钟K线
- `kline:CN_000001_1day` - 平安银行日K线

**数据管理策略**：
```
Redis 中保持最新 100 条K线
    ↓
当新增数据后总数 > 100
    ↓
取出最旧的 50 条
    ↓
从 Redis 删除这 50 条
    ↓
返回这 50 条用于持久化
```

### 3. KlineServiceImpl（核心处理逻辑）

**主要方法**：

#### processKlineMessage(stock: Stock)
主入口方法，处理一条股票实时数据：
1. 验证数据完整性
2. 获取市场时区
3. 为所有7种周期生成K线

#### processKlineForInterval()
为单个周期处理K线：
1. 对齐时间戳
2. 查询 Redis 获取当前周期的最新K线
3. 判断是更新还是创建新K线
4. 写入 Redis
5. 如果返回待持久化数据，写入 MongoDB

#### createNewKline()
创建新K线：
- open = high = low = close = 当前价格
- volume = 当前成交量
- timestamp = 对齐后的时间戳

#### updateKline()
更新现有K线：
- open: 保持不变
- high: 取最大值
- low: 取最小值
- close: 更新为当前价格
- volume: 累加

#### persistToMongoDB()
持久化到 MongoDB：
- Collection 命名：`Kline_{market}_{symbol}_{interval}`
- 批量插入K线数据

## MongoDB 集合设计

### 集合命名规则
```
Kline_{market}_{symbol}_{interval}
```

### 示例
```
Kline_US_AAPL_1min      # 苹果1分钟K线
Kline_US_AAPL_5min      # 苹果5分钟K线
Kline_US_AAPL_1day      # 苹果日K线
Kline_CN_000001_1min    # 平安银行1分钟K线
```

### 数据结构
```json
{
  "_id": "1704096000",           // 时间戳（秒）
  "symbol": "AAPL",              // 股票代码
  "market": "US",                // 市场标识
  "interval": "1min",            // K线周期
  "timestamp": 1704096000,       // 时间戳（秒）
  "open": 185.50,                // 开盘价
  "high": 186.20,                // 最高价
  "low": 185.30,                 // 最低价
  "close": 185.80,               // 收盘价
  "volume": 1234567,             // 成交量
  "createTime": 1704096060000    // 创建时间（毫秒）
}
```

## 处理流程详解

### 场景1：新周期开始
```
当前时间: 14:05:30
上一条K线: 14:00:00 - 14:04:59
    ↓
对齐时间戳: 14:05:00
    ↓
Redis 中无此时间戳的K线
    ↓
创建新K线：
  - timestamp: 14:05:00
  - open/high/low/close: 当前价格
  - volume: 当前成交量
    ↓
添加到 Redis SortedSet
```

### 场景2：周期内更新
```
当前时间: 14:05:30
当前周期: 14:05:00
    ↓
对齐时间戳: 14:05:00
    ↓
Redis 中找到此时间戳的K线
    ↓
更新K线：
  - high: max(原high, 当前价格)
  - low: min(原low, 当前价格)
  - close: 当前价格
  - volume: 原volume + 当前成交量
    ↓
更新 Redis SortedSet
```

### 场景3：触发持久化
```
Redis 中已有 100 条K线
    ↓
新增一条K线后，总数 = 101
    ↓
KlineRedisManager 检测到超过阈值
    ↓
取出最旧的 50 条 (index 0-49)
    ↓
从 Redis 删除这 50 条
    ↓
返回这 50 条K线列表
    ↓
KlineServiceImpl 批量插入到 MongoDB
    ↓
Redis 中剩余 51 条（继续循环）
```

## 性能优化

### 1. 异步处理
- 使用 Disruptor 异步处理，不阻塞主流程
- 单线程顺序消费，保证K线数据一致性

### 2. Redis 缓存
- 热数据在 Redis，查询速度快
- SortedSet 按时间排序，范围查询高效

### 3. 批量持久化
- 累积 50 条再写入 MongoDB
- 减少数据库写入次数

### 4. 分表存储
- 按市场、股票、周期分Collection
- 避免单表数据过大
- 便于并行查询

## 数据示例

### 苹果股票（AAPL）- 1分钟K线

#### Redis 数据
```
key: kline:US_AAPL_1min

SortedSet:
Score             Value (JSON)
-----------------+--------------------------------
1704096000       | {"id":"1704096000", "open":185.50, ...}
1704096060       | {"id":"1704096060", "open":185.80, ...}
1704096120       | {"id":"1704096120", "open":185.90, ...}
...
1704101940       | {"id":"1704101940", "open":186.20, ...} (最新100条)
```

#### MongoDB 数据
```
Collection: Kline_US_AAPL_1min

Documents:
[
  {"_id": "1704090000", "open": 185.00, ...},
  {"_id": "1704090060", "open": 185.10, ...},
  ...
  (所有历史数据)
]
```

## 监控指标

### 日志输出
- ✅ `成功持久化 XX 条K线到MongoDB: Kline_US_AAPL_1min`
- ⚠️ `股票数据不完整，跳过K线生成`
- ❌ `处理K线周期失败`
- ❌ `插入K线到MongoDB失败`

### 关键指标
- Redis 中各周期K线数量
- MongoDB 持久化频率
- K线生成延迟
- 数据完整性（是否有缺失的周期）

## 常见问题

### Q1: 为什么使用 SortedSet 而不是 List？
A: SortedSet 按 score 自动排序，支持：
- 快速获取最新/最旧数据
- 范围查询（按时间范围）
- 自动去重（相同timestamp的K线只保留最新）

### Q2: 如何处理跨天/跨月的K线？
A: `KlineAggregator` 会自动对齐：
- 日K线：对齐到当天0点（考虑时区）
- 周K线：对齐到本周周一0点
- 月K线：对齐到本月1日0点

### Q3: 如果 Redis 宕机了怎么办？
A: 
- 短期：从 MongoDB 重新加载最近100条到 Redis
- 实时数据：继续生成新K线并直接写入 MongoDB
- 建议：配置 Redis 持久化（AOF/RDB）

### Q4: 不同市场的交易时间不同，如何处理？
A: 
- 时区已自动处理
- K线按本地市场时间对齐
- 可以在 `KlineAggregator` 中添加交易时间过滤

## 扩展建议

### 1. 技术指标计算
```kotlin
// 在 persistToMongoDB 后触发
calculateTechnicalIndicators(klineList, market, symbol, interval)
```

### 2. K线合并
```kotlin
// 从小周期合成大周期
merge1MinTo5Min()
merge5MinTo30Min()
```

### 3. 实时推送
```kotlin
// WebSocket 推送给前端
pushKlineToWebSocket(kline, subscribers)
```

### 4. 数据修复
```kotlin
// 定时任务：检查并修复缺失的K线
@Scheduled(cron = "0 0 2 * * ?")
fun repairMissingKlines()
```

## 配置要求

### Redis
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### MongoDB
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/fund
      database: fund
```

### Disruptor
```yaml
kline:
  disruptor:
    enabled: true
    buffer-size: 1024
```

---

**文档版本**: 1.0  
**最后更新**: 2024-01-15  
**维护者**: 开发团队

