package com.fund.modules.stock.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fund.modules.kline.Kline

import java.io.Serializable
import java.math.BigDecimal

/**
 * <p>
 * 
 * </p>
 *
 * @author 书记
 * @since 2025-12-28
 */
@TableName("stock_index")
class StockIndex : Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 名称
     */
    @TableField("name")
    var name: String? = null

    /**
     * 指数名称
     */
    @TableField("index_code")
    var indexCode: String? = null

    /**
     * 外部资源的id
     */
    @TableField("index_id")
    var indexId: Int? = null

    /**
     * 状态，1:开启，0:关闭
     */
    @TableField("status")
    var status: Int? = null

    @TableField(exist = false)
    var chg: BigDecimal? = null

    @TableField(exist = false)
    var chgPct: BigDecimal? = null
    @TableField(exist = false)
    var price: BigDecimal? = null
    @TableField(exist = false)
    var high: BigDecimal? = null
    @TableField(exist = false)
    var open: BigDecimal? = null
    @TableField(exist = false)
    var low: BigDecimal? = null
    @TableField(exist = false)
    var chart:List<Kline> = mutableListOf()

    override fun toString(): String {
        return "StockIndex{" +
        "id=" + id +
        ", name=" + name +
        ", indexCode=" + indexCode +
        ", indexId=" + indexId +
        ", status=" + status +
        "}"
    }
}
