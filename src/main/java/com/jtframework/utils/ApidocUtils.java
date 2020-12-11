package com.jtframework.utils;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.dao.ServerField;

import java.lang.reflect.Field;

public class ApidocUtils {
    public static void getModelApiDocParams(Object model, int state) {
        if (model != null && model instanceof BaseModel) {
            Field[] fields = model.getClass().getDeclaredFields();
            String str = "";
            String prefix = null;
            if (state == 0) {
                prefix = "* @apiParam {String} ";
            } else {
                prefix = "* @apiSuccess {String} ";
            }

            for (Field field : fields) {
                str += prefix + field.getName() + " " + field.getAnnotation(ServerField.class).name() + " \n";
            }
            System.out.println(str);
        }
    }
}
