package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCompositionReqDTO {

    @Schema(title = "海报URL")
    private String posterUrl;
    @Schema(title = "二维码URL", hidden = true)
    private String qrCodeUrl = "https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png";
}
