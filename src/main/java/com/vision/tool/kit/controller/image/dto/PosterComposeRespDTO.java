package com.vision.tool.kit.controller.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PosterComposeRespDTO {

    @Schema(title  = "海报URL")
    private String posterUrl;
    
}
