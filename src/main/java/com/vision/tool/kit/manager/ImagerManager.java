package com.vision.tool.kit.manager;

import com.vision.tool.kit.controller.image.dto.PosterCompositionReqDTO;
import com.vision.tool.kit.controller.image.dto.PosterCompositionRespDTO;
import com.vision.tool.kit.util.EdgeDetectionBlankSpace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImagerManager {

    public PosterCompositionRespDTO posterComposition(PosterCompositionReqDTO input) {
        return EdgeDetectionBlankSpace.generatePosterCompositionResult(input);
    }

}
