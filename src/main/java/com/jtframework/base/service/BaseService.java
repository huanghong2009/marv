package com.jtframework.base.service;

import com.jtframework.base.exception.BusinessException;

import java.util.List;

public interface BaseService{
    void init(String... args) throws BusinessException;

}
