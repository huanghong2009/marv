package com.jtframework.base.auth;

import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.rest.ServerResponse;
import com.rabbitmq.client.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;


@RequestMapping("/api/sysAuth")
@Slf4j
@Api(tags = "系统鉴权管理")
public abstract class SysAuthController {

    public static final String LOGIN_QUEUE_NAME = "login_queue";


    public static final String LOGOUT_QUEUE_NAME = "logout_queue";

    @Value("${spring.profiles.active}")
    private String env;


    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${socket.localRead:true}")
    private boolean localRead;

    @RabbitListener(queues = LOGIN_QUEUE_NAME)
    public void loginMesage(String data, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("收到socket消息队列:{},消息:{}，正在执行消息队列逻辑", LOGIN_QUEUE_NAME, data);
        JSONObject json = JSONObject.parseObject(data);
        AuthDto authDto = JSONObject.toJavaObject(json, AuthDto.class);
        /**
         * 本地不读消息
         */
        if (env.equals("local") && !localRead){
            channel.basicReject(tag, true);
        }

        try {

            /**
             * 不是自己服务的不读消息
             */
           if (!serviceName.equals(authDto.getServiceName())){
               channel.basicReject(tag, true);
           }

            loginCallback(authDto);

            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理{}socket消息出错:{}",LOGIN_QUEUE_NAME, e.getMessage());
            channel.basicNack(tag, false, true);
        }
    }


    @RabbitListener(queues = LOGOUT_QUEUE_NAME)
    public void logoutMesage(String data, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("收到socket消息队列:{},消息:{}，正在执行消息队列逻辑", LOGOUT_QUEUE_NAME, data);
        JSONObject json = JSONObject.parseObject(data);
        AuthDto authDto = JSONObject.toJavaObject(json, AuthDto.class);
        /**
         * 本地不读消息
         */
        if (env.equals("local") && !localRead){
            channel.basicReject(tag, true);
        }

        try {

            /**
             * 不是自己服务的不读消息
             */
            if (!serviceName.equals(authDto.getServiceName())){
                channel.basicReject(tag, true);
            }

            logoutCallback(authDto);

            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理{}socket消息出错:{}",LOGOUT_QUEUE_NAME, e.getMessage());
            channel.basicNack(tag, false, true);
        }
    }

    /**
     * 根据用户名获取用户
     *
     * @param
     * @return
     */
    @SysAuth(name = "根据用户名获取用户")
    @ApiOperation(value = "根据用户名获取用户")
    @PostMapping("/get_userinfo_by_username")
    public ServerResponse<AuthDto> getUserInfoByUserNameApi(@RequestBody AuthDto authDto) {
        try {
            return ServerResponse.succeed("根据用户名获取用户成功", getUserInfoByUserName(authDto));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("根据用户名获取用户失败", e.getMessage());
        }
    }

    /**
     * 根据手机号获取用户
     *
     * @param
     * @return
     */
    @SysAuth(name = "根据手机号获取用户",type = SysAuth.Type.GET_USER_INFO_BY_PHONE)
    @ApiOperation(value = "根据手机号获取用户")
    @PostMapping("/get_userinfo_by_phone")
    public ServerResponse<AuthDto> getUserInfoByPhoneApi(@RequestBody AuthDto authDto) {
        try {
            return ServerResponse.succeed("根据手机号获取用户成功", getUserInfoByPhone(authDto));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("根据手机号获取用户失败", e.getMessage());
        }
    }

    /**
     * 根据用户名查询
     * @param authDto
     * @return
     */
    public abstract AuthDto getUserInfoByUserName(AuthDto authDto);


    /**
     * 根据手机号查询用户
     * @param authDto
     * @return
     */
    public abstract AuthDto getUserInfoByPhone(AuthDto authDto);

    /**
     * 登录成功的回调方法 [重写覆盖]
     * @throws Exception
     */
    public abstract void loginCallback(AuthDto authDto) throws Exception;

    /**
     * 退出登录的回调方法 [重写覆盖]
     * @throws Exception
     */
    public abstract void logoutCallback(AuthDto authDto) throws Exception;
}
