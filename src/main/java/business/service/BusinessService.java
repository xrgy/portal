package business.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface BusinessService {

    /**
     * 设备是否已经被加入了业务监控
     * @param monitorUuid
     * @return
     */
    boolean isJoinBusinessMonitor(String monitorUuid);

    /**
     * 获取业务列表
     * @return
     */
    ResultMsg getBusinessList();

    /**
     * 添加业务资源
     * @param uuids
     * @return
     */
    ResultMsg addBusinessResource(String businessId,List<String> uuids) throws JsonProcessingException;
}