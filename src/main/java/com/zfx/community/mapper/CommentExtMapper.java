package com.zfx.community.mapper;


import com.zfx.community.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
@Repository
@Mapper
public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}