-- 股票市场配置数据插入SQL
-- 美国股票市场配置
INSERT INTO app_config (code, value, remark, create_by, create_time, update_time) VALUES
('usTimezone', 'America/New_York', '美国股票市场时区', 'system', NOW(), NOW()),
('usMorningOpen', '09:30', '美国股票市场上午开盘时间', 'system', NOW(), NOW()),
('usAfternoonOpen', '13:00', '美国股票市场下午开盘时间', 'system', NOW(), NOW()),
('usMorningClose', '12:00', '美国股票市场上午收盘时间', 'system', NOW(), NOW()),
('usAfternoonClose', '16:00', '美国股票市场下午收盘时间', 'system', NOW(), NOW()),

-- 中国股票市场配置
('cnTimezone', 'Asia/Shanghai', '中国股票市场时区', 'system', NOW(), NOW()),
('cnMorningOpen', '09:30', '中国股票市场上午开盘时间', 'system', NOW(), NOW()),
('cnAfternoonOpen', '13:00', '中国股票市场下午开盘时间', 'system', NOW(), NOW()),
('cnMorningClose', '11:30', '中国股票市场上午收盘时间', 'system', NOW(), NOW()),
('cnAfternoonClose', '15:00', '中国股票市场下午收盘时间', 'system', NOW(), NOW()),

-- 印度股票市场配置
('inTimezone', 'Asia/Kolkata', '印度股票市场时区', 'system', NOW(), NOW()),
('inMorningOpen', '09:15', '印度股票市场上午开盘时间', 'system', NOW(), NOW()),
('inAfternoonOpen', '14:00', '印度股票市场下午开盘时间', 'system', NOW(), NOW()),
('inMorningClose', '11:30', '印度股票市场上午收盘时间', 'system', NOW(), NOW()),
('inAfternoonClose', '15:30', '印度股票市场下午收盘时间', 'system', NOW(), NOW()),

-- 德国股票市场配置
('deTimezone', 'Europe/Berlin', '德国股票市场时区', 'system', NOW(), NOW()),
('deMorningOpen', '09:00', '德国股票市场上午开盘时间', 'system', NOW(), NOW()),
('deAfternoonOpen', '13:00', '德国股票市场下午开盘时间', 'system', NOW(), NOW()),
('deMorningClose', '12:00', '德国股票市场上午收盘时间', 'system', NOW(), NOW()),
('deAfternoonClose', '17:30', '德国股票市场下午收盘时间', 'system', NOW(), NOW()),

-- 股票购买数量配置
('buyMinNum', '1', '最小购买数量', 'system', NOW(), NOW()),
('buyMaxNum', '999999', '最大购买数量', 'system', NOW(), NOW()),

-- 各市场Lot单位配置
('usLotUnit', '1', '美国市场Lot单位', 'system', NOW(), NOW()),
('cnLotUnit', '100', '中国市场Lot单位', 'system', NOW(), NOW()),
('inLotUnit', '1', '印度市场Lot单位', 'system', NOW(), NOW()),
('deLotUnit', '1', '德国市场Lot单位', 'system', NOW(), NOW()),

-- 各市场最小购买金额配置
('usMinBuyAmount', '10', '美国市场最小购买金额', 'system', NOW(), NOW()),
('cnMinBuyAmount', '100', '中国市场最小购买金额', 'system', NOW(), NOW()),
('inMinBuyAmount', '10', '印度市场最小购买金额', 'system', NOW(), NOW()),
('deMinBuyAmount', '10', '德国市场最小购买金额', 'system', NOW(), NOW())，
('buyFeeRate', '0.001', '买入手续费率', 'system', NOW(), NOW()),
('stayFeeRate', '0.0001', '留仓费率', 'system', NOW(), NOW()),
('dutyFeeRate', '0.001', '印花税费率', 'system', NOW(), NOW())
;
