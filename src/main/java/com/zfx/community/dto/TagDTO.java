package com.zfx.community.dto;


import java.util.List;

/**
 * Created by codedrinker on 2019/6/5.
 */

public class TagDTO {
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private String categoryName;
    private List<String> tags;
}
