package com.jtframework.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
public class PrimaryUtil {
    private static final long EPOCH = 1479533469598L; //开始时间,固定一个小于当前时间的毫秒数
    private static final int max12bit = 4095;
    private static final long max41bit= 1099511627775L;
    private static String machineId = "121" ; // 机器ID

    private final static String str = "1234567890abcdefghijklmnopqrstuvwxyz";
    private final static int pixLen = str.length();
    private static volatile int pixOne = 0;
    private static volatile int pixTwo = 0;
    private static volatile int pixThree = 0;
    private static volatile int pixFour = 0;
    /**
     * 生成主键(16位数字)
     * 主键生成方式,年月日时分秒毫秒的时间戳+四位随机数保证不重复
     */

    final public synchronized static String getId() {
        StringBuilder sb = new StringBuilder();// 创建一个StringBuilder
        sb.append(Long.toHexString(System.currentTimeMillis()));// 先添加当前时间的毫秒值的16进制
        pixFour++;
        if (pixFour == pixLen) {
            pixFour = 0;
            pixThree++;
            if (pixThree == pixLen) {
                pixThree = 0;
                pixTwo++;
                if (pixTwo == pixLen) {
                    pixTwo = 0;
                    pixOne++;
                    if (pixOne == pixLen) {
                        pixOne = 0;
                    }
                }
            }
        }
        return sb.append(str.charAt(pixOne)).append(str.charAt(pixTwo)).append(str.charAt(pixThree)).append(str.charAt(pixFour)).toString();
    }


    /**
     *
     * 创建ID
     *
     * @return
     *
     */
    public synchronized static String getIntId(){

        long time = System.currentTimeMillis() - EPOCH  + max41bit;
        // 二进制的 毫秒级时间戳
        String base = Long.toBinaryString(time);

        // 序列数
        String randomStr = StringUtils.leftPad(Integer.toBinaryString(new Random().nextInt(max12bit)),12,'0');
        if(StringUtils.isNotEmpty(machineId)){
            machineId = StringUtils.leftPad(machineId, 10, '0');
        }

        //拼接
        String appendStr = base + machineId + randomStr;
        // 转化为十进制 返回
        BigInteger bi = new BigInteger(appendStr, 2);

        return  Long.valueOf(bi.toString())+"";
    }

}




