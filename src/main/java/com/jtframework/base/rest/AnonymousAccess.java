/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jtframework.base.rest;

import com.jtframework.utils.BaseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * @author jacky
 *  用于标记匿名访问方法
 */
@Inherited
@Documented
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnonymousAccess {


//    Method method = signature.getMethod();
//    boolean state = false;
//
//    GetMapping getMapping = method.getAnnotation(GetMapping.class);
//        if (getMapping != null && getMapping.value().length != 0 && redisServiceInit.getRedisService()!=null && BaseUtils.isNotBlank(getMapping.value()[0])) {
//        redisServiceInit.getRedisService().lSet("UNAUTH_URLS",getMapping.value()[0]);
//        state = true;
//    }
//
//        if (!state){
//        PostMapping postMapping = method.getAnnotation(PostMapping.class);
//        if (postMapping != null && postMapping.value().length != 0 && redisServiceInit.getRedisService()!=null && BaseUtils.isNotBlank(postMapping.value()[0])) {
//            redisServiceInit.getRedisService().lSet("UNAUTH_URLS",postMapping.value()[0]);
//        }
//        state = true;
//    }
//
//        if (!state){
//        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
//        if (requestMapping != null && requestMapping.value().length != 0 && redisServiceInit.getRedisService()!=null && BaseUtils.isNotBlank(requestMapping.value()[0])) {
//            redisServiceInit.getRedisService().lSet("UNAUTH_URLS",requestMapping.value()[0]);
//        }
//    }
}
