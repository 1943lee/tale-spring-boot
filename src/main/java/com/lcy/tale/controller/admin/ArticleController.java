package com.lcy.tale.controller.admin;

import com.github.pagehelper.PageInfo;
import com.lcy.tale.constant.WebConst;
import com.lcy.tale.controller.BaseController;
import com.lcy.tale.model.dto.LogActions;
import com.lcy.tale.model.dto.Types;
import com.lcy.tale.exception.TipException;
import com.lcy.tale.model.bo.RestResponseBo;
import com.lcy.tale.model.entity.ContentsVo;
import com.lcy.tale.model.entity.MetasVo;
import com.lcy.tale.model.entity.UsersVo;
import com.lcy.tale.service.IContentService;
import com.lcy.tale.service.ILogService;
import com.lcy.tale.service.IMetaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by 13 on 2017/2/21.
 */
@Slf4j
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {
    @Resource
    private IContentService contentsService;

    @Resource
    private IMetaService metasService;

    @Resource
    private ILogService logService;

    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit,
                        HttpServletRequest request) {
        Example contentVoExample = new Example(ContentsVo.class);
        contentVoExample.setOrderByClause("created desc");
        contentVoExample.createCriteria().andEqualTo("type", Types.ARTICLE.getType());
        PageInfo<ContentsVo> contentsPaginator = contentsService.getArticlesWithpage(contentVoExample, page, limit);
        request.setAttribute("articles", contentsPaginator);
        return "admin/article_list";
    }

    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        List<MetasVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    @GetMapping(value = "/{cid}")
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {
        ContentsVo contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);
        List<MetasVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/article_edit";
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponseBo publishArticle(ContentsVo contents, HttpServletRequest request) {
        UsersVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        String result = contentsService.publish(contents);
        if (!WebConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponseBo modifyArticle(ContentsVo contents, HttpServletRequest request) {
        UsersVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        String result = contentsService.updateArticle(contents);
        if (!WebConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        String result = contentsService.deleteByCid(cid);
        logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "", request.getRemoteAddr(), this.getUid(request));
        if (!WebConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }
}
