package business.service;


import business.entity.BusinessEntity;
import business.entity.BusinessMonitorEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import business.entity.PageData;

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
     * @param view
     * @return
     */
    ResultMsg addBusinessResource(String businessId,List<DelMonitorRecordView> view) throws JsonProcessingException;

    /**
     * 根据uuid获取业务实体
     * @param uuid
     * @return
     */
    BusinessEntity getBusinessNode(String uuid);


    ResultMsg getBusinessListByPage(PageData page) throws JsonProcessingException;

    /**
     * 删除业务
     * @param businessId
     * @return
     */
    ResultMsg delBusiness(String businessId);

    /**
     * 获取业务信息，包括业务内的资源
     * @param businessId
     * @return
     */
    ResultMsg getBusinessInfo(String businessId);

    /**
     * 删除业务资源
     * @param businessId
     * @param monitorIds
     * @return
     */
    ResultMsg delBusinessResource(String businessId, List<String> monitorIds) throws JsonProcessingException;

    /**
     * 更新业务
     * @param businessId
     * @param busname
     * @param data
     * @return
     */
    ResultMsg updateBusiness(String businessId, String busname, List<BusinessMonitorEntity> data) throws JsonProcessingException;
}
