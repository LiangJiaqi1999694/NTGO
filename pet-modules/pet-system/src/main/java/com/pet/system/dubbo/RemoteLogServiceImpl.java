package com.pet.system.dubbo;

import com.pet.system.api.RemoteLogService;
import com.pet.system.api.domain.SysLogininfor;
import com.pet.system.api.domain.SysOperLog;
import com.pet.system.service.ISysLogininforService;
import com.pet.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 操作日志记录
 *
 * @author zy
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteLogServiceImpl implements RemoteLogService {

    private final ISysOperLogService operLogService;
    private final ISysLogininforService logininforService;

    @Override
    public Boolean saveLog(SysOperLog sysOperLog) {
        return operLogService.insertOperlog(sysOperLog) > 0;
    }

    @Override
    public Boolean saveLogininfor(SysLogininfor sysLogininfor) {
        return logininforService.insertLogininfor(sysLogininfor) > 0;
    }
}
