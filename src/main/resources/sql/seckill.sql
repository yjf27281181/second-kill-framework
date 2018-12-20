DROP TABLE IF EXISTS `seckill`;

CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(120) NOT NULL COMMENT 'name of item',
  `number` int(11) NOT NULL COMMENT 'number of inventory',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'start time',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'end time',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `version` int(11) NOT NULL COMMENT 'version number',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='second kill inventory table';

/*Data for the table `seckill` */

insert  into `seckill`(`seckill_id`,`name`,`number`,`start_time`,`end_time`,`create_time`,`version`) values (1000,'$1000 iphone XR',100,'2018-12-10 15:31:53','2018-12-10 15:31:53','2018-12-10 15:31:53',0),(1001,'$500 for ipad pro',100,'2018-12-10 15:31:53','2018-12-10 15:31:53','2018-12-10 15:31:53',0),(1002,'$40 for apple pencil',100,'2018-12-10 15:31:53','2018-12-10 15:31:53','2018-12-10 15:31:53',0),(1003,'$200 for ipad2',100,'2018-05-10 15:31:53','2018-05-10 15:31:53','2018-05-10 15:31:53',0);

/*Table structure for table `success_killed` */

DROP TABLE IF EXISTS `success_killed`;

CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT 'item id',
  `user_id` bigint(20) NOT NULL COMMENT 'user Id',
  `state` tinyint(4) NOT NULL COMMENT 'state: -1 means error，0 means success，1 means have paid',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='success killed detail';