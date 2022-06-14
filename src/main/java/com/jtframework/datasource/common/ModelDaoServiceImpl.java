package com.jtframework.datasource.common;

import com.jtframework.base.service.BaseServiceImpl;
import com.jtframework.utils.AnnotationUtils;
import com.jtframework.utils.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public abstract class ModelDaoServiceImpl<T> extends BaseServiceImpl implements ModelDaoService, Serializable {
    public String name;

    public Class cls;

    public ModelDaoServiceImpl() {
        Class cls = ClassUtils.getTClass(this);
        this.cls = cls;
        this.name = AnnotationUtils.getServeModelDesc(cls);
    }



}
