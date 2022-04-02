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
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.jtframework.base.dao.BaseModel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.SimpleFormatter;

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

    /**
     * 根据excel 模板，生成model代码
     *
     * @throws IOException
     */
    public static void generatorModelCodeByExcel(File excelFile,String packPath) throws Exception {
        generatorModelCodeByExcel(excelFile, 0,packPath);
    }


    /**
     * 根据excel 模板，生成model代码
     *
     * @throws IOException
     */
    public static void generatorModelCodeByExcel(InputStream inputStream, String packPath) throws Exception {
        generatorModelCodeByExcel(inputStream, 0, packPath);
    }

    /**
     * 根据excel 模板，生成model代码
     *
     * @throws IOException
     */
    public static void generatorModelCodeByExcel(InputStream inputStream, int sheetIndex, String packPath) throws Exception {
        ByteArrayOutputStream baos = BaseUtils.cloneInputStream(inputStream);
        inputStream.close();
        ByteArrayInputStream cpInput1 = new ByteArrayInputStream(baos.toByteArray());
        ByteArrayInputStream cpInput2 = new ByteArrayInputStream(baos.toByteArray());

        ExcelReadListener excelReadListener = new ExcelReadListener();
        try {
            ExcelFileUtils.readExcel(cpInput1, null, sheetIndex, excelReadListener);
        } finally {
            cpInput1.close();
        }

        try {
            GenUtil.genExcelModelCode(excelReadListener, cpInput2, sheetIndex, packPath);
        } finally {
            cpInput2.close();
        }

    }


    /**
     * 拼装数据(sheet 名称最好是中文)
     * 1：获取列
     *
     * @param excelReadListener
     * @throws Exception
     */
    public static void genExcelModelCode(ExcelReadListener excelReadListener, InputStream inputStream, int sheetIndex, String packPath) throws Exception {
        Map<String, Object> params = new HashMap<>();
        /**
         * 中文描述
         */
        String sheetName = ExcelFileUtils.getSheetName(inputStream, sheetIndex);
        params.put("moduleName", sheetName);

        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("date", formater.format(new Date()));
        params.put("package",packPath);
        /**
         * 类名 大小写(翻译之后)
         */
        String[] moduleNames = StringUtils.getZhWordName(TranslateUtils.getLanguageTranslate(sheetName));
        params.put("upClassName", moduleNames[1]);
        params.put("changeClassName", moduleNames[0]);

        List<FiledType> filedTypes = new ArrayList<>();

        excelReadListener.head.keySet().stream().forEach(index -> {
            FiledType filedType = new FiledType();
            filedType.setIndex(index);
            String zhName = excelReadListener.head.get(index);
            if (zhName.indexOf("号") > 0) {
                filedType.setType("String");
            } else {
                filedType.setType(getExcelVauleType(excelReadListener.data.get(index)));
            }

            filedType.setDesc(zhName);

            try {
                filedType.setName(StringUtils.getZhWordName(TranslateUtils.getLanguageTranslate(zhName))[0]);

            } catch (Exception e) {
                e.printStackTrace();
                filedType.setName("");
            }
            filedTypes.add(filedType);
        });

        params.put("dataList", filedTypes);


        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("codetemp", TemplateConfig.ResourceMode.CLASSPATH));
        String templateName = "ExcelModel";
        Template template = engine.getTemplate(templateName + ".ftl");
        String packagePath = getPackPath(packPath);
        String filePath = packagePath + "model" + File.separator + moduleNames[1] + "Model.java";
        assert filePath != null;
        File file = new File(filePath);
        genFile(file, template, params);
    }

    /**
     * 判断excel字段类型
     *
     * @return
     */
    private static String getExcelVauleType(String value) {
        if (BaseUtils.isBlank(value)) {
            return "String";
        }

        /**
         * 判断是不是数字
         */
        if (StringUtils.isNumeric(value)) {
            return "BigDecimal";
        } else {
            return "String";
        }
    }

    @Data
    public static class FiledType {
        private int index;
        /**
         * 类型
         */
        private String type;
        /**
         * 变量名
         */
        private String name;
        /**
         * 中文描述
         */
        private String desc;
    }

    /**
     * 根据excel 模板，生成model代码
     *
     * @throws IOException
     */
    public static void generatorModelCodeByExcel(File excelFile, int sheetIndex,String packPath) throws Exception {
        ExcelReadListener excelReadListener = new ExcelReadListener();

        ExcelFileUtils.readExcel(new FileInputStream(excelFile), null, sheetIndex, excelReadListener);
        genExcelModelCode(excelReadListener, new FileInputStream(excelFile), sheetIndex, packPath);

    }


    public static void main(String[] args) throws IOException {
        try {
            GenUtil.generatorModelCodeByExcel(new FileInputStream(new File("/Users/huanghong/Desktop/code/hds_server/src/main/resources/9ac49da8-0709-4b21-8e76-e19efb2a9fef2022-03-31_HistoryOrderInfo.xlsx")),2,"com.jtframework.utils");
        } catch (Exception e) {
            e.printStackTrace();
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
        String packagePath = getPackPath(packPath);


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
     * 获取包路径
     *
     * @param packPath
     * @return
     */
    private static String getPackPath(String packPath) {
        String rootPath = System.getProperty("user.dir");

        String packagePath = rootPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + packPath.replace(".", File.separator) + File.separator;
        return packagePath;
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

    @Data
    @Slf4j
    public static class ExcelReadListener extends AnalysisEventListener<Map<Integer, String>> {
        /**
         * 数据
         */
        public Map<Integer, String> data = null;
        /**
         * 标头
         */
        public Map<Integer, String> head = null;



        @Override
        public void invokeHeadMap(Map headMap, AnalysisContext context) {
            log.info("解析到的表头数据: {}", headMap);
            head = headMap;
        }

        @Override
        public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
            data = integerStringMap;
            throw new ExcelAnalysisStopException("只读一行");
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            System.out.println("读取完毕");

        }

    }


}
