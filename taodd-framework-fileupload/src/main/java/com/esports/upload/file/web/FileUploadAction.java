package com.esports.upload.file.web;



import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.upload.file.service.FileUploadManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【系统组件-文件上传相关接口】"})
@RestController
public class FileUploadAction {

    @Autowired
    private FileUploadManager fileUploadManager;

    @ApiOperation("文件/图片上传")

    @RequestMapping(value = "upload/", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件对象", paramType = "query", dataType = "String", required = true)})

    @Auth(authentication = false, sign=false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper uploadFile(@RequestParam(value = "file") MultipartFile file, HttpServletResponse resp, HttpServletRequest request) {
        if (null == file) {
            return JsonWrapper.failureWrapper(ResultCode._441.getCode(), ResultCode._441.getMessage());
        }
        String originalFilename = file.getOriginalFilename();
        String filePath = fileUploadManager.upload(file, originalFilename, null);
        return JsonWrapper.successWrapper(filePath, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("根据文件ID删除文件")

    @RequestMapping(value = "upload/", produces = "application/json; charset=UTF-8", method = {RequestMethod.DELETE})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "文件ID", paramType = "query", dataType = "String", required = true)})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper deleteAtt(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            return JsonWrapper.failureWrapper(ResultCode._441.getCode(), ResultCode._441.getMessage());
        }
        fileUploadManager.delete(fileId);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

}
