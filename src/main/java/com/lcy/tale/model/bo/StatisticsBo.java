package com.lcy.tale.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台统计对象
 */
@Data
public class StatisticsBo implements Serializable {

    private static final long serialVersionUID = -4737690588696762772L;
    private int articles;
    private int comments;
    private int links;
    private int attachs;

    @Override
    public String toString() {
        return "StatisticsBo{" +
                "articles=" + articles +
                ", comments=" + comments +
                ", links=" + links +
                ", attachs=" + attachs +
                '}';
    }
}
