package com.vision.tool.kit.common.exception;

import com.vision.tool.kit.common.enums.ResultEnum;
import lombok.Getter;


@Getter
public class BizException extends RuntimeException {
    private final ResultEnum resultEnum;

    public BizException(ResultEnum result) {
        super(result.getMessage());
        this.resultEnum = result;
    }

    public BizException(String message) {
        super(message);
        this.resultEnum = ResultEnum.BIZ_EXCEPTION;
    }

    public BizException(ResultEnum result, String message) {
        super(message);
        this.resultEnum = result;
    }
}
