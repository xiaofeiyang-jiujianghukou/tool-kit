package com.vision.tool.kit.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EffectiveStatusEnum {

    NO(0, "失效"),

    YES(1, "有效");

    private final int value;

    private final String desc;

}
