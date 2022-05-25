package com.jtframework.base.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AuthDto implements Serializable {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "登录id")
    private String loginId;

    @ApiModelProperty(value = "鉴权id")
    private String authId;

    @ApiModelProperty(value = "url")
    private String authUrl;

    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @ApiModelProperty(value = "账户信息")
    private Object accountInfo;

    @ApiModelProperty(value = "权限")
    private Object competence;
}
