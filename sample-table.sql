CREATE TABLE sample_table (
  `pk_sample` VARCHAR(50) NOT NULL COMMENT '主键',
  `code` VARCHAR(50) NOT NULL COMMENT '编码',
  `name` VARCHAR(100) COMMENT '名称',
  `amount` DECIMAL(20,8) DEFAULT 0 COMMENT '金额',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态',
  `created_time` DATETIME COMMENT '创建时间',
  PRIMARY KEY (`pk_sample`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';