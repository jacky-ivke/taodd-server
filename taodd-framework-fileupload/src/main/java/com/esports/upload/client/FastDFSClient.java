package com.esports.upload.client;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@Data
@PropertySource("classpath:fastdfs.properties")
public class FastDFSClient {
    
    @Value("${download}")
    private String download;

    public static StorageClient1 getClient() {
    	StorageClient1 storageClient1 = null;
        try {
            String filePath = new ClassPathResource("fastdfs.properties").getFile().getAbsolutePath();
            ClientGlobal.init(filePath);
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                throw new IllegalStateException("getConnection return null");
            }
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                throw new IllegalStateException("getStoreStorage return null");
            }
            if (null == storageClient1) {
                storageClient1 = new StorageClient1(trackerServer, storageServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storageClient1;
    }

    /**
     * @Title:
     * @Description:文件上传
     * @Date:2019/3/21 14:53
     * @Author:Administrator
     * @Param:fileName-文件的磁盘路径名称 如：D:/image/java.jpg
     * @return:
     **/
    public static String uploadFile(String fileName) {
        return uploadFile(fileName, null, null);
    }

    /**
     * @Title:uploadFile
     * @Description:文件上传
     * @Date:2019/3/21 15:57
     * @Author:Administrator
     * @Param:fileName-文件的磁盘路劲,extName-扩展名:txt,png
     * @return:
     **/
    public static String uploadFile(String fileName, String extName) {
        return uploadFile(fileName, extName, null);
    }

    /**
     * @Title:uploadFile
     * @Description:文件上传
     * @Date:2019/3/21 15:58
     * @Author:Administrator
     * @Param:fileName-文件的磁盘路劲,extName-扩展名:txt,png,metas-元数据（根据需要扩展）：如图片名称：xxx,创建者：xxx,宽度:xxx
     * @return:
     **/
    public static String uploadFile(String fileName, String extName, NameValuePair[] metas) {
        try {
            return getClient().upload_file1(fileName, extName, metas);
        } catch (IOException io) {
            io.printStackTrace();
        } catch (MyException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @Title:uploadFile
     * @Description:文件上传
     * @Date:2019/3/21 15:58
     * @Author:Administrator
     * @Param:fileName-文件的磁盘路劲,extName-扩展名:txt,png,metaList-元数据（根据需要扩展）：如图片名称：xxx,创建者：xxx,宽度:xxx
     * @return:
     **/
    public static String uploadFile(File file, String fileName, Map<String, String> metaList) {
        try {
            byte[] buff = IOUtils.toByteArray(new FileInputStream(file));
            NameValuePair[] nameValuePairs = null;
            if (metaList != null && !metaList.isEmpty()) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            return getClient().upload_file1(buff, FilenameUtils.getExtension(fileName), nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uploadFile(MultipartFile multipartFile, String fileName, Map<String, String> metaList) {
        try {
            byte[] buff = IOUtils.toByteArray(multipartFile.getInputStream());
            NameValuePair[] nameValuePairs = null;
            if (metaList != null && !metaList.isEmpty()) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            return getClient().upload_file1(buff, FilenameUtils.getExtension(fileName), nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title:getFileMetadata
     * @Description:获取文件的扩展的元数据
     * @Date:2019/3/21 15:55
     * @Author:Administrator
     * @Param:
     * @return:
     **/
    public static Map<String, String> getFileMetadata(String fileId) {
        try {
            if (!StringUtils.isEmpty(fileId)) {
                NameValuePair[] metaList = getClient().get_metadata1(fileId);
                if (metaList != null) {
                    Map<String, String> map = new HashMap<String, String>();
                    for (NameValuePair metaItem : metaList) {
                        map.put(metaItem.getName(), metaItem.getValue());
                    }
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title:downloadFile
     * @Description:文件下载到磁盘
     * @Date:2019/3/21 16:02
     * @Author:Administrator
     * @Param:fileId-文件ID,outFile-文件下载位置
     * @return:
     **/
    public static int downloadFile(String fileId, File outFile) {
        FileOutputStream fos = null;
        try {
            byte[] content = getClient().download_file1(fileId);
            fos = new FileOutputStream(outFile);
            IOUtils.copy(new ByteArrayInputStream(content), fos);
            return 0;
        } catch (Exception ex) {
            //可能取消了下载
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * @Title:deleteFile
     * @Description:删除文件
     * @Date:2019/3/21 16:11
     * @Author:Administrator
     * @Param:group-文件组名,storagePath-路劲
     * @return:
     **/
    public static Integer deleteFile(String group, String storagePath) {
        int result = -1;
        try {
            result = getClient().delete_file(group, storagePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @Title:deleteFile
     * @Description:文件删除
     * @Date:2019/3/21 16:06
     * @Author:Administrator
     * @Param:
     * @return:
     **/
    public static Integer deleteFile(String storagePath) {
        int result = -1;
        try {
            result = getClient().delete_file1(storagePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @Title:modifyFile
     * @Description:文件上传,同时删除旧文件
     * @Date:2019/3/21 16:48
     * @Author:Administrator
     * @Param:
     * @return:
     **/
    public static String modifyFile(String oldGroupName, String oldFileId, File file, String fileNmae) {
        String fileid = null;
        try {
            // 先上传
            fileid = uploadFile(file, fileNmae, null);
            if (StringUtils.isEmpty(fileid)) {
                return null;
            }
            // 再删除
            int delResult = deleteFile(oldGroupName, oldFileId);
            if (delResult != 0) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
        return fileid;
    }

    /**
     * @Title:getFile
     * @Description:获取文件资源信息
     * @Date:2019/3/21 16:06
     * @Author:Administrator
     * @Param:
     * @return:
     **/
    public FileInfo getFile(String groupName, String remoteFileName) {
        try {
            return getClient().get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
