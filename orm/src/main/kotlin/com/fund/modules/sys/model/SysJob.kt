package com.fund.modules.sys.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 定时任务
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("sys_job")
class SysJob : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 任务名称
     */
    @TableField("job_name")
    var jobName: String? = null

    /**
     * bean名称
     */
    @TableField("bean_name")
    var beanName: String? = null

    /**
     * 方法名称
     */
    @TableField("method_name")
    var methodName: String? = null

    /**
     * 方法参数
     */
    @TableField("method_params")
    var methodParams: String? = null

    /**
     * cron表达式
     */
    @TableField("cron_expression")
    var cronExpression: String? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 是否启动
     */
    @TableField("running")
    var running: Boolean? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "SysJob(id=$id, jobName=$jobName, beanName=$beanName, methodName=$methodName, methodParams=$methodParams, cronExpression=$cronExpression, remark=$remark, running=$running, createTime=$createTime, updateTime=$updateTime)"
    }
}
