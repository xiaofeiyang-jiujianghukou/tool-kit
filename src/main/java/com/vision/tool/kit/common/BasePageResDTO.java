package com.vision.tool.kit.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "分页查询返回实体基础信息")
public class BasePageResDTO<T> {

    @Schema(title = "总记录数")
    private long totalCount = 0;
    @Schema(title = "详情")
    private List<T> data;

}
