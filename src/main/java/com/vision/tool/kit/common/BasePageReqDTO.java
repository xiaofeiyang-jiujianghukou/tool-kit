package com.vision.tool.kit.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分页查询请求实体基础信息")
public class BasePageReqDTO {

    @Schema(title = "当前页数(默认为1)", defaultValue = "1")
    @NotNull(message = "当前页不能为空")
    private Integer pageIndex = 1;

    @Schema(title = "每页条数(默认10条)", defaultValue = "10")
    @NotNull(message = "分页条数不能为空")
    private final Integer pageSize =10;

    @Schema(title = "开始分页点", hidden = true)
    public Integer getPageStart() {
        return this.pageIndex * this.pageSize - this.pageSize;
    }
}
