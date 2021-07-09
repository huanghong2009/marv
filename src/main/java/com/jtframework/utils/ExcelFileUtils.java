package com.jtframework.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

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
        EasyExcel.read(file, cls, analysisEventListener).sheet().doRead();
    }


    /**
     * 导出excel（一个sheet）
     *
     * @param response
     */
    public static void export(HttpServletResponse response, List data,Class resultClass) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), resultClass)
                .sheet("sheet0")
                // 设置字段宽度为自动调整，不太精确
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(data);
    }

    /**
     * 导出excel（多个sheet）
     * map string 为sheet 名称
     * @param response
     */
    public static void exports(HttpServletResponse response, Map<String,List> datas,Class resultClass) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(),resultClass).build();

        int num = 0 ;
        for (String key : datas.keySet()) {
            //获取sheet0对象
            WriteSheet mainSheet = EasyExcel.writerSheet(num, key).head(resultClass).build();
            //向sheet0写入数据 传入空list这样只导出表头
            excelWriter.write(datas.get(key),mainSheet);
            num ++;
        }

        excelWriter.finish();

    }


    //excel 导入 Demo

//    @Data
//    public class DemoListener extends AnalysisEventListener<Stone> {
//        private List<Stone> stoneList;
//
//        public DemoListener() {
//            stoneList = new ArrayList<>();
//        }
//
//        // 读取第一行表头内容
//        @Override
//        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
//            System.out.println("表头信息" + headMap);
//        }
//
//        /**
//         * When analysis one row trigger invoke function.
//         *
//         * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
//         * @param context
//         */
//        @Override
//        public void invoke(Stone data, AnalysisContext context) {
//            this.stoneList.add(data);
////        System.out.println(data.toString());
//        }
//
//        /**
//         * if have something to do after all analysis
//         *
//         * @param context
//         */
//        @SneakyThrows
//        @Override
//        public void doAfterAllAnalysed(AnalysisContext context) {
//            System.out.println("执行完成");
//        }
//
//
//    }
//
//    @Data
//    public class Stone {
//
//        private int id;
//        private String name;            //'产品名称',
//
//
//        @ExcelProperty(value = "货号", index = 0)
//        private String serialNumber;        //货号
//
//        @ExcelProperty(value = "形状", index = 1)
//        private String shape;           //'形状',
//
//        @ExcelProperty(value = "重量", index = 2)
//        private String stoneWeight;     //'石重',
//
//        @ExcelProperty(value = "颜色", index = 3)
//        private String color;           //'颜色',
//
//        @ExcelProperty(value = "净度", index = 4)
//        private String neatness;        //'净度',
//
//        @ExcelProperty(value = "切工", index = 5)
//        private String cut;             //'切工',
//
//
//        @ExcelProperty(value = "抛光", index = 6)
//        private String polishing;       //'抛光',
//
//        @ExcelProperty(value = "对称", index = 7)
//        private String symmetry;        //'对称',
//
//        @ExcelProperty(value = "荧光强度", index = 8)
//        private String fluoreStrength;  //'荧光强度'
//
//        @ExcelProperty(value = "证书", index = 9)
//        private String certificateClassify; //'证书类型（裸石）',
//
//        @ExcelProperty(value = "证书号", index = 10)
//        private String certificateNo1;         //'证书号1',
//
//        @ExcelProperty(value = "成本", index = 11)
//        private String basePrice;          //'基础价',
//
//        @ExcelProperty(value = "地点", index = 12)
//        private String source;              //全球钻/现货
//    }

}
