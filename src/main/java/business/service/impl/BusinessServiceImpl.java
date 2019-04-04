package business.service.impl;

import business.dao.BusinessDao;
import business.entity.*;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import monitor.entity.OperationMonitorEntity;
import monitor.service.MonitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import topo.service.TopoService;

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

    @Autowired
    TopoService topoService;

    @Autowired
    MonitorService monitorService;

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

    @Override
    public ResultMsg delBusiness(String businessId) {

        //调用拓扑接口，删除节点和链路
        topoService.delTopoResourceByBusinessId(businessId);

        //调用业务接口删除业务
        boolean res = dao.delBusinessResourceByBusinessId(businessId);
        return ResCommon.genSimpleResByBool(res);
    }

    @Override
    public ResultMsg getBusinessInfo(String businessId) {
        BusinessEntity business = dao.getBusinessNode(businessId);
        BusinessInfo businessInfo = new BusinessInfo();
        BeanUtils.copyProperties(business,businessInfo);
        List<BusinessMonitorEntity> businessMonitorList = new ArrayList<>();
        List<BusinessResourceEntity> resourceList = dao.getBusinessResourcesByBusinessId(businessId);
        List<OperationMonitorEntity> monitorList = (List<OperationMonitorEntity>) monitorService.getBusinessMonitorRecord().getData();
        resourceList.forEach(x->{
            BusinessMonitorEntity monitorEntity = new BusinessMonitorEntity();
            monitorEntity.setMonitorId(x.getMonitorId());
            monitorEntity.setLightType(x.getLightType());
            Optional<OperationMonitorEntity> monitorOpt = monitorList.stream().filter(y->y.getUuid().equals(x.getMonitorId())
                    &&y.getLightTypeId().equals(x.getLightType())).findFirst();
            monitorOpt.ifPresent(operationMonitorEntity -> {
                monitorEntity.setIp(operationMonitorEntity.getIp());
                monitorEntity.setName(operationMonitorEntity.getName());
            });
            businessMonitorList.add(monitorEntity);
        });
        businessInfo.setResourceList(businessMonitorList);
        return ResCommon.getCommonResultMsg(businessInfo);
    }

    @Override
    public ResultMsg delBusinessResource(String businessId, List<String> monitorIds) throws JsonProcessingException {
        List<BusinessResourceEntity> resourceEntityList = new ArrayList<>();
        monitorIds.forEach(x->{
            BusinessResourceEntity resource = new BusinessResourceEntity();
            resource.setBusinessUuid(businessId);
            resource.setMonitorId(x);
            resourceEntityList.add(resource);
        });
        boolean res = dao.delBusinessResourceList(resourceEntityList);
        return ResCommon.genSimpleResByBool(res);
    }

    @Override
    public ResultMsg updateBusiness(String businessId, String busname, List<BusinessMonitorEntity> data) throws JsonProcessingException {
       //更新业务
        BusinessEntity businessEntity = dao.getBusinessNode(businessId);
        if (!businessEntity.getName().equals(busname)){
            businessEntity.setName(busname);
            dao.insertBusiness(businessEntity);
        }
        List<DelMonitorRecordView> view = new ArrayList<>();
        data.forEach(x->{
            DelMonitorRecordView dele = new DelMonitorRecordView();
            dele.setUuid(x.getMonitorId());
            dele.setLightType(x.getLightType());
            view.add(dele);
        });

        return addBusinessResource(businessId,view);
    }

}
