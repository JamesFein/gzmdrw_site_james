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
package com.chestnut.word.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chestnut.word.domain.HotWordGroup;

public interface IHotWordGroupService extends IService<HotWordGroup> {

	/**
	 * 添加热词分组
	 * 
	 * @param group
	 */
	HotWordGroup addHotWordGroup(HotWordGroup group);

	/**
	 * 修改热词分组
	 * 
	 * @param group
	 */
	void updateHotWordGroup(HotWordGroup group);

	/**
	 * 删除热词分组
	 * 
	 * @param groupIds
	 */
	void deleteHotWordGroups(List<Long> groupIds);

	/**
	 * 校验热词分组编码唯一性
	 *
	 * @param owner
	 * @param groupId
	 * @param code
	 */
    void checkUnique(String owner, Long groupId, String code);
}
