package monitorConfig.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.CommonEnum;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import monitor.service.MonitorService;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.metric.ResMetricInfo;
import monitorConfig.entity.metric.UpTemplateView;
import monitorConfig.entity.template.*;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

    private static final String RULE_ANME_START="rule_";
    private static final String AVL_RULE_NAME="_avl";
    private static final String ONE="one";
    private static final String TWO="two";
    private static final String ONE_LEVEL_PERF="_one_level_perf";
    private static final String TWO_LEVEL_PERF="_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MonitorConfigDao dao;

    @Autowired
    MonitorService monitorService;

    @Override
    public String getJPA() {
//        objectMapper.writeValueAsString();
//        objectMapper.readValue("",TestEntity.class);
        ResponseEntity<TestEntity> response = rest().getForEntity("http://127.0.0.1:8086/monitorConfig/jpa",TestEntity.class);
        return response.getBody().getName();
    }

    @Override
    public ResultMsg getMetricInfo(String lightType, String monitorMode) {
        ResultMsg msg = new ResultMsg();
        ResMetricInfo info = dao.getMetricInfo(lightType,monitorMode);
        if (null != info) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(dao.getMetricInfo(lightType, monitorMode));
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }

    @Override
    public boolean isTemplateNameDup(String name) {
        return dao.isTemplateNameDup(name);
    }

    @Override
    public ResultMsg addTemplate(NewTemplateView view) {
        boolean res = dao.addTemplate(view);
        return ResCommon.genSimpleResByBool(res);
    }

    @Override
    public ResultMsg getTemplateByLightType(String lightType,String monitorMode) {
        ResultMsg msg = new ResultMsg();
        MonitorTemplate templateList = dao.getTemplateByLightType(lightType);
//        JSONObject object = new JSONObject();
//        object.put("lightType",lightType);
//        object.put("monitorMode",monitorMode);
//        object.put("template",templateList);
        if (null == templateList){
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }else {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(templateList);
        }
        return msg;

    }

    @Override
    public RuleMonitorEntity addMonitorRecordAlertRule(String monitorUuid,String templateId) {
//        String monitorUuid = operationMonitorEntity.getUuid();
//        String templateId = operationMonitorEntity.getTemplateId();
        List<AlertAvlRuleEntity> avlRuleList = dao.getAvlRuleByTemplateId(templateId);
        List<AlertPerfRuleEntity> perfRuleList = dao.getPerfRuleByTemplate(templateId);
        List<AlertAvlRuleMonitorEntity> avlRuleMonitorList = new ArrayList<>();
        List<AlertPerfRuleMonitorEntity> perfRuleMonitorEntityList = new ArrayList<>();
        avlRuleList.forEach(x->{
            AlertAvlRuleMonitorEntity entity = new AlertAvlRuleMonitorEntity();
//            String id =UUID.randomUUID().toString().replaceAll("-","");
            String id = (monitorUuid+x.getUuid()).replaceAll("-","");
            entity.setUuid(id);
            entity.setAlertRuleName(RULE_ANME_START+id+AVL_RULE_NAME);
            entity.setMonitorUuid(monitorUuid);
            entity.setAvlRuleUuid(x.getUuid());
            avlRuleMonitorList.add(entity);
        });
//        dao.addAvlRuleMonitorList(avlRuleMonitorList);
        perfRuleList.forEach(x->{
            AlertPerfRuleMonitorEntity entity = new AlertPerfRuleMonitorEntity();
            String id = (monitorUuid+x.getUuid()).replaceAll("-","");
//            String id = UUID.randomUUID().toString().replaceAll("-","");
            entity.setUuid(id);
            entity.setMonitorUuid(monitorUuid);
            entity.setPerfRuleUuid(x.getUuid());
            if (x.getAlertLevel().equals(ONE)){
                entity.setAlertRuleName(RULE_ANME_START+id+ONE_LEVEL_PERF);
            }else if(x.getAlertLevel().equals(TWO)){
                entity.setAlertRuleName(RULE_ANME_START+id+TWO_LEVEL_PERF);
            }
            perfRuleMonitorEntityList.add(entity);
        });
//        dao.addPerfRuleMonitorList(perfRuleMonitorEntityList);
        AlertRuleTemplateMonitorEntity templateMonitorEntity = new AlertRuleTemplateMonitorEntity();
        String temuuid = (monitorUuid+templateId).replaceAll("-","");
        templateMonitorEntity.setUuid(temuuid);
        templateMonitorEntity.setMonitorUuid(monitorUuid);
        templateMonitorEntity.setTemplateUuid(templateId);
//        dao.addTemplateMonitorEntity(templateMonitorEntity);

        RuleMonitorEntity ruleMonitorEntity = new RuleMonitorEntity();
        ruleMonitorEntity.setAvlRuleMonitorList(avlRuleMonitorList);
        ruleMonitorEntity.setPerfRuleMonitorList(perfRuleMonitorEntityList);
        ruleMonitorEntity.setTemplateMonitorEntity(templateMonitorEntity);
        return ruleMonitorEntity;
    }

    @Override
    public void addAlertTemplateToEtcd(String lightTypeId, String templateId, RuleMonitorEntity ruleMonitorEntity) throws JsonProcessingException {
        dao.addAlertTemplateToEtcd(lightTypeId,templateId,ruleMonitorEntity);
    }

    @Override
    public boolean delAlertRuleByUuids(List<String> uuids) {
        uuids.forEach(x->{
            dao.delAlertRuleByUuid(x);
        });
        return true;
    }

    @Override
    public RuleMonitorEntity updateMonitorRecordAlertRule(String uuid, String templateId,String oldTemplateId) {
        //删除
        //delAlertRuleByUuid 需要uuid是monitoruuid+templateuuid 去除'-'
        String tempuuid = (uuid+oldTemplateId).replaceAll("-", "");
        boolean res = dao.delAlertRuleByUuid(tempuuid);
        if (res){
            return addMonitorRecordAlertRule(uuid,templateId);
        }
        return null;
    }

    @Override
    public ResultMsg delTemplate(List<String> templateUuids) {
        templateUuids.forEach(x->{
            boolean res = dao.delTemplate(x);
        });
        return ResCommon.genSimpleResByBool(true);
    }

    @Override
    public ResultMsg updateTemplate(UpTemplateView view) {
        boolean res = dao.updateTemplate(view);
        if (res){

        }
        return ResCommon.genSimpleResByBool(res);
    }

    @Override
    public ResultMsg OpenTemplate(String uuid) throws IOException {
        UpTemplateView templateView = dao.getOpenTemplateData(uuid);
        int count = monitorService.getMonitorCountByTemplateId(uuid,templateView.getLightType());
        templateView.setUsedCount(count);

        return ResCommon.getCommonResultMsg(templateView);
    }

    @Override
    public ResultMsg getAllTemplate() {
        List<RuleTemplate>  view = dao.getAllTemplate();
        view.forEach(x->{
            int count = 0;
            try {
                count = monitorService.getMonitorCountByTemplateId(x.getUuid(),x.getLightType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            x.setUsedCount(count);
        });
        return ResCommon.getCommonResultMsg(view);
    }


    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
}
