# IPO 与大宗交易的业务逻辑差异

## 概述

本文档说明 IPO（新股申购）和大宗交易（Block Trade）在系统中的业务逻辑差异。

## 核心差异对比

| 特性 | IPO (新股申购) | 大宗交易 (Block Trade) |
|------|---------------|---------------------|
| **股票查询方式** | 通过 `symbol`（股票代码）查询 | 通过 `stockId` 直接关联 |
| **价格确定** | 固定价格 (`ipo.price`) | 当前市场价格 × 折扣 (`stock.last × discount`) |
| **锁定状态字段** | `isLock` (0=未锁仓, 1=锁仓) | `lockStatus` (1=锁定, 2=不锁定) |
| **持仓锁定逻辑** | 简单锁定/不锁定 | 支持分阶段释放锁定 |
| **申购记录状态** | 1-已认购, 2-未中签, 3-已中签, 4-已缴纳, 5-已转持仓 | 1-已申购, 2-已取消, 3-已确认, 4-已转持仓 |
| **确认数量字段** | `allotmentQuantity`（中签数量） | `confirmQuantity`（确认数量） |
| **转持仓操作类型** | `IPO_CONVERSION` | `BLOCK_TRADE_CONVERSION` |

## 详细说明

### 1. 股票关联方式

#### IPO
- IPO 记录中存储 `symbol`（股票代码）
- 转持仓时通过 `symbol` 查询 `stock` 表获取股票信息
- 适用于新股上市场景，股票可能还未在系统中存在

```kotlin
val stock = stockService.getOne(
    KtQueryWrapper(Stock())
        .eq(Stock::symbol, subscription.symbol)
)
```

#### 大宗交易
- 大宗交易记录中直接存储 `stockId`
- 转持仓时通过 `stockId` 直接查询
- 适用于已上市股票的折扣销售

```kotlin
val stock = stockService.getById(subscription.stockId?.toLong())
```

### 2. 价格计算逻辑

#### IPO
```kotlin
// 固定价格
val buyPrice = ipo.price
val totalAmount = allotmentQuantity × buyPrice
```

特点：
- 价格固定不变
- 在 IPO 创建时就已确定
- 不受市场波动影响

#### 大宗交易
```kotlin
// 动态折扣价格
val originalPrice = stock.last  // 当前市场价格
val discount = blockTrade.discount  // 折扣（如 0.9 表示9折）
val buyPrice = originalPrice × discount
val totalAmount = confirmQuantity × buyPrice
```

特点：
- 价格随市场实时变动
- 申购时计算折扣价格
- 给予用户优惠（通常低于市场价）

### 3. 锁定状态转换

#### IPO 锁仓逻辑
```kotlin
// IPO 的 isLock: 0=未锁仓，1=锁仓
// UserPosition 的 isLock: 1=锁定，2=不锁定
isLock = if (ipo.isLock == 1) 1.toByte() else 2.toByte()
```

特点：
- 简单的二元状态
- 锁定后通常在上市日期自动解锁
- 不支持分阶段释放

#### 大宗交易锁定逻辑
```kotlin
// BlockTrade 的 lockStatus: 1=锁定，2=不锁定
// UserPosition 的 isLock: 1=锁定，2=不锁定（逻辑一致）
isLock = blockTrade.lockStatus?.toByte() ?: 2.toByte()
```

特点：
- 支持分阶段释放
- 可设置第一次释放比例和时间 (`firstReleaseLookRate`, `firstReleaseLookDateTime`)
- 可设置完全释放时间 (`releaseLookTime`)

**分阶段释放示例：**
```
假设购买 1000 股，锁定设置如下：
- lockStatus = 1（锁定）
- firstReleaseLookRate = 0.3（30%）
- firstReleaseLookDateTime = 2025-11-01
- releaseLookTime = 2026-01-01

释放时间表：
- 2025-10-16: 0 股可卖（完全锁定）
- 2025-11-01: 300 股可卖（释放30%）
- 2026-01-01: 1000 股可卖（完全释放）
```

### 4. 申购流程差异

#### IPO 申购流程
```
1. 用户申购（status=1 已认购）
2. 管理员抽签决定中签
   - 未中签：status=2
   - 中签：status=3
3. 用户缴纳资金（status=4 已缴纳）
4. 转持仓（status=5 已转持仓）
```

特点：
- 需要抽签环节
- 可能未中签
- 中签后需要缴纳

