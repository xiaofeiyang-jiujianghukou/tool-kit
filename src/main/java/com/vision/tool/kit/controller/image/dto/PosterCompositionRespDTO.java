package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCompositionRespDTO {

    @Schema(title = "X轴")
    private Integer x;
    @Schema(title = "Y轴")
    private Integer y;
    @Schema(title = "缩放比例")
    private Integer scaleFactor;
}
