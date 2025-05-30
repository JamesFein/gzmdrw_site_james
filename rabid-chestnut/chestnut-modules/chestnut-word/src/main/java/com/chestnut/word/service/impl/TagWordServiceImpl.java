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
package com.chestnut.word.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chestnut.common.exception.CommonErrorCode;
import com.chestnut.common.utils.Assert;
import com.chestnut.common.utils.IdUtils;
import com.chestnut.common.utils.SortUtils;
import com.chestnut.common.utils.StringUtils;
import com.chestnut.word.domain.TagWord;
import com.chestnut.word.domain.TagWordGroup;
import com.chestnut.word.domain.dto.BatchAddTagDTO;
import com.chestnut.word.mapper.TagWordGroupMapper;
import com.chestnut.word.mapper.TagWordMapper;
import com.chestnut.word.service.ITagWordService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TagWordServiceImpl extends ServiceImpl<TagWordMapper, TagWord> implements ITagWordService {

	private final RedissonClient redissonClient;

	private final TagWordGroupMapper tagWordGroupMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addTagWord(TagWord tagWord) {
		RLock lock = redissonClient.getLock("TagWord");
		lock.lock();
		try {
			boolean checkUnique = checkUnique(tagWord.getGroupId(), null, tagWord.getWord());
			Assert.isTrue(checkUnique, () -> CommonErrorCode.DATA_CONFLICT.exception("word"));

			TagWordGroup tagWordGroup = tagWordGroupMapper.selectById(tagWord.getGroupId());
			Assert.notNull(tagWordGroup, () -> CommonErrorCode.DATA_NOT_FOUND_BY_ID.exception("groupId", tagWord.getGroupId()));

			tagWord.setWordId(IdUtils.getSnowflakeId());
			tagWord.setOwner(tagWordGroup.getOwner());
			if (Objects.isNull(tagWord.getSortFlag())) {
				tagWord.setSortFlag(SortUtils.getDefaultSortValue());
			}
			this.save(tagWord);

			tagWordGroup.setWordTotal(tagWordGroup.getWordTotal() + 1);
			tagWordGroupMapper.updateById(tagWordGroup);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void batchAddTagWord(BatchAddTagDTO dto) {
		RLock lock = redissonClient.getLock("TagWord");
		lock.lock();
		try {
			TagWordGroup tagWordGroup = tagWordGroupMapper.selectById(dto.getGroupId());
			Assert.notNull(tagWordGroup, () -> CommonErrorCode.DATA_NOT_FOUND_BY_ID.exception("groupId", dto.getGroupId()));

			for (String word : dto.getWords()) {
				boolean checkUnique = checkUnique(dto.getGroupId(), null, word);
				Assert.isTrue(checkUnique, () -> CommonErrorCode.DATA_CONFLICT.exception("word"));
			}

			List<TagWord> list = dto.getWords().stream().filter(StringUtils::isNotBlank).map(word -> {
				TagWord tagWord = new TagWord();
				tagWord.setWordId(IdUtils.getSnowflakeId());
				tagWord.setGroupId(dto.getGroupId());
				tagWord.setOwner(tagWordGroup.getOwner());
				tagWord.setWord(word);
				tagWord.setSortFlag(SortUtils.getDefaultSortValue());
				tagWord.createBy(dto.getOperator().getUsername());
				return tagWord;
			}).toList();

			this.saveBatch(list);

			tagWordGroup.setWordTotal(tagWordGroup.getWordTotal() + list.size());
			tagWordGroupMapper.updateById(tagWordGroup);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void editTagWord(TagWord tagWord) {
		TagWord dbTagWord = this.getById(tagWord.getWordId());
		Assert.notNull(dbTagWord, () -> CommonErrorCode.DATA_NOT_FOUND_BY_ID.exception("wordId", tagWord.getWordId()));

		boolean checkUnique = checkUnique(tagWord.getGroupId(), tagWord.getWordId(), tagWord.getWord());
		Assert.isTrue(checkUnique, () -> CommonErrorCode.DATA_CONFLICT.exception("word"));

		dbTagWord.setWord(tagWord.getWord());
		dbTagWord.setLogo(tagWord.getLogo());
		dbTagWord.setSortFlag(tagWord.getSortFlag());
		dbTagWord.setRemark(tagWord.getRemark());
		dbTagWord.updateBy(tagWord.getUpdateBy());
		this.updateById(tagWord);
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteTagWords(List<Long> tagWordIds) {
		RLock lock = redissonClient.getLock("TagWord");
		lock.lock();
		try {
			List<TagWord> tagWords = this.listByIds(tagWordIds);
			Map<Long, Integer> groupWordDecrease = new HashMap<>();
			tagWords.forEach(tag -> {
				groupWordDecrease.put(tag.getGroupId(), groupWordDecrease.getOrDefault(tag.getGroupId(), 0) + 1);
			});
			this.removeByIds(tagWordIds);
			groupWordDecrease.forEach((k, v) -> {
				TagWordGroup group = tagWordGroupMapper.selectById(k);
				group.setWordTotal(group.getWordTotal() - v);
				tagWordGroupMapper.updateById(group);
			});
		} finally {
			lock.unlock();
		}
	}

	private boolean checkUnique(Long groupId, Long wordId, String word) {
		return this.lambdaQuery().eq(TagWord::getGroupId, groupId)
				.ne(wordId != null && wordId > 0, TagWord::getWordId, wordId)
				.eq(TagWord::getWord, word)
				.count() == 0;
	}
}