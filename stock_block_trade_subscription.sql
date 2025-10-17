-- 大宗交易申购记录表
CREATE TABLE IF NOT EXISTS `stock_block_trade_subscription` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `block_trade_id` bigint(20) DEFAULT NULL COMMENT '大宗交易ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `top_user_id` bigint(20) DEFAULT NULL COMMENT '上级用户ID',
  `name` varchar(255) DEFAULT NULL COMMENT '股票名称',
  `stock_id` bigint(20) DEFAULT NULL COMMENT '股票ID',
  `buy_price` decimal(20, 8) DEFAULT NULL COMMENT '购买价格',
  `apply_nums` decimal(20, 4) DEFAULT NULL COMMENT '申购数量',
  `discount` decimal(10, 4) DEFAULT NULL COMMENT '折扣',
  `actual_amount` decimal(20, 8) DEFAULT NULL COMMENT '实际支付金额',
  `status` int(11) DEFAULT NULL COMMENT '状态：1、已申购，2、已取消，3、已确认，4、已转持仓',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_block_trade_id` (`block_trade_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大宗交易申购记录表';

