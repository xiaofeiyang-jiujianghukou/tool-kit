package com.vision.tool.kit.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnum {
    SUCCESS("success", "", "成功"),
    SUCCESS0000("0000", "", "成功"),
    BIZ_EXCEPTION("10", "00", "业务异常"),
    ILLEGAL_ARGUMENTS("20", "00", "非法参数"),
    DATA_NOT_FOUND("30", "00", "数据不存在"),
    SIGNATURE_NOT_VALID("40", "00", "无效签名"),
    MULTI_THREAD("50", "00", "操作频繁"),
    THIRD_REQ_EXCEPTION("60", "00", "请求三方接口异常"),
    USER_NOT_SIGN_IN("70", "00", "用户未登录"),
    TOKEN_TYPE_EXCEPTION("70", "01", "用户未登录"),
    TOKEN_GET_EXCEPTION("70", "02", "用户未登录"),
    METHOD_NOT_SUPPORT("80", "00", "HttpRequestMethodNotSupported"),
    SYSTEM_ERROR("9999", "", "系统繁忙");

    private final String code;
    private final String subCode;
    private final String message;
}
