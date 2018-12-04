package com.lcy.tale.model.entity;

import javax.persistence.*;

@Table(name = "t_attach")
public class AttachVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fname;

    private String ftype;

    private String fkey;

    @Column(name = "author_id")
    private Integer authorId;

    private Integer created;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fname
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return ftype
     */
    public String getFtype() {
        return ftype;
    }

    /**
     * @param ftype
     */
    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    /**
     * @return fkey
     */
    public String getFkey() {
        return fkey;
    }

    /**
     * @param fkey
     */
    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    /**
     * @return author_id
     */
    public Integer getAuthorId() {
        return authorId;
    }

    /**
     * @param authorId
     */
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    /**
     * @return created
     */
    public Integer getCreated() {
        return created;
    }

    /**
     * @param created
     */
    public void setCreated(Integer created) {
        this.created = created;
    }
}