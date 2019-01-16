package business.service.impl;

import business.dao.BusinessDao;
import business.entity.BusinessResourceEntity;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    private static final String RULE_ANME_START="rule";
    private static final String AVL_RULE_NAME="_avl";
    private static final String ONE="one";
    private static final String TWO="two";
    private static final String ONE_LEVEL_PERF="_one_level_perf";
    private static final String TWO_LEVEL_PERF="_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BusinessDao dao;

    @Override
    public boolean isJoinBusinessMonitor(String monitorUuid) {
        return false;
    }

    @Override
    public ResultMsg getBusinessList() {
        return ResCommon.getCommonResultMsg(dao.getBusinessList());
    }

    @Override
    public ResultMsg addBusinessResource(String businessId, List<DelMonitorRecordView> view) throws JsonProcessingException {
        List<BusinessResourceEntity> resourceList = new ArrayList<>();
        view.forEach(x->{
            BusinessResourceEntity entity = new BusinessResourceEntity();
            entity.setUuid(UUID.randomUUID().toString());
            entity.setBusinessUuid(businessId);
            entity.setMonitorId(x.getUuid());
            entity.setLightType(x.getLightType());
            resourceList.add(entity);
        });
        boolean res = dao.insertBusinessResourceList(resourceList);
        return ResCommon.genSimpleResByBool(res);
    }
}
