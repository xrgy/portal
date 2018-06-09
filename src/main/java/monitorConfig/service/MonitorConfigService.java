package monitorConfig.service;

import monitorConfig.common.ResultMsg;
import org.springframework.stereotype.Service;

/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorConfigService {
    public String getJPA();

    /**
     * 获取指标信息
     * @param lightType
     * @param monitorMode
     * @return
     */
    public ResultMsg getMetricInfo(String lightType,String monitorMode);
}
