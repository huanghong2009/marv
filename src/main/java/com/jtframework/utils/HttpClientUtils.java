package com.jtframework.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2018/1/18
 */
@Slf4j
public class HttpClientUtils {
    public static final int cache = 10 * 1024;


    /**
     * post请求
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static JSONObject post(String url, JSONObject params) throws Exception {
        return JSONObject.parseObject(EntityUtils.toString(executePost(url, params), "UTF-8"));
    }


    /**
     * 获取数据流
     *
     * @param url
     * @param paraMap
     * @return
     */
    public static byte[] doImgPost(String url, JSONObject paraMap) throws Exception {
        byte[] result = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        try {
            // 设置请求的参数
            JSONObject postData = new JSONObject();
            for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
                postData.put(entry.getKey(), entry.getValue());
            }
            httpPost.setEntity(new StringEntity(postData.toString(), "UTF-8"));
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            entity = new BufferedHttpEntity(entity);

            result = EntityUtils.toByteArray(entity);
        } catch (ConnectionPoolTimeoutException e) {
            e.printStackTrace();
            throw new Exception("请求图片出错");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new Exception("请求图片出错");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new Exception("请求图片出错");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("请求图片出错");
        } finally {
            httpPost.releaseConnection();
        }
        return result;
    }


    /**
     * 执行post请求
     *
     * @param url
     * @param params
     * @param
     * @return
     * @throws Exception
     */
    public static HttpEntity executePost(String url, JSONObject params) throws Exception {
        return executePost(url, params, false);
    }

    /**
     * 执行post请求
     *
     * @param url
     * @param params
     * @param isDownloadFile 是否是文件
     * @return
     * @throws Exception
     */
    public static HttpEntity executePost(String url, JSONObject params, boolean isDownloadFile) throws Exception {
        String entityStr = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            //把参数放入请求体中
            StringEntity entity = new StringEntity(params.toJSONString(), "UTF-8");

            if (isDownloadFile) {
                entity.setContentType("image/png");
            }

            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(entity);
            //发起请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取返回状态，并判断是否连接成功。
            if (response.getStatusLine().getStatusCode() == 200) {
                log.info("连接成功");
            } else {
                log.error("连接异常");
            }
            // 获得响应的实体对象
            return response.getEntity();
        } catch (Exception e) {
            log.error("Http协议异常:{}", e.getMessage());
            e.printStackTrace();
            throw new Exception("Http协议调用异常:" + e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 上传文件
     *
     * @param url  路径
     * @param file 文件
     * @return
     */
    public static JSONObject httpPostUploadFile(String url, MultipartFile file) throws Exception {
        return httpPostUploadFile(url, file, null, null);
    }


    /**
     * 上传文件
     *
     * @param url          路径
     * @param file         文件
     * @param headerParams
     * @param otherParams
     * @return
     */
    public static JSONObject httpPostUploadFile(String url, MultipartFile file, Map<String, String> headerParams, Map<String, String> otherParams) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;
        try {
            String fileName = file.getOriginalFilename();
            HttpPost httpPost = new HttpPost(url);

            if (headerParams != null) {
                //添加header
                for (Map.Entry<String, String> e : headerParams.entrySet()) {
                    httpPost.addHeader(e.getKey(), e.getValue());
                }
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            builder.addBinaryBody("file", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流

            if (otherParams != null) {
                for (Map.Entry<String, String> e : otherParams.entrySet()) {
                    builder.addTextBody(e.getKey(), e.getValue());// 类似浏览器表单提交，对应input的name和value
                }
            }


            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }

            return JSONObject.parseObject(result);
        } catch (Exception e) {
            log.error("Http上传文件异常:{}", e.getMessage());
            e.printStackTrace();
            throw new Exception("Http上传文件异常:" + e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 执行get请求
     *
     * @return
     */
    public static JSONObject get(String url) throws Exception {
        return get(url, null);
    }


    /**
     * 执行get请求
     *
     * @return
     */
    public static JSONObject get(String url, Map<String, String> params) throws Exception {
        return JSONObject.parseObject(executeGet(url, params));
    }

    /**
     * 执行get请求
     *
     * @return
     */
    public static String executeGet(String url) throws Exception {
        return executeGet(url, null);
    }


    /**
     * 执行get请求
     *
     * @return
     */
    public static String executeGet(String url, Map<String, String> params) throws Exception {
        CloseableHttpResponse response = null;
        String entityStr = null;
        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            URIBuilder uriBuilder = new URIBuilder(url);

            if (null != params && params.keySet().size() > 0) {
                ArrayList<NameValuePair> list = new ArrayList<>();

                for (String key : params.keySet()) {
                    list.add(new BasicNameValuePair(key, params.get(key)));
                }
                uriBuilder.setParameters(list);
            }

            //根据带参数的URI对象构建GET请求对象
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            //执行请求
            response = httpClient.execute(httpGet);
            //获得响应的实体对象
            HttpEntity entity = response.getEntity();
            //转换成字符串
            entityStr = EntityUtils.toString(entity, "UTF-8");

            //关闭请求
            httpClient.close();

            return entityStr;
        } catch (Exception e) {
            log.error("Http协议调用异常:{}", e.getMessage());
            e.printStackTrace();
            throw new Exception("Http协议调用异常:" + e.getMessage());
        }
    }

    /**
     * 根据url下载文件
     *
     * @param url
     * @return
     */
    public static InputStream downloadInputStream(String url)throws Exception {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件下载失败");
        }
    }





    /**
     * 根据url下载文件
     *
     * @param url
     * @return
     */
    public static File downloadFile(String url)throws Exception {
        File file = null;
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String[] urlSplit = url.split("/");
            String fileName = urlSplit[urlSplit.length -1];

            file = new File(fileName);
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer = new byte[cache];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();


        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件下载失败");
        }
        return file;
    }
}
