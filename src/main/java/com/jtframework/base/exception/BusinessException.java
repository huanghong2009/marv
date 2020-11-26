package com.jtframework.base.exception;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 1721891525581654383L;
    private int code = -3;
    private Object data;

    public BusinessException() {
    }

    public BusinessException(Exception ex) {
        super(ex.getMessage());
        if (ex instanceof BusinessException) {
            this.code = ((BusinessException)ex).getCode();
            this.data = ((BusinessException)ex).getData();
        }

    }

    public BusinessException(Exception ex, String msg) {
        super(msg);
        if (ex instanceof BusinessException) {
            this.code = ((BusinessException)ex).getCode();
            this.data = ((BusinessException)ex).getData();
        }

    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, int code, Object data) {
        super(msg);
        this.code = code;
        this.data = data;
    }

    public String getMessage() {
        return super.getMessage();
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
