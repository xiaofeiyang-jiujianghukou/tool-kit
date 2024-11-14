package com.vision.tool.kit.controller.image;

import com.vision.tool.kit.common.Result;
import com.vision.tool.kit.controller.image.dto.PosterCompositionReqDTO;
import com.vision.tool.kit.controller.image.dto.PosterCompositionRespDTO;
import com.vision.tool.kit.manager.ImagerManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/management/tool-kit/", "/inner/tool-kit/"})
@Tag(name = "图片管理", description = "包含图片相关的操作")
public class ImageController {

    @Resource
    private ImagerManager imagerManager;

    @PostMapping("/image/posterComposition")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<PosterCompositionRespDTO> posterComposition(@Validated @RequestBody PosterCompositionReqDTO input) {
        return Result.success(imagerManager.posterComposition(input));
    }


}