#### 大宗交易申购流程
```
1. 用户申购（status=1 已申购）
2. 管理员审核确认（status=3 已确认）
3. 转持仓时扣款
   - 余额不足：status=2 已取消
   - 余额充足：status=4 已转持仓
```

特点：
- 无抽签环节
- 申购即确认（或需管理员审核）
- 转持仓时才扣款

### 5. 余额不足处理

#### IPO
```kotlin
// 余额不足时标记为"未中签"
subscription.status = 2  // 未中签
subscription.allotmentQuantity = BigDecimal.ZERO
subscription.remarks = "余额不足，未中签"
```

#### 大宗交易
```kotlin
// 余额不足时标记为"已取消"
subscription.status = 2  // 已取消
subscription.remarks = "余额不足，已取消"
```

## 数据库字段对比

### IPO 表 (ipo)
```sql
- id: 主键
- name: 股票名称
- symbol: 股票代码（用于关联）
- price: 固定申购价格
- openDate: 开始申购时间
- closeDate: 结束申购时间
- listingDate: 上市时间
- isLock: 是否锁仓 (0/1)
- status: 状态 (1=认购中, 2=结束)
```

### 大宗交易表 (stock_block_trade)
```sql
- id: 主键
- name: 股票名称
- stockId: 股票ID（直接关联）
- discount: 折扣比例
- minAmount: 最小申购数量
- maxAmount: 最大申购数量
- startDateTime: 开始售卖时间
- endDateTime: 结束售卖时间
- lockStatus: 锁定状态 (1/2)
- releaseLookTime: 完全释放时间
- firstReleaseLookRate: 第一次释放比例
- firstReleaseLookDateTime: 第一次释放时间
- status: 状态 (1=开放中, 2=已关闭)
```

## API 接口对比

### 用户端 (business 模块)

| 功能 | IPO | 大宗交易 |
|------|-----|---------|
| 列表查询 | `GET /ipo/list` | `GET /block/list` |
| 申购 | `POST /ipo/apply` | `POST /block/apply` |
| 申购历史 | `GET /ipo/history` | `GET /block/history` |
| 修改申购 | `POST /ipo/update` | `POST /block/update` |

### 管理端 (manage 模块)

| 功能 | IPO | 大宗交易 |
|------|-----|---------|
| 主体管理 | `/ipo/*` | `/block/*` |
| 申购记录 | `/subscription/*` | `/block/subscription/*` |
| 转持仓 | `POST /subscription/conversion` | `POST /block/subscription/conversion` |

## 最佳实践建议

### 使用 IPO 的场景
- 新股上市
- 需要公平抽签机制
- 价格固定不变
- 简单的锁仓/解锁逻辑

### 使用大宗交易的场景
- 已上市股票的折扣销售
- 需要按市场价格打折
- 需要分阶段释放锁定
- VIP 用户特殊待遇
- 员工持股计划

## 注意事项

1. **时间类型统一**：两者都使用 `LocalDateTime` 进行时间判断
2. **Redis 锁机制**：两者都使用 Redis 分布式锁防止并发问题
3. **国际化支持**：错误消息都支持 i18n
4. **事务一致性**：转持仓操作都在事务中执行，保证数据一致性
5. **日志记录**：所有关键操作都有详细的日志记录

## 代码示例

### IPO 转持仓核心代码
```kotlin
// 查询股票（通过symbol）
val stock = stockService.getOne(
    KtQueryWrapper(Stock()).eq(Stock::symbol, subscription.symbol)
)

// 固定价格
val totalAmount = allotmentQuantity.multiply(subscription.buyPrice)

// 锁定状态转换
isLock = if (ipo.isLock == 1) 1.toByte() else 2.toByte()
```

### 大宗交易转持仓核心代码
```kotlin
// 查询股票（通过stockId）
val stock = stockService.getById(subscription.stockId?.toLong())

// 折扣价格
val totalAmount = confirmQuantity.multiply(subscription.buyPrice)

// 锁定状态一致
isLock = blockTrade.lockStatus?.toByte() ?: 2.toByte()
```

## 总结

IPO 和大宗交易虽然都涉及股票申购和转持仓，但它们服务于不同的业务场景：

- **IPO** 更适合新股发行，强调公平性（抽签）和固定价格
- **大宗交易** 更适合折扣销售，强调灵活性（动态定价、分阶段解锁）

在实现时，两者的代码结构相似但细节不同，需要注意各自的特殊逻辑。

