package com.jtframework.datasource.mysql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public class SqlUtils {
    public SqlUtils() {
    }

    public static String removeSelect(String sql, int index) {
        String sqlLow = sql.toLowerCase();
        int beginPos = sqlLow.indexOf("from", index);

        if (beginPos == -1) {
            return sql;
        } else {
            String slectStr = sqlLow.substring(index, beginPos);
            int nums = getWordNum("select", slectStr);
            if ((index == 0 && nums <= 1) || (index > 0 && nums == 0)) {
                return sql.substring(beginPos);
            } else {
                return removeSelect(sql, beginPos + 4);
            }
        }
    }


    public static int getWordNum(String word, String str) {
        return getWordNum(word, str, 0, 0);
    }

    public static int getWordNum(String word, String str, int index, int num) {
        int nums = str.indexOf(word, index);
        int indexnum = nums + word.length();

        if (nums == -1) {
            return num;
        } else {
            num++;
            return getWordNum(word, str, indexnum, num);
        }

    }

    public static String removeSelect(String sql) {
        return removeSelect(sql, 0);
    }

    public static String removeOrders(String sql) {
        int lastFromIndex = sql.toLowerCase().lastIndexOf("from");
        int lastIndex = sql.toLowerCase().lastIndexOf(")");
        int beginPos = sql.toLowerCase().lastIndexOf("order by");
        if (beginPos == -1 || beginPos <= lastFromIndex || beginPos < lastIndex) {
            return sql;
        } else {
            return sql.substring(0, beginPos);
        }
    }

    public static void main(String[] args) {
        System.out.println(removeOrders("SELECT  from (SELECT t.*, @rownum := @rownum + 1 AS rownum FROM (SELECT @rownum := 0) r, (SELECT * FROM activity_baby_vote WHERE  state ='AUDIT_PASSED' ORDER BY num DESC) AS t  ) a  left join (SELECT baby_Id,max(create_time) as create_time from activity_baby_vote_log GROUP BY baby_Id ) b on a.id = b.baby_Id ORDER BY a.num desc,b.create_time asc "));
    }
}
