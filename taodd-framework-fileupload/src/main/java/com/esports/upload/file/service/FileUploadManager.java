package com.esports.upload.file.service;

import com.esports.upload.client.FastDFSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileUploadManager {

    @Autowired
    private FastDFSClient fastDFSClient;

    public String upload(MultipartFile multipartFile, String fileName, Map<String, String> metaList){
        String fileId = FastDFSClient.uploadFile(multipartFile, fileName, metaList);
        String fullPath = fastDFSClient.getDownload() + fileId;
        return fullPath;
    }

    public void delete(String fileId){
        FastDFSClient.deleteFile(fileId);
    }
}
