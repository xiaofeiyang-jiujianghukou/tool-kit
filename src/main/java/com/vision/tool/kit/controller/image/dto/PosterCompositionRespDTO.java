package com.vision.tool.kit.controller.image.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCompositionRespDTO {

    @ApiModelProperty(value = "海报URL")
    private Integer x;
    @ApiModelProperty(value = "二维码URL")
    private Integer y;
    private Integer scaleFactor;
}
