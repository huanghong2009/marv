package com.jtframework.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2018/1/18
 */
@Slf4j
public class HttpClientUtils {

    //封装POST方法
    public JSONObject post(String POST_URL, ArrayList<NameValuePair> list) {
        String entityStr = null;
        try {
            //把参数放入请求体中
            UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(POST_URL);
            httpPost.setEntity(entityParam);
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
            //关闭请求
            httpClient.close();

            return JSONObject.parseObject(entityStr);
        } catch (ClientProtocolException e) {
            log.error("Http协议异常");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO异常");
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject get(String url, Map<String, String> params) throws URISyntaxException {
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
        } catch (ClientProtocolException e) {
            log.error("Http协议异常");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO异常");
            e.printStackTrace();
        }

        return null;
    }
}
