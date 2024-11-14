package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCompositionRespDTO {

    @Schema(title = "海报URL")
    private Integer x;
    @Schema(title = "二维码URL")
    private Integer y;
    @Schema(title = "缩放比例")
    private Integer scaleFactor;
}
