package business.dao;


import business.entity.BusinessEntity;
import business.entity.BusinessResourceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface BusinessDao {

    List<BusinessEntity> getBusinessList();

    boolean insertBusinessResourceList(List<BusinessResourceEntity> resourceList) throws JsonProcessingException;
}