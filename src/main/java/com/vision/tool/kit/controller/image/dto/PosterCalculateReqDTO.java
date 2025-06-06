package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterCalculateReqDTO {

    @Schema(title = "海报URL")
    private String posterUrl = "https://va-papers.oss-accelerate.aliyuncs.com/oss-platform/4a69/1864/f136/f2fd0d01-631f-4681-96a4-1b6cf10fe947.png";
    @Schema(title = "二维码URL 默认值：https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png")
    private String qrCodeUrl = "https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png";
}
