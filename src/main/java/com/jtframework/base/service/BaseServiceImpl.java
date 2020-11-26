package com.jtframework.base.service;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.system.ApplicationContextProvider;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;

public class BaseServiceImpl implements CommandLineRunner {
    @Resource
    ApplicationContextProvider applicationContextProvider;

    public void init(String... args) throws BusinessException {
    }

    @Override
    public void run(String... args) throws Exception {
        init(args);
    }
}
