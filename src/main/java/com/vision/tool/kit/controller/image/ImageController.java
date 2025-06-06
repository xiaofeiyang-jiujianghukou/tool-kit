package com.vision.tool.kit.controller.image;

import com.vision.tool.kit.common.Result;
import com.vision.tool.kit.controller.image.dto.*;
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

    @PostMapping("/image/posterCalculate")
    @Operation(summary = "海报计算", description = "通过用户ID获取用户的详细信息")
    public Result<PosterCalculateRespDTO> posterCalculate(@Validated @RequestBody PosterCalculateReqDTO input) {
        return Result.success(imagerManager.posterCalculate(input));
    }

    @PostMapping("/image/posterCompress")
    @Operation(summary = "海报压缩", description = "通过用户ID获取用户的详细信息")
    public Result<PosterCompressRespDTO> posterCompress(@Validated @RequestBody PosterCompressReqDTO input){
        return Result.success(imagerManager.posterCompress(input));
    }

    @PostMapping("/image/posterCompose")
    @Operation(summary = "海报合成", description = "通过用户ID获取用户的详细信息")
    public Result<PosterComposeRespDTO> posterCompose(@Validated @RequestBody PosterComposeReqDTO input){
        return Result.success(imagerManager.posterCompose(input));
    }


}
