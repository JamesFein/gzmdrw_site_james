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
package com.chestnut.customform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chestnut.common.domain.R;
import com.chestnut.common.security.anno.Priv;
import com.chestnut.common.security.web.BaseRestController;
import com.chestnut.common.security.web.PageRequest;
import com.chestnut.common.utils.DateUtils;
import com.chestnut.common.utils.IdUtils;
import com.chestnut.common.utils.ServletUtils;
import com.chestnut.common.utils.StringUtils;
import com.chestnut.contentcore.domain.CmsSite;
import com.chestnut.contentcore.service.ISiteService;
import com.chestnut.contentcore.util.InternalUrlUtils;
import com.chestnut.customform.CmsCustomFormMetaModelType;
import com.chestnut.customform.permission.CustomFormPriv;
import com.chestnut.system.security.AdminUserType;
import com.chestnut.system.validator.LongId;
import com.chestnut.xmodel.service.IModelDataService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 自定义表单数据控制器
 * </p>
 *
 * @author 兮玥
 * @email 190785909@qq.com
 */
@RestController
@RequestMapping("/cms/customform/data")
@RequiredArgsConstructor
public class CustomFormDataController extends BaseRestController {

    private final ISiteService siteService;

    private final IModelDataService modelDataService;

    @Priv(type = AdminUserType.TYPE, value = CustomFormPriv.View)
    @GetMapping
    public R<?> getList(@RequestParam @LongId Long formId, @RequestParam(required = false) String ip) {
        CmsSite site = this.siteService.getCurrentSite(ServletUtils.getRequest());
        PageRequest pr = this.getPageRequest();
        IPage<Map<String, Object>> page = this.modelDataService.selectModelDataPage(formId,
                new Page<>(pr.getPageNumber(), pr.getPageSize(), true), sqlBuilder -> {
            sqlBuilder.and().eq(CmsCustomFormMetaModelType.FIELD_SITE_ID.getFieldName(), site.getSiteId());
            if (StringUtils.isNotEmpty(ip)) {
                sqlBuilder.and().eq(CmsCustomFormMetaModelType.FIELD_CLIENT_IP.getFieldName(), ip);
            }
        });
        List<Map<String, Object>> dataList = page.getRecords().stream().map(data -> {
            Map<String, Object> map = new HashMap<>();
            data.forEach((k, v)  -> {
                if (Objects.nonNull(v) && InternalUrlUtils.isInternalUrl(v.toString())) {
                    map.put(k + "_src", InternalUrlUtils.getActualPreviewUrl(v.toString()));
                }
                map.put(k, v);
            });
            return map;
        }).toList();
        return this.bindDataTable(dataList, page.getTotal());
    }

    @GetMapping("/detail")
    public R<?> getCustomFormDataDetail(@RequestParam @LongId Long formId, @RequestParam @LongId Long dataId) {
        Map<String, Object> map = this.modelDataService.getModelDataByPkValue(formId,
                Map.of(CmsCustomFormMetaModelType.FIELD_DATA_ID.getCode(), dataId));
        Map<String, Object> dataMap = new HashMap<>();
        map.forEach((k, v) -> {
            if (Objects.nonNull(v) && InternalUrlUtils.isInternalUrl(v.toString())) {
                dataMap.put(k + "_src", InternalUrlUtils.getActualPreviewUrl(v.toString()));
            }
            dataMap.put(k, v);
        });
        return R.ok(dataMap);
    }

    @PostMapping
    public R<?> addCustomFormData(@RequestParam @LongId Long formId, @RequestBody Map<String, Object> data) {
        CmsSite site = this.siteService.getCurrentSite(ServletUtils.getRequest());

        data.put(CmsCustomFormMetaModelType.FIELD_DATA_ID.getCode(), IdUtils.getSnowflakeId());
        data.put(CmsCustomFormMetaModelType.FIELD_MODEL_ID.getCode(), formId);
        data.put(CmsCustomFormMetaModelType.FIELD_SITE_ID.getCode(), site.getSiteId());
        data.put(CmsCustomFormMetaModelType.FIELD_UUID.getCode(), StringUtils.EMPTY);
        data.put(CmsCustomFormMetaModelType.FIELD_CLIENT_IP.getCode(), "127.0.0.1");
        data.put(CmsCustomFormMetaModelType.FIELD_CREATE_TIME.getCode(), DateUtils.getDateTime());
        this.modelDataService.addModelData(formId, data);
        return R.ok();
    }

    @PutMapping
    public R<?> editCustomFormData(@RequestParam @LongId Long formId, @RequestBody Map<String, Object> data) {
        this.modelDataService.updateModelData(formId, data);
        return R.ok();
    }

    @DeleteMapping
    public R<?> deleteCustomFormDatas(@RequestParam @LongId Long formId, @RequestBody @NotEmpty List<Long> dataIds) {
        List<Map<String, String>> pkValues = dataIds.stream()
                .map(id -> Map.of(CmsCustomFormMetaModelType.FIELD_DATA_ID.getCode(), id.toString())).toList();
        this.modelDataService.deleteModelDataByPkValue(formId, pkValues);
        return R.ok();
    }
}
