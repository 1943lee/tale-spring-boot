package com.lcy.tale.service;

import com.github.pagehelper.PageInfo;
import com.lcy.tale.model.bo.CommentBo;
import com.lcy.tale.model.entity.CommentsVo;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by BlueT on 2017/3/16.
 */
public interface ICommentService {

    /**
     * 保存对象
     * @param commentVo
     */
    String insertComment(CommentsVo commentVo);

    /**
     * 获取文章下的评论
     * @param cid
     * @param page
     * @param limit
     * @return CommentBo
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);

    /**
     * 获取文章下的评论
     * @param commentVoExample
     * @param page
     * @param limit
     * @return CommentVo
     */
    PageInfo<CommentsVo> getCommentsWithPage(Example commentVoExample, int page, int limit);

    /**
     * 根据主键查询评论
     * @param coid
     * @return
     */
    CommentsVo getCommentById(Integer coid);

    /**
     * 删除评论，暂时没用
     * @param coid
     * @param cid
     * @throws Exception
     */
    void delete(Integer coid, Integer cid);

    /**
     * 更新评论状态
     * @param comments
     */
    void update(CommentsVo comments);

}
