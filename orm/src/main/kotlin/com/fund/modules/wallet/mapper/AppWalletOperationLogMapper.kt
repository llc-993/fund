package com.fund.modules.wallet.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.fund.modules.wallet.model.AppWalletOperationLog
import org.apache.ibatis.annotations.Mapper

/**
 * <p>
 * 钱包操作日志表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Mapper
interface AppWalletOperationLogMapper : BaseMapper<AppWalletOperationLog> {

}
