package com.vision.tool.kit.controller.image.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCompositionReqDTO {

    @ApiModelProperty(value = "海报URL")
    private String posterUrl;
    @ApiModelProperty(value = "二维码URL")
    private String qrCodeUrl;
}
