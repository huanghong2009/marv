package com.jtframework.utils;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.RootDoc;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取对象，属性 的注释
 */
@Slf4j
public class DocletUtils extends Doclet {

    /**
     * Doclet 回调doc
     */
    private static ClassDoc[] rootDoc;

    public static boolean start(RootDoc root) {
        rootDoc = root.classes();
        //注释文档信息，自己爱怎么解析组织就怎么解析了，看自己需求
        return true;
    }

    public static JSONObject getClassDoc(String path) throws NoSuchFieldException, IllegalAccessException {

        log.info("doc路径是：{}", path);

        JSONObject result = new JSONObject();

        com.sun.tools.javadoc.Main.execute(new String[]{"-doclet", DocletUtils.class.getName(), "-encoding", "utf-8", path});

        if (rootDoc == null || rootDoc.length == 0) {
            log.warn(path + " 无ClassDoc信息");
            return result;
        }

        ClassDoc classDoc = rootDoc[0];

        // 获取类的注释
        Object classCommentObj = ReflectUtil.getFieldValue(classDoc, "documentation");
        if (classCommentObj != null){
            // 获取类的名称
            String spitStr = "\n";
            for (String msg : String.valueOf(classCommentObj).split(spitStr)) {
                if (!msg.trim().startsWith("@") && msg.trim().length() > 0) {
                    result.put("classDoc", msg);
                    break;
                }
            }
        }else {
            result.put("classDoc", "");
        }

        // 获取属性名称和注释
        FieldDoc[] fields = classDoc.fields(false);
        JSONObject filesJson = new JSONObject();
        for (FieldDoc field : fields) {
            filesJson.put(field.name(), field.commentText());
        }
        result.put("filedDoc", filesJson);
        return result;
    }

}
