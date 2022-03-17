package com.jtframework.utils;


import com.alibaba.fastjson.JSONObject;
import com.jtframework.datasource.redis.RedisService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;


import java.io.File;


/**
 * 微信小程序工具类
 */
@Slf4j
public class WxAppletUtils {
    /**
     * 获取access_token 接口
     */
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";

    /**
     * 获取小程序二维码
     */
    public static final String GET_QR_CODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s";

    /**
     * 获取openid
     */
    public static final String CHECK_LOGIN_CODE_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

    /**
     * 获取access_token
     * access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";
     *
     * @param appid
     * @param secret
     * @return
     */
    public static String getAccessToken(RedisService redisService, String appid, String secret) throws Exception {
        String key = appid + secret;
        if (redisService.hasKey(key)) {
            return redisService.get(key).toString();
        } else {
            String token = getAccessToken(appid, secret);
            redisService.set(key,token , 7160);
            return token;
        }
    }


    /**
     * 获取access_token
     * access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";
     *
     * @param appid
     * @param secret
     * @return
     */
    public static String getAccessToken(String appid, String secret) throws Exception {
        WechatAccessToken accessToken = null;
        String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace("SECRET", secret);


        try {
            return JSONObject.parseObject(HttpClientUtils.executeGet(requestUrl), WechatAccessToken.class).getAccess_token();
        } catch (Exception e) {
            // 获取token失败
            log.error("获取token失败, jsonString:{}", e.getMessage());
            throw e;
        }
    }


    public static WechatLoginDto getOpenIdByCode(String appid, String secret, String code) throws Exception {

        String url = CHECK_LOGIN_CODE_URL.replace("APPID", appid).replace("SECRET", secret).replace("JSCODE", code);

        JSONObject result = HttpClientUtils.get(url, null);

        int errorCode = result.containsKey("errcode") ? result.getInteger("errcode") : 0;
        if (errorCode == 0) {
            WechatLoginDto wechatLoginDto = new WechatLoginDto();
            wechatLoginDto.setOpenId(result.getString("openid"));
            wechatLoginDto.setSessionKey(result.getString("session_key"));
            return wechatLoginDto;
        } else if (errorCode == -1 || errorCode == 45011) {
            throw new Exception("请稍后重试...");
        } else if (errorCode == 40029) {
            throw new Exception("code无效或者失效...");
        } else {
            log.error(result.toJSONString());
            throw new Exception("请联系管理员，CODE:" + code);
        }
    }



    /**
     * 获取小程序码
     *
     * @param token  token
     * @param path   路径
     * @param params 参数
     * @return
     * @throws Exception
     */
    public static byte[] getQrCode(String token, String path, String params) throws Exception {
        return getQrCode(token,path,params,"release");
    }
    /**
     * 获取小程序码
     *
     * @param token  token
     * @param path   路径
     * @param params 参数
     * @param envVersion 版本 正式版为 release，体验版为 trial，开发版为 develop
     * @return
     * @throws Exception
     */
    public static byte[] getQrCode(String token, String path, String params,String envVersion) throws Exception {
        String url = GET_QR_CODE_URL.replace("%s", token);

        JSONObject data = new JSONObject();
        data.put("scene", params);
        data.put("page", path);
        data.put("env_version", envVersion);

        byte[] reuslt= HttpClientUtils.doImgPost(url, data);

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(new String(reuslt));
        }catch (Exception exception){
            return reuslt;
        }

        int errorCode = jsonObject.containsKey("errcode") ? jsonObject.getInteger("errcode") : 0;
        if (errorCode == -1 || errorCode == 45009) {
            throw new Exception("请稍后重试...");
        } else if (errorCode == 41030) {
            throw new Exception("页面不存在...");
        } else {
            log.error(jsonObject.toJSONString());
            throw new Exception("请联系管理员，CODE:" + errorCode);
        }
    }


    /**
     * @author WangJing
     * @Description
     * @date 2021/6/7 23:12
     */
    @ApiModel("token")
    @Data
    public static class WechatAccessToken {

        @ApiModelProperty(value = "access_token")
        private String access_token;

        @ApiModelProperty(value = "超时时间")
        private Integer expires_in;

    }


    /**
     * 微信登录类
     */
    @Data
    public static class WechatLoginDto {

        /**
         * open ID
         */
        @ApiModelProperty(value = "openId")
        private String openId;

        /**
         * uninon id
         */
        @ApiModelProperty(value = "unionId")
        private String unionId;


        /**
         * sesss key
         */
        @ApiModelProperty(value = "sessionKey")
        private String sessionKey;

    }

}
