package com.pet.resource.dubbo;

import com.pet.common.core.exception.ServiceException;
import com.pet.common.core.utils.StringUtils;
import com.pet.common.oss.core.OssClient;
import com.pet.common.oss.entity.UploadResult;
import com.pet.common.oss.factory.OssFactory;
import com.pet.resource.api.RemoteFileService;
import com.pet.resource.api.domain.SysFile;
import com.pet.resource.domain.bo.SysOssBo;
import com.pet.resource.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件请求处理
 *
 * @author zy
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DubboService
public class RemoteFileServiceImpl implements RemoteFileService {

    private final ISysOssService sysOssService;

    /**
     * 文件上传请求
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysFile upload(String name, String originalFilename, String contentType, byte[] file,String bucketName) throws ServiceException {
        try {
            String suffix = StringUtils.substring(originalFilename, originalFilename.lastIndexOf("."), originalFilename.length());
            OssClient storage = OssFactory.instance();
            UploadResult uploadResult = storage.uploadSuffix(file, suffix, contentType,bucketName);
            // 保存文件信息
            SysOssBo oss = new SysOssBo();
            oss.setUrl(uploadResult.getUrl());
            oss.setFileSuffix(suffix);
            oss.setFileName(uploadResult.getFilename());
            oss.setOriginalName(originalFilename);
            oss.setService(storage.getConfigKey());
            sysOssService.insertByBo(oss);
            SysFile sysFile = new SysFile();
            sysFile.setOssId(oss.getOssId());
            sysFile.setName(uploadResult.getFilename());
            sysFile.setUrl(uploadResult.getUrl());
            return sysFile;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new ServiceException("上传文件失败");
        }
    }

    /**
     * 通过ossId查询对应的url
     *
     * @param ossIds ossId串逗号分隔
     * @return url串逗号分隔
     */
    @Override
    public String selectUrlByIds(String ossIds) {
        return sysOssService.selectUrlByIds(ossIds);
    }

}
