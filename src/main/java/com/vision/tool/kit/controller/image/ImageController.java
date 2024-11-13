package com.vision.tool.kit.controller.image;

import com.vision.cool.component.common.result.Result;
import com.vision.tool.kit.controller.image.dto.PosterCompositionReqDTO;
import com.vision.tool.kit.controller.image.dto.PosterCompositionRespDTO;
import com.vision.tool.kit.manager.ImagerManager;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = {"/management/tool-kit/", "/inner/tool-kit/"})
public class ImageController {

    @Resource
    private ImagerManager imagerManager;

    @PostMapping("/image/posterComposition")
    @ApiOperation("海报合成")
    public Result<PosterCompositionRespDTO> posterComposition(@Validated @RequestBody PosterCompositionReqDTO input) {
        return Result.success(imagerManager.posterComposition(input));
    }


}
