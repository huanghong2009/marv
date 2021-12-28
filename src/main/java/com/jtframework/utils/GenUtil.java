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
package com.jtframework.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.template.*;
import com.jtframework.base.dao.BaseModel;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成
 *
 * @author Zheng Jie
 * @date 2019-01-02
 */
@Slf4j
public class GenUtil {


    /**
     * 获取后端代码模板名称
     *
     * @return List
     */
    private static List<String> getAdminTemplateNames(Type type) {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("Dto");
        templateNames.add("Controller");
        if (type.equals(Type.MYSQL)) {
            templateNames.add("MysqlService");
            templateNames.add("MysqlServiceImpl");
        } else {
            templateNames.add("MongoService");
            templateNames.add("MongoServiceImpl");
        }

        return templateNames;
    }


    /**
     * 代码生成
     *
     * @throws IOException
     */
    public static void generatorCode(Class<? extends BaseModel> clazz, Type type) throws IOException {
        Map<String, Object> genMap = getGenMap(clazz, type);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("codetemp", TemplateConfig.ResourceMode.CLASSPATH));
        // 生成后端代码
        List<String> templates = getAdminTemplateNames(type);
        for (String templateName : templates) {
            Template template = engine.getTemplate(templateName + ".ftl");

            String filePath = getAdminFilePath(templateName, genMap.get("name").toString(), genMap.get("package").toString());
            assert filePath != null;
            File file = new File(filePath);

            // 生成代码
            genFile(file, template, genMap);
        }


    }

    // 获取模版数据
    private static Map<String, Object> getGenMap(Class<? extends BaseModel> clazz, Type type) {
        // 存储模版字段数据
        Map<String, Object> genMap = new HashMap<>();

        String classPackPath = clazz.getPackage().getName();
        String upperStoryPackPath = classPackPath.substring(0, classPackPath.lastIndexOf("."));
        genMap.put("moduleName", BaseUtils.getServeModelDesc(clazz));

        // 包名称
        genMap.put("package", upperStoryPackPath);
        // 创建日期
        genMap.put("date", LocalDate.now().toString());


        String className = clazz.getSimpleName();

        String name = className.indexOf("Model") == -1 ? className : className.substring(0, className.indexOf("Model"));

        // 保存类名
        genMap.put("name", name);


        // 小写开头的类名
        String changeClassName = StringUtils.toCamelCase(name);

        // 保存小写开头的类名
        genMap.put("changeClassName", changeClassName);

        if (type.equals(Type.MONGODB)) {
            genMap.put("dtoType", "MongodbParamsDTO");
        } else {
            genMap.put("dtoType", "ParamsDTO");
        }

        genMap.put("hasName", "N");
        genMap.put("hasType", "N");
        genMap.put("hasState", "N");
        genMap.put("hasStatus", "N");

        for (Field field : ReflectUtil.getFields(clazz)) {
            String filedName = ReflectUtil.getFieldName(field);
            if (filedName.equals("name")) {
                genMap.put("hasName", "Y");
            } else if (filedName.equals("type")) {
                genMap.put("hasType", "Y");
            } else if (filedName.equals("state")) {
                genMap.put("hasState", "Y");
            } else if (filedName.equals("status")) {
                genMap.put("hasStatus", "Y");
            }
        }

        return genMap;
    }

    /**
     * 获取类的路径
     *
     * @param clazz
     * @return
     */
    private static String getResourcePath(Class<? extends BaseModel> clazz) {
        String className = clazz.getName();
        String classNamePath = className.replace(".", "/") + ".class";
        URL is = GenUtil.class.getClassLoader().getResource(classNamePath);
        String path = is.getFile();
        path = StringUtils.replace(path, "%20", " ");

        return StringUtils.removeStart(path, "/");
    }

    /**
     * 定义后端文件路径以及名称
     */
    private static String getAdminFilePath(String templateName, String name, String packPath) {
        String rootPath = System.getProperty("user.dir");

        String packagePath = rootPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + packPath.replace(".", File.separator) + File.separator;


        if ("Controller".equals(templateName)) {
            return packagePath + "rest" + File.separator + name + "Controller.java";
        }

        if ("MysqlService".equals(templateName)) {
            return packagePath + "service" + File.separator + name + "Service.java";
        }

        if ("MysqlServiceImpl".equals(templateName)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + name + "ServiceImpl.java";
        }

        if ("MongoService".equals(templateName)) {
            return packagePath + "service" + File.separator + name + "Service.java";
        }

        if ("MongoServiceImpl".equals(templateName)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + name + "ServiceImpl.java";
        }

        if ("Dto".equals(templateName)) {
            return packagePath + "service" + File.separator + "dto" + File.separator + name + "Dto.java";
        }


        return null;
    }


    /**
     * 根据模板生成文件
     *
     * @param file
     * @param template
     * @param map
     * @throws IOException
     */
    private static void genFile(File file, Template template, Map<String, Object> map) throws IOException {
        // 生成目标文件
        Writer writer = null;
        try {
            FileUtil.touch(file);
            writer = new FileWriter(file);
            template.render(map, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert writer != null;
            writer.close();
        }
    }


    public enum Type {
        MYSQL("mysql"),

        MONGODB("mongodb");

        private String desc;

        Type(String field) {
            this.desc = field;
        }

        public String getState() {
            return desc;
        }
    }
}
