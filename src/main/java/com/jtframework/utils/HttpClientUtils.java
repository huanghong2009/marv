package com.jtframework.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2018/1/18
 */
@Slf4j
public class HttpClientUtils {

    //封装POST方法
    public static JSONObject post(String url, JSONObject params) throws Exception{
        String entityStr = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            //把参数放入请求体中
            StringEntity se = new StringEntity(params.toJSONString(), "UTF-8");

            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(se);
            //发起请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取返回状态，并判断是否连接成功。
            if (response.getStatusLine().getStatusCode() == 200) {
                log.info("连接成功");
            } else {
                log.error("连接异常");
            }
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();

            //转换成字符串
            entityStr = EntityUtils.toString(entity, "UTF-8");


            return JSONObject.parseObject(entityStr);
        } catch (Exception e) {
            log.error("Http协议异常:{}",e.getMessage());
            e.printStackTrace();
            throw new Exception("Http协议调用异常:"+e.getMessage());
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
     * @param url 路径
     * @param file 文件
     * @return
     */
    public static JSONObject httpPostUploadFile(String url ,MultipartFile file) throws Exception{
        return httpPostUploadFile(url,file,null,null);
    }


    /**
     * 上传文件
     * @param url 路径
     * @param file 文件
     * @param headerParams
     * @param otherParams
     * @return
     */
    public static JSONObject httpPostUploadFile(String url ,MultipartFile file,Map<String,String>headerParams,Map<String,String>otherParams) throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;
        try {
            String fileName = file.getOriginalFilename();
            HttpPost httpPost = new HttpPost(url);

            if (headerParams != null){
                //添加header
                for (Map.Entry<String, String> e : headerParams.entrySet()) {
                    httpPost.addHeader(e.getKey(), e.getValue());
                }
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            builder.addBinaryBody("file", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流

            if (otherParams!=null){
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
        }  catch (Exception e) {
            log.error("Http上传文件异常:{}",e.getMessage());
            e.printStackTrace();
            throw new Exception("Http上传文件异常:"+e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject get(String url, Map<String, String> params) throws Exception {
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

            return JSONObject.parseObject(entityStr);
        } catch (Exception e) {
            log.error("Http协议调用异常:{}",e.getMessage());
            e.printStackTrace();
            throw new Exception("Http协议调用异常:"+e.getMessage());
        }
    }
}
