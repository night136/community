package com.zfx.community.dto;

import lombok.Data;

/**
 * 创建评论请求 DTO
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
