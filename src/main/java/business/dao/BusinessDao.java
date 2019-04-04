package business.dao;


import business.entity.BusinessEntity;
import business.entity.BusinessResourceEntity;
import business.entity.PageBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import business.entity.PageData;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface BusinessDao {

    List<BusinessEntity> getBusinessList();

    boolean insertBusinessResourceList(List<BusinessResourceEntity> resourceList) throws JsonProcessingException;

    

    /**
     * 根据资源uuid获取业务资源实体
     * @param monitorUuid
     * @return
     */
    List<BusinessResourceEntity> getBusinessResourceByMonitorUuid(String monitorUuid);

    /**
     * 通过业务id获取业务实体
     * @param uuid
     * @return
     */
    BusinessEntity getBusinessNode(String uuid);

    /**
     * 分页获取业务列表
     * @param page
     * @return
     */
    PageBean getBusinessListByPage(PageData page) throws JsonProcessingException;

    List<BusinessResourceEntity> getBusinessResourcesByBusinessId(String businessId);

    boolean delBusinessResourceList(List<BusinessResourceEntity> needDel) throws JsonProcessingException;

    boolean delBusinessResourceByBusinessId(String businessId);

    boolean insertBusiness(BusinessEntity businessEntity) throws JsonProcessingException;
}
