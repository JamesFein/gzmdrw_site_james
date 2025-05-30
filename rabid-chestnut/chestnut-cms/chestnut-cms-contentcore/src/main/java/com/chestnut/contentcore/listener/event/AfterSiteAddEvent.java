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
package com.chestnut.contentcore.listener.event;

import com.chestnut.contentcore.domain.CmsSite;
import com.chestnut.contentcore.domain.dto.SiteDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

@Getter
public class AfterSiteAddEvent extends ApplicationEvent {

	@Serial
	private static final long serialVersionUID = 1L;

	private final SiteDTO siteDTO;

	private final CmsSite site;

	public AfterSiteAddEvent(Object source, CmsSite site, SiteDTO dto) {
		super(source);
		this.site = site;
		this.siteDTO = dto;
	}
}
