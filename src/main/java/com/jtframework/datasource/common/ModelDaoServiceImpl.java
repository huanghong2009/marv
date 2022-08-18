package com.jtframework.datasource.common;

import com.jtframework.base.service.BaseServiceImpl;
import com.jtframework.utils.AnnotationUtils;
import com.jtframework.utils.ClassUtils;

import java.io.Serializable;

public abstract class ModelDaoServiceImpl<T> extends BaseServiceImpl implements ModelDaoService, Serializable {
    public String tableName;

    public String desc;

    public Class cls;

    public ModelDaoServiceImpl() {
        Class cls = ClassUtils.getTClass(this);
        this.cls = cls;
        this.tableName = AnnotationUtils.getServeModelValue(cls);
        this.desc = AnnotationUtils.getServeModelDesc(cls);
    }



}
