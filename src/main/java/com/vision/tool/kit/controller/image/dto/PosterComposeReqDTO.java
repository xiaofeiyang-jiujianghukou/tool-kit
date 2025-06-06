package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterComposeReqDTO {

    @Schema(title  = "海报URL")
    private String posterUrl;
    @Schema(title  = "二维码URL 默认值：https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png")
    private String qrCodeUrl = "https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png";

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
