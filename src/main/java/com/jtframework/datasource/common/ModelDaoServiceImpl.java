package com.jtframework.datasource.common;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.service.BaseServiceImpl;
import com.jtframework.utils.BaseUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;

public abstract class ModelDaoServiceImpl<T> extends BaseServiceImpl implements ModelDaoService, Serializable {
    public String name;

    public Class cls;

    public ModelDaoServiceImpl() {
        Class cls = getTClass();
        this.cls = cls;
        this.name = BaseUtils.getServeModelDesc(cls);
    }

    public Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }



}
