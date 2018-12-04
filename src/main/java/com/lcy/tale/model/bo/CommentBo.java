package com.lcy.tale.model.bo;

import com.lcy.tale.model.entity.CommentsVo;
import lombok.Data;

import java.util.List;

/**
 * 返回页面的评论，包含父子评论内容
 * Created by 13 on 2017/2/24.
 */
@Data
public class CommentBo extends CommentsVo {

    private int levels;
    private List<CommentsVo> children;

    public CommentBo(CommentsVo comments) {
        setAuthor(comments.getAuthor());
        setMail(comments.getMail());
        setCoid(comments.getCoid());
        setAuthorId(comments.getAuthorId());
        setUrl(comments.getUrl());
        setCreated(comments.getCreated());
        setAgent(comments.getAgent());
        setIp(comments.getIp());
        setContent(comments.getContent());
        setOwnerId(comments.getOwnerId());
        setCid(comments.getCid());
    }
}
