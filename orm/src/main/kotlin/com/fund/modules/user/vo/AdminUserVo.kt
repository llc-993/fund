package com.fund.modules.user.vo

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.fund.modules.user.model.AppUser
import com.fund.modules.wallet.model.AppUserWalletV2
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

class AdminUserVo: Serializable {

    /**
     * id
     */
    @Schema(description = "用户主键ID", example = "10001", nullable = true)
    var id: Long? = null

    /**
     * 总代用户ID
     */
    @Schema(description = "上级代理用户ID", example = "20001", nullable = true)
    var topUserId: Long? = null

    @Schema(description = "用户昵称", example = "小明", nullable = true)
    var userName: String? = null

    @Schema(description = "用户登录账号", example = "xiaoming88", nullable = true)
    var userAccount: String? = null

    @Schema(description = "用户邀请码", example = "INV123456", nullable = true)
    var shareCode: String? = null

    @Schema(description = "绑定手机号", example = "+8613712345678", nullable = true)
    var mobilePhone: String? = null

    @Schema(description = "用户组类型（0=正常用户，1=虚拟用户）", example = "0", nullable = true)
    var userGroup: Int? = null

    @Schema(description = "是否冻结（true=已冻结）", example = "false", nullable = true)
    var isFrozen: Boolean? = null

    @Schema(description = "是否允许交易（true=允许）", example = "true", nullable = true)
    var tradable: Boolean? = null

    @Schema(description = "是否允许提现（true=允许）", example = "true", nullable = true)
    var cashable: Boolean? = null

    @Schema(description = "注册IP地址", example = "192.168.1.10", nullable = true)
    var registerIp: String? = null

    @Schema(description = "注册时间", example = "2025-08-21T15:30:00", nullable = true)
    var registerTime: LocalDateTime? = null

    @Schema(description = "关联的钱包信息列表", nullable = true)
    var wallet: List<AppUserWalletV2>? = null

    @Schema(description = "盈亏", nullable = true)
    var profitAndLose: BigDecimal? = null


}