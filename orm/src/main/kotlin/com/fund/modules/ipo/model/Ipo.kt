package com.fund.modules.ipo.model;

import com.baomidou.mybatisplus.annotation.*
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * IPO信息表
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@TableName("ipo")
class Ipo : Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 名字
     */
    @TableField("name")
    var name: String? = null

    /**
     * 国家
     */
    @TableField("country")
    var country: String? = null

    /**
     * 产品代码
     */
    @TableField("symbol")
    var symbol: String? = null

    /**
     * 开始时间
     */
    @TableField("open_date")
    var openDate: LocalDateTime? = null

    /**
     * 结束时间
     */
    @TableField("close_date")
    var closeDate: LocalDateTime? = null

    /**
     * 上市时间
     */
    @TableField("listing_date")
    var listingDate: LocalDateTime? = null

    /**
     * 价格
     */
    @TableField("price")
    var price: BigDecimal? = null

    /**
     * 认购数量
     */
    @TableField("count")
    var count: Long? = null

    /**
     * 交易所名称
     */
    @TableField("exchange")
    var exchange: String? = null

    /**
     * 状态，1:认购中，2:结束
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 1: 新股， 2:线下配售、3:定额配售、4:IPO多配
     */
    @TableField("type")
    var type: Int? = 1

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 新股申购，已认缴后，转持仓是否锁仓
     * 是否锁仓（0: 未锁仓, 1: 锁仓）
     */
    @TableField(value = "is_lock")
    var isLock: Int = 0

    /**
     * 认缴时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "subscription_time")
    var subscriptionTime: LocalDateTime? = null

    override fun toString(): String {
        return "Ipo{" +
                "id=" + id +
                ", name=" + name +
                ", country=" + country +
                ", symbol=" + symbol +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", listingDate=" + listingDate +
                ", price=" + price +
                ", count=" + count +
                ", exchange=" + exchange +
                ", status=" + status +
                ", createTime=" + createTime +
                ", isLock=" + isLock +
                ", subscriptionTime=" + subscriptionTime +
                "}"
    }
}
