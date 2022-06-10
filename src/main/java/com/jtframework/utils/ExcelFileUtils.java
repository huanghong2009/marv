package com.jtframework.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.analysis.ExcelReadExecutor;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ExcelFileUtils {


    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }


    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地临时文件
     *
     * @param file
     */
    public static void delteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }

    /**
     * 读excel
     *
     * @param file
     * @param cls
     * @param analysisEventListener
     */
    public static void readExcel(File file, Class cls, AnalysisEventListener analysisEventListener) {
        try {
            EasyExcel.read(file, cls, analysisEventListener).sheet().doRead();
        } catch (ExcelDataConvertException excelDataConvertException) {
            excelDataConvertException.printStackTrace();
            throw new RuntimeException("数据格式有问题：" + excelDataConvertException.getRowIndex() + " 行，" + excelDataConvertException.getColumnIndex() + "列，数据是:" + excelDataConvertException.getCellData() == null ? "null" : JSONObject.toJSONString(excelDataConvertException.getCellData()));
        }


    }

    /**
     * 读excel
     *
     * @param file
     * @param cls
     * @param analysisEventListener
     */
    public static void readExcel(File file, Class cls, int sheetIndex, AnalysisEventListener analysisEventListener) {
        try {
            EasyExcel.read(file, cls, analysisEventListener).sheet(sheetIndex).doRead();
        } catch (ExcelDataConvertException excelDataConvertException) {
            excelDataConvertException.printStackTrace();
            throw new RuntimeException("数据格式有问题：" + excelDataConvertException.getRowIndex() + " 行，" + excelDataConvertException.getColumnIndex() + "列，数据是:" + excelDataConvertException.getCellData() == null ? "null" : JSONObject.toJSONString(excelDataConvertException.getCellData()));
        }

    }

    /**
     * 获取sheet 名称
     *
     * @param inputStream
     * @return
     */
    public static Map<Integer, String> getAllSheetName(InputStream inputStream) throws Exception {

        List<ReadSheet> sheets = EasyExcel.read(inputStream).build().excelExecutor().sheetList();
        Map<Integer, String> result = new HashMap<>();
        for (ReadSheet sheet : sheets) {
            result.put(sheet.getSheetNo(), sheet.getSheetName());
        }
        return result;
    }

    /**
     * 获取第一个sheet 名称
     *
     * @param inputStream
     * @return
     */
    public static String getFirstSheetName(InputStream inputStream) throws Exception {
        return getSheetName(inputStream, 0);
    }


    /**
     * 获取第一个sheet 名称
     *
     * @param inputStream
     * @return
     */
    public static String getSheetName(InputStream inputStream, int index) throws Exception {
//        WorkbookFactory.create(inputStream);
        ExcelReadExecutor excelReader = EasyExcel.read(inputStream).build().excelExecutor();
        List<ReadSheet> sheets = excelReader.sheetList();
        return getSheetName(index, sheets);
    }

    /**
     * 获取sheet名称
     *
     * @param index
     * @param sheets
     * @return
     * @throws Exception
     */
    private static String getSheetName(int index, List<ReadSheet> sheets) throws Exception {
        if (sheets.size() - 1 < index) {
            throw new Exception("sheet 下表越界");
        }
        return sheets.get(index).getSheetName();
    }

    /**
     * 获取sheet 名称
     *
     * @param file
     * @return
     */
    public static Map<Integer, String> getAllSheetName(File file) {
        List<ReadSheet> sheets = EasyExcel.read(file).build().excelExecutor().sheetList();
        Map<Integer, String> result = new HashMap<>();
        for (ReadSheet sheet : sheets) {
            result.put(sheet.getSheetNo(), sheet.getSheetName());
        }
        return result;
    }

    /**
     * 获取第一个sheet 名称
     *
     * @param file
     * @return
     */
    public static String getFirstSheetName(File file) throws Exception {

        return getSheetName(file, 0);
    }

    /**
     * 获取第一个sheet 名称
     *
     * @param file
     * @return
     */
    public static String getSheetName(File file, int index) throws Exception {
        return getSheetName(new FileInputStream(file), index);
    }


    /**
     * 读excel
     *
     * @param inputStream
     * @param cls
     * @param analysisEventListener
     */
    public static void readExcel(InputStream inputStream, Class cls, int sheetIndex, AnalysisEventListener analysisEventListener) {

        try {
            EasyExcel.read(inputStream, cls, analysisEventListener).sheet(sheetIndex).doRead();
        } catch (ExcelDataConvertException excelDataConvertException) {
            excelDataConvertException.printStackTrace();
            throw new RuntimeException("数据格式有问题：" + excelDataConvertException.getRowIndex() + " 行，" + excelDataConvertException.getColumnIndex() + "列，数据是:" + excelDataConvertException.getCellData() == null ? "null" : JSONObject.toJSONString(excelDataConvertException.getCellData()));
        }

    }

    /**
     * 读excel
     *
     * @param file
     * @param cls
     * @param analysisEventListener
     */
    public static void readExcel(File file, Class cls, String sheetName, AnalysisEventListener analysisEventListener) {
        try {
            EasyExcel.read(file, cls, analysisEventListener).sheet(sheetName).doRead();
        } catch (ExcelDataConvertException excelDataConvertException) {
            excelDataConvertException.printStackTrace();
            throw new RuntimeException("数据格式有问题：" + excelDataConvertException.getRowIndex() + " 行，" + excelDataConvertException.getColumnIndex() + "列，数据是:" + excelDataConvertException.getCellData() == null ? "null" : JSONObject.toJSONString(excelDataConvertException.getCellData()));
        }


    }


    /**
     * 排除字段列表
     *
     * @param response
     * @param data
     * @param resultClass
     * @param excludeColumn
     * @throws IOException
     */
    public static void export(HttpServletResponse response, List data, Class resultClass, Set<String> excludeColumn) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(response.getOutputStream(), resultClass);
        if (excludeColumn != null && !excludeColumn.isEmpty()) {
            excelWriterBuilder.excludeColumnFiledNames(excludeColumn);
        }

        excelWriterBuilder.sheet("sheet0")
                // 设置字段宽度为自动调整，不太精确
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(data);
    }


    /**
     * 导出excel（一个sheet）
     *
     * @param response
     */
    public static void export(HttpServletResponse response, List data, Class resultClass) throws IOException {
        export(response, data, resultClass, null);
    }

    /**
     * 导出excel（多个sheet）
     * map string 为sheet 名称
     *
     * @param response
     */
    public static void exports(HttpServletResponse response, Map<String, List> datas, Class resultClass) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), resultClass).build();

        int num = 0;
        for (String key : datas.keySet()) {
            //获取sheet0对象
            WriteSheet mainSheet = EasyExcel.writerSheet(num, key).head(resultClass).build();
            //向sheet0写入数据 传入空list这样只导出表头
            excelWriter.write(datas.get(key), mainSheet);
            num++;
        }
        excelWriter.finish();
    }

}
