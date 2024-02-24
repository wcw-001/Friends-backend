package com.wcw.usercenter.service;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author wcw
 */

public interface QiNiuService {
    /**
     * 上传头像到OSS
     *
     * @param file
     * @return
     */
    String uploadFileAvatar(MultipartFile file);
}
