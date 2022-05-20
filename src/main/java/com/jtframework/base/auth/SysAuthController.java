package com.jtframework.base.auth;

import com.jtframework.base.rest.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/sysAuth")
@Slf4j
@Api(tags = "系统鉴权管理")
public abstract class SysAuthController {


    /**
     * 根据用户名获取用户
     *
     * @param
     * @return
     */
    @SysAuth(name = "根据用户名获取用户")
    @ApiOperation(value = "根据用户名获取用户")
    @GetMapping("/get_userinfo_by_username")
    public ServerResponse getUserInfoByUserNameApi(AuthDto authDto) {
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
    @GetMapping("/get_userinfo_by_phone")
    public ServerResponse getUserInfoByPhoneApi(AuthDto authDto) {
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
}
