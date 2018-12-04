package com.lcy.tale.controller.admin;

import com.github.pagehelper.PageInfo;
import com.lcy.tale.controller.BaseController;
import com.lcy.tale.model.bo.RestResponseBo;
import com.lcy.tale.model.entity.CommentsVo;
import com.lcy.tale.model.entity.UsersVo;
import com.lcy.tale.service.ICommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by 13 on 2017/2/26.
 */
@Slf4j
@Controller
@RequestMapping("admin/comments")
public class CommentController extends BaseController {

    @Resource
    private ICommentService commentsService;

    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        UsersVo users = this.user(request);
        Example commentVoExample = new Example(CommentsVo.class);
        commentVoExample.setOrderByClause("coid desc");
        //commentVoExample.createCriteria().andEqualTo("authorId", users.getUid());
        PageInfo<CommentsVo> commentsPaginator = commentsService.getCommentsWithPage(commentVoExample, page, limit);
        request.setAttribute("comments", commentsPaginator);
        return "admin/comment_list";
    }

    /**
     * 删除一条评论
     *
     * @param coid
     * @return
     */
    @PostMapping(value = "delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam Integer coid) {
        try {
            CommentsVo comments = commentsService.getCommentById(coid);
            if (null == comments) {
                return RestResponseBo.fail("不存在该评论");
            }
            commentsService.delete(coid, comments.getCid());
        } catch (Exception e) {
            String msg = "评论删除失败";
            log.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "status")
    @ResponseBody
    public RestResponseBo edit(@RequestParam Integer coid, @RequestParam String status) {
        try {
            CommentsVo comments = commentsService.getCommentById(coid);
            if (comments != null) {
                comments.setCoid(coid);
                comments.setStatus(status);
                commentsService.update(comments);
            } else {
                return RestResponseBo.fail("操作失败");
            }
        } catch (Exception e) {
            String msg = "操作失败";
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

}
