package business.service.impl;

import business.dao.BusinessDao;
import business.entity.BusinessEntity;
import business.entity.BusinessResourceEntity;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import business.entity.PageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    private static final String RULE_ANME_START = "rule";
    private static final String AVL_RULE_NAME = "_avl";
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String ONE_LEVEL_PERF = "_one_level_perf";
    private static final String TWO_LEVEL_PERF = "_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BusinessDao dao;

    @Override
    public boolean isJoinBusinessMonitor(String monitorUuid) {
        List<BusinessResourceEntity> entities = dao.getBusinessResourceByMonitorUuid(monitorUuid);
        if (entities.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResultMsg getBusinessList() {
        return ResCommon.getCommonResultMsg(dao.getBusinessList());
    }

    @Override
    public ResultMsg addBusinessResource(String businessId, List<DelMonitorRecordView> view) throws JsonProcessingException {
        List<BusinessResourceEntity> resourceList = new ArrayList<>();
        //删除这次没有选择的
        List<BusinessResourceEntity> allResource = dao.getBusinessResourcesByBusinessId(businessId);
        List<BusinessResourceEntity> needDel = new ArrayList<>();
        List<BusinessResourceEntity> noChange = new ArrayList<>();
        allResource.forEach(x->{
            Optional<DelMonitorRecordView> optView = view.stream().filter(y->y.getUuid().equals(x.getMonitorId())).findFirst();
            if (optView.isPresent()){
                //不用变的
                noChange.add(x);
            }else {
                //需要删除的
                needDel.add(x);
            }
        });
        //需要添加的
        view.forEach(x->{
            Optional<BusinessResourceEntity> no = noChange.stream().filter(y->y.getMonitorId().equals(x.getUuid())).findFirst();
            Optional<BusinessResourceEntity> del = needDel.stream().filter(y->y.getMonitorId().equals(x.getUuid())).findFirst();
            if (!no.isPresent() && !del.isPresent()){
                BusinessResourceEntity entity = new BusinessResourceEntity();
                entity.setUuid(UUID.randomUUID().toString());
                entity.setBusinessUuid(businessId);
                entity.setMonitorId(x.getUuid());
                entity.setLightType(x.getLightType());
                resourceList.add(entity);
            }
        });
        boolean delres = dao.delBusinessResourceList(needDel);
//        view.forEach(x -> {
//            BusinessResourceEntity entity = new BusinessResourceEntity();
//            entity.setUuid(UUID.randomUUID().toString());
//            entity.setBusinessUuid(businessId);
//            entity.setMonitorId(x.getUuid());
//            entity.setLightType(x.getLightType());
//            resourceList.add(entity);
//        });
        if (delres){
            boolean res = dao.insertBusinessResourceList(resourceList);
            return ResCommon.genSimpleResByBool(res);
        }
        return ResCommon.genSimpleResByBool(false);
    }

    @Override
    public BusinessEntity getBusinessNode(String uuid) {
        return dao.getBusinessNode(uuid);
    }

    @Override
    public ResultMsg getBusinessListByPage(PageData page) throws JsonProcessingException {
        return ResCommon.getCommonResultMsg(dao.getBusinessListByPage(page));
    }

}
