package com.zfx.community.dto;

import lombok.Data;

/**
 * 文件上传返回 DTO
 */
@Data
public class FileDTO {
    private int success;
    private String message;
    private String url;
}
