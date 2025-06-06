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
package com.chestnut.contentcore.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chestnut.common.db.DBConstants;
import com.chestnut.common.db.domain.BackupBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 内容备份表对象 [b_cms_content]
 * 
 * @author 兮玥
 * @email 190785909@qq.com
 */
@Getter
@Setter
@TableName(value = BCmsContent.TABLE_NAME, autoResultMap = true)
public class BCmsContent extends BackupBaseEntity<CmsContent> {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public static final String TABLE_NAME = DBConstants.BACKUP_TABLE_PREFIX + CmsContent.TABLE_NAME;

    private Long contentId;

    /**
     * 所属站点ID
     */
    private Long siteId;

    /**
     * 所属栏目ID
     */
    private Long catalogId;

    /**
     * 所属栏目祖级IDs
     */
    private String catalogAncestors;

    /**
     * 所属顶级栏目
     */
    private Long topCatalog;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 短标题
     */
    private String shortTitle;

    /**
     * 标题样式
     */
    private String titleStyle;

    /**
     * logo
     */
    private String logo;

    /**
     * 来源
     */
    private String source;

    /**
     * 来源URL
     */
    private String sourceUrl;

    /**
     * 是否原创
     */
    private String original;

    /**
     * 作者
     */
    private String author;

    /**
     * 编辑
     */
    private String editor;

    /**
     * 投稿会员Id
     */
    private Long contributorId;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 自定义静态化文件路径
     */
    private String staticPath;

    /**
     * 内容状态
     */
    private String status;

    /**
     * 内容属性
     */
    private Integer attributes;

    /**
     * 是否链接内容
     */
    private String linkFlag;

    /**
     * 链接
     */
    private String redirectUrl;

    /**
     * 置顶标识
     */
    private Long topFlag;

    /**
     * 置顶结束时间
     */
    private LocalDateTime topDate;

    /**
     * 排序字段
     */
    private Long sortFlag;

    /**
     * 关键词
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] keywords;

    /**
     * TAGs
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] tags;

    /**
     * 复制类型
     */
    private Integer copyType;

    /**
     * 复制源ID
     */
    private Long copyId;

    /**
     * 发布时间
     */
    private LocalDateTime publishDate;

    /**
     * 下线时间
     */
    private LocalDateTime offlineDate;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 文章浏览数
     */
    private Long viewCount;

    /**
     * 是否锁定
     */
    private String isLock;

    /**
     * 锁定用户名
     */
    private String lockUser;

    /**
     * SEO标题
     */
    private String seoTitle;

    /**
     * SEO关键词
     */
    private String seoKeywords;

    /**
     * SEO描述
     */
    private String seoDescription;

    /**
     * 发布通道
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] publishPipe;

    /**
     * 发布通道属性
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Map<String, Object>> publishPipeProps;

    /**
     * 扩展属性
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> configProps;

    /**
     * 备用字段1
     */
    private String prop1;

    /**
     * 备用字段2
     */
    private String prop2;

    /**
     * 备用字段3
     */
    private String prop3;

    /**
     * 备用字段4
     */
    private String prop4;

    @Override
    public CmsContent toSourceEntity() {
        CmsContent content = new CmsContent();
        BeanUtils.copyProperties(this, content);
        return content;
    }
}
