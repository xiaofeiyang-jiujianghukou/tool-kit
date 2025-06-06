package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCalculateRespDTO {

    @Schema(title  = "海报URL")
    private String posterUrl;
    @Schema(title  = "X轴")
    private int x;
    @Schema(title  = "Y轴")
    private int y;
    @Schema(title  = "宽")
    private int width;
    @Schema(title  = "高")
    private int height;
    @Schema(title  = "缩放比例")
    private double scaleFactor;
}
