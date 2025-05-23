/*
 * Copyright 2022-2024 兮玥(190785909@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chestnut.common.db.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 备份表字段抽象类，包含备份表字段和基础类字段
 *
 * @author 兮玥
 * @email 190785909@qq.com
 */
@Getter
@Setter
public abstract class BackupBaseEntity<T> extends BaseEntity implements IBackupEntity<T> {

	/**
	 * 备份ID
	 */
	@TableId(value = "backup_id", type = IdType.INPUT)
	private Long backupId;

	/**
	 * 备份时间
	 */
	private LocalDateTime backupTime;

	/**
	 * 备份操作人
	 */
	private String backupBy;

	/**
	 * 备注
	 */
	private String backupRemark;
}
