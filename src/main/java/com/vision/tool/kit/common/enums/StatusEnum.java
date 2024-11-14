package com.vision.tool.kit.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    NOT(1, "未删除"),

    YET(0, "已删除");

    private final int value;

    private final String desc;

}
