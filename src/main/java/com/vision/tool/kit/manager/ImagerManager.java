package com.vision.tool.kit.manager;

import com.vision.tool.kit.controller.image.dto.*;
import com.vision.tool.kit.util.ImageCompressor;
import com.vision.tool.kit.util.PosterQRCodeCalculate;
import com.vision.tool.kit.util.PosterQRCodeComposer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImagerManager {

    @Resource
    private ImageCompressor imageCompressor;

    @Resource
    private PosterQRCodeComposer posterQRCodeComposer;

    public PosterCalculateRespDTO posterCalculate(PosterCalculateReqDTO input) {
        //return EdgeDetectionBlankSpace.generatePosterCompositionResult(input);
        return PosterQRCodeCalculate.generatePosterCompositionResult(input);
    }

    public PosterCompressRespDTO posterCompress(PosterCompressReqDTO input) {
        try {
            String fileUrl = imageCompressor.compressImage2(input.getPosterUrl(), 0.9f);
            return PosterCompressRespDTO.builder().posterUrl(fileUrl).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public PosterComposeRespDTO posterCompose(PosterComposeReqDTO input) {
        try {
            String postUrl = posterQRCodeComposer.composeAndUpload(input.getPosterUrl(),
                    input.getQrCodeUrl(),
                    input.getX(),
                    input.getY(),
                    input.getWidth(),
                    input.getHeight(),
                    input.getScaleFactor());
            return PosterComposeRespDTO.builder().posterUrl(postUrl).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
