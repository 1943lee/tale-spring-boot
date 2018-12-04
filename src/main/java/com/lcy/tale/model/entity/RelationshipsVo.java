package com.lcy.tale.model.entity;

import javax.persistence.*;

@Table(name = "t_relationships")
public class RelationshipsVo {
    @Id
    private Integer cid;

    private Integer mid;

    /**
     * @return cid
     */
    public Integer getCid() {
        return cid;
    }

    /**
     * @param cid
     */
    public void setCid(Integer cid) {
        this.cid = cid;
    }

    /**
     * @return mid
     */
    public Integer getMid() {
        return mid;
    }

    /**
     * @param mid
     */
    public void setMid(Integer mid) {
        this.mid = mid;
    }
}