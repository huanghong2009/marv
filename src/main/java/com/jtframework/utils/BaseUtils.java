package com.jtframework.utils;

import com.jtframework.base.dao.ServerModel;
import com.jtframework.utils.tools.SHA256;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public final class BaseUtils {



    /**
     * 获取当前机器的IP
     *
     * @return /
     */
    public static String getLocalIp() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface anInterface = interfaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<InetAddress> inetAddresses = anInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddresses.nextElement();
                    // 排除loopback类型地址
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                return "";
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 克隆流
     * @param input
     * @return
     */
    public static ByteArrayOutputStream cloneInputStream(InputStream input) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 克隆流
     * @param input
     * @return
     */
    public static List<InputStream> cloneInputStream(InputStream input,int size) {
        List<InputStream> inputStreams = new ArrayList<>();
        ByteArrayOutputStream baos = cloneInputStream(input);
        for (int i = 0; i < size; i++) {
            inputStreams.add(new ByteArrayInputStream(baos.toByteArray()));
        }
        return inputStreams;
    }



    /**
     * 将List<String>集合 转化为String
     * 如{"aaa","bbb"} To 'aaa','bbb'
     */
    public static String convertListToString(List<String> strlist) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(strlist)) {
            for (int i = 0; i < strlist.size(); i++) {
                if (i == 0) {
                    sb.append("'").append(strlist.get(i)).append("'");
                } else {
                    sb.append(",").append("'").append(strlist.get(i)).append("'");
                }
            }
        }
        return sb.toString();

    }


    /**
     * 保存图片
     *
     * @param instreams
     * @param fileName
     * @return
     */
    public static File readInpuStreamToFile(InputStream instreams, String fileName) throws Exception {
        if (instreams == null || BaseUtils.isBlank(fileName)) {
            throw new Exception("缺少必要参数");
        }

        return readInpuStreamToFile(instreams, new File(fileName));
    }


    /**
     * 保存图片
     *
     * @param instreams
     * @param file
     * @return
     */
    public static File readInpuStreamToFile(InputStream instreams, File file) throws Exception {
        if (instreams == null || null == file) {
            throw new Exception("缺少必要参数");
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int nRead = 0;
            while ((nRead = instreams.read(b)) != -1) {
                fos.write(b, 0, nRead);
            }
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("读取流失败:" + e.getMessage() + "...");
        }

    }


    /**
     * 判断在不在arry list 里面
     *
     * @param key
     * @param arry
     * @return
     */
    public static boolean searchInArray(String key, String[] arry) {
        if (arry != null && arry.length > 0) {
            String[] var2 = arry;
            for (String str : arry) {
                if (key.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }



    public static boolean isNotBlank(String str) {
        return StringUtil.isNotBlank(str);
    }

    public static boolean isBlank(String str) {
        return StringUtil.isBlank(str);
    }



    /**
     * 获取异常栈字符串
     *
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }


    /**
     * 获得一个随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    /**
     * get请求参数拼接
     *
     * @param ip     ip地址
     * @param parmas
     * @return
     */
    public static String httpGetParamsSplic(String ip, Map<String, String> parmas) {
        Set<String> keys = parmas.keySet();
        if (keys.size() == 0) {
            return ip;
        }
        ip += "?";
        for (String key : keys) {
            ip = ip + key + "=" + parmas.get(key) + "&";
        }
        ip = ip.substring(0, ip.length() - 1);

        return ip;
    }




    /**
     * bute合并
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {

        byte[] data3 = new byte[data1.length + data2.length];

        System.arraycopy(data1, 0, data3, 0, data1.length);

        System.arraycopy(data2, 0, data3, data1.length, data2.length);

        return data3;
    }

    /**
     * 密码加盐
     *
     * @param password 用户密码
     * @param salt     盐
     * @return passwordBuf 加盐后的密码
     * @throws null
     */
    public static String addSalt(String password, String salt) {
        StringBuffer passwordBuf = new StringBuffer(password);
        List<String> saltBranch = new ArrayList<>();
        int part = salt.length() / SHA256.SALT_PART_NUMBER;
        for (int i = 0, k = 0; ; i = i + part, k++) {
            if (k != SHA256.SALT_PART_NUMBER - 1) {
                saltBranch.add(salt.substring(i, part + i));
            } else {
                saltBranch.add(salt.substring(i, salt.length()));
                break;
            }
        }

        boolean bool = true;
        int l = 0;
        while (bool) {
            if (passwordBuf.length() < 16) {
                passwordBuf.append(saltBranch.get(l));
                l++;
            } else {
                for (int p = 0; l < 5; l++) {
                    passwordBuf.insert(p, saltBranch.get(l));
                    p = saltBranch.get(l).length() + p + 3;
                }
                bool = false;
            }
        }

        return passwordBuf.toString();
    }



    /**
     * 读取一个文本 一行一行读取
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readTxtFile(String path) throws IOException {
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new ArrayList<String>();
        FileInputStream fis = new FileInputStream(path);
        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
            if (line.lastIndexOf("---") < 0) {
                list.add(line);
            }
        }
        br.close();
        isr.close();
        fis.close();
        return list;
    }


    /**
     * 切分list
     *
     * @param sourceList
     * @param groupSize  每组定长
     * @return
     */
    public static List<List> splitList(List sourceList, int groupSize) {
        int length = sourceList.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }



}
