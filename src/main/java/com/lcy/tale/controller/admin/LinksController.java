package com.lcy.tale.controller.admin;

import com.lcy.tale.controller.BaseController;
import com.lcy.tale.model.dto.Types;
import com.lcy.tale.model.bo.RestResponseBo;
import com.lcy.tale.model.entity.MetasVo;
import com.lcy.tale.service.IMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by 13 on 2017/2/21.
 */
@Slf4j
@Controller
@RequestMapping("admin/links")
public class LinksController extends BaseController {

    @Resource
    private IMetaService metasService;

    @GetMapping(value = "")
    public String index(HttpServletRequest request) {
        List<MetasVo> metas = metasService.getMetas(Types.LINK.getType());
        request.setAttribute("links", metas);
        return "admin/links";
    }

    @PostMapping(value = "save")
    @ResponseBody
    public RestResponseBo saveLink(@RequestParam String title, @RequestParam String url,
                                   @RequestParam String logo, @RequestParam Integer mid,
                                   @RequestParam(value = "sort", defaultValue = "0") int sort) {
        try {
            MetasVo metas = new MetasVo();
            metas.setName(title);
            metas.setSlug(url);
            metas.setDescription(logo);
            metas.setSort(sort);
            metas.setType(Types.LINK.getType());
            if (null != mid) {
                metas.setMid(mid);
                metasService.update(metas);
            } else {
                metasService.saveMeta(metas);
            }
        } catch (Exception e) {
            String msg = "友链保存失败";
            log.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam int mid) {
        try {
            metasService.delete(mid);
        } catch (Exception e) {
            String msg = "友链删除失败";
            log.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

}
