package com.lcy.tale.model.bo;

import com.lcy.tale.model.entity.ContentsVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 13 on 2017/2/23.
 */
@Data
public class ArchiveBo implements Serializable {

    private static final long serialVersionUID = 1602112735699306282L;
    private String date;
    private String count;
    private List<ContentsVo> articles;

    @Override
    public String toString() {
        return "Archive [" +
                "date='" + date + '\'' +
                ", count='" + count + '\'' +
                ", articles=" + articles +
                ']';
    }
}
