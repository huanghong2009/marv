package com.jtframework.base.query;

import com.jtframework.base.dao.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class ParamsDTO<T extends BaseModel> implements Serializable {

    /**
     * 第几页
     */
    @ApiModelProperty("去第几页")
    private Integer toPage;

    @ApiModelProperty("每页多少条")
    private Integer pageSize;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private String sortFiled;

    /**
     * 是否倒序
     */
    @ApiModelProperty("是否要倒序")
    private boolean isDesc;



    public ParamsDTO() {
        initPage();
    }

    private void initPage() {
        this.toPage = 1;
        this.pageSize = 10;
    }
}
