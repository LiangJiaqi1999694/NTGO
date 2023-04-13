package com.xxl.job.core.util;

import lombok.Data;

/**
 * @Author: 庄勇
 * @Date: 2021/2/8 0008 18:48
 * @Version 1.0
 */
@Data
public class QueueInfo {

    private String jobId;
    private int queueSize;
}
