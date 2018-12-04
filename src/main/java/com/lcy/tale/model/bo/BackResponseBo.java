package com.lcy.tale.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by 13 on 2017/2/25.
 */
@Data
public class BackResponseBo implements Serializable {

    private static final long serialVersionUID = -1137170326405360998L;
    private String attachPath;
    private String themePath;
    private String sqlPath;
}
