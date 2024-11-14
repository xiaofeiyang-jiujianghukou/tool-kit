package com.vision.tool.kit.common;

import com.vision.tool.kit.common.enums.ResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class Result<T> {
    @Schema(title = "返回码")
    private final String code;
    @Schema(title = "错误信息")
    private final String message;
    @Schema(title = "数据实体")
    private final T data;

    private Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 静态成功响应方法
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    // 静态失败响应方法
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultEnum.BIZ_EXCEPTION.getCode(), ResultEnum.BIZ_EXCEPTION.getMessage(), null);
    }

    // 静态错误响应方法
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage(), null);
    }

    // 可选: 根据业务需求增加其他成功和失败方法
    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(code, message, null);
    }
}
