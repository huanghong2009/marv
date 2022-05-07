package com.jtframework.base.service;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.utils.system.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

public class BaseServiceImpl implements CommandLineRunner,BaseService {

    /**
     * 当前环境
     */
    @Value("${spring.profiles.active}")
    public String active;

    @Autowired
    public ApplicationContextProvider applicationContextProvider;

    @Override
    public void init(String... args) throws BusinessException {
    }

    @Override
    public void run(String... args) throws Exception {
        init(args);
    }
}
