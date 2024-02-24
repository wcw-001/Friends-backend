package com.wcw.usercenter.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.wcw.usercenter.service.QiNiuService;
import com.wcw.usercenter.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
/**
 * @author wcw
 * 七牛云对象存储实现类
 */
@Service
@Slf4j
public class QiNiuServiceImpl implements QiNiuService {
    // 设置好账号的ACCESS_KEY和SECRET_KEY
    String accessKey = "RfAPWyXFwNHF6Z8OBQipIg_1iNIp-KJj5A-Fe5eL";
    String secretKey = "-OUMlQVUczKKAVBVp9Q5xBUIasMw1hBRdXqCFJ9B";
    // 要上传的空间
    String bucketName = "fancydog";

    // 密钥配置
    Auth auth = Auth.create(accessKey, secretKey);
    // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
    Configuration cfg = new Configuration(Zone.zone2());
    // ...其他参数参考类注释
    UploadManager uploadManager = new UploadManager(cfg);

    // 测试域名，只有30天有效期
    private static String QINIU_IMAGE_DOMAIN = "http://img.wcw231407.cn/";

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(bucketName);
    }

    public String uploadFileAvatar(MultipartFile file) {
        try {
            //截取文件后缀
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            // 判断是否是合法的文件后缀
            if (!FileUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            // 调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                // 返回这张存储照片的地址
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                log.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (IOException e) {
            // 请求失败时打印的异常的信息
            log.error("七牛异常:" + e.getMessage());
            return null;
        }
    }
}
