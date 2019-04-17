package monitorConfig.service.impl;

import business.entity.PageBean;
import business.entity.PageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.CommonEnum;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import monitor.service.MonitorService;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.metric.*;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.template.*;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.BeanUtils;
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
    public boolean isTemplateNameDup(String name,String templateUuid) {
        return dao.isTemplateNameDup(name,templateUuid);
    }

    @Override
    public ResultMsg addTemplate(NewTemplateView view) {
        boolean res = dao.addTemplate(view);
        return ResCommon.genSimpleResByBool(res);
    }

    @Override
    public MonitorTemplate getTemplateByLightType(String lightType,String monitorMode) {
//        ResultMsg msg = new ResultMsg();
        MonitorTemplate templateList = dao.getTemplateByLightType(lightType);
//        JSONObject object = new JSONObject();
//        object.put("lightType",lightType);
//        object.put("monitorMode",monitorMode);
//        object.put("template",templateList);
//        if (null == templateList){
//            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            msg.setMsg(CommonEnum.MSG_ERROR.value());
//        }else {
//            msg.setCode(HttpStatus.OK.value());
//            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
//            msg.setData(templateList);
//        }
        return templateList;

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
        boolean res =  dao.delAlertRuleByUuid(tempuuid);
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
    public ResultMsg updateTemplate(NewTemplateView view) {
        //NewTemplateView 转为UpTemplateView
        UpTemplateView upTemplateView = new UpTemplateView();
        BeanUtils.copyProperties(view,upTemplateView);
        List<UpAvaliable> available = new ArrayList<>();
        List<UpPerformance> performance = new ArrayList<>();
        view.getAvailable().forEach(x->{
            x.getData().forEach(y->{
                UpAvaliable avl = new UpAvaliable();
                avl.setUuid(y.getAvlUuid());
                avl.setMetricUuid(y.getUuid());
                avl.setSeverity(y.getSeverity());
                avl.setDescription(y.getDescription());
                available.add(avl);
            });
        });
        upTemplateView.setAvailable(available);
        view.getPerformance().forEach(x->{
            x.getData().forEach(y->{
                UpPerformance perf = new UpPerformance();
                BeanUtils.copyProperties(y,perf);
                perf.setMetricUuid(y.getUuid());
                performance.add(perf);
            });
        });
        upTemplateView.setPerformance(performance);
        boolean res = dao.updateTemplate(upTemplateView);
        if (res){

        }
        return ResCommon.genSimpleResByBool(res);
    }

    @Override
    public ResultMsg OpenTemplate(String uuid) throws IOException {
        UpTemplateView templateView = dao.getOpenTemplateData(uuid);

        int count = monitorService.getMonitorCountByTemplateId(uuid,templateView.getLightType());
        templateView.setUsedCount(count);

//        ResMetricInfo newInfo = new ResMetricInfo();
//        newInfo.setMonitorMode(templateView.getMonitorMode());
//        List<MetricInfo> avlMetricInfo = new ArrayList<>();
//        templateView.getAvailable().forEach(x->{
//
//        });

//        ResMetricInfo info = dao.getMetricInfo(lightType,monitorMode);


        return ResCommon.getCommonResultMsg(templateView);
    }

    @Override
    public ResultMsg getAllTemplate(PageData page,String type) throws JsonProcessingException {
//        PageBean<RuleTemplate> view = dao.getAllTemplate(page,type);
        PageBean view = dao.getAllTemplate(page,type);
        List<RuleTemplate> filterList = objectMapper.convertValue(view.getList(), new TypeReference<List<RuleTemplate>>() { });
        filterList.forEach(x->{
//            RuleTemplate x = (RuleTemplate)y;
            int count = 0;
            try {
                count = monitorService.getMonitorCountByTemplateId(x.getUuid(),x.getLightType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            x.setUsedCount(count);
        });
        view.setList(filterList);
        return ResCommon.getCommonResultMsg(view);
    }

    @Override
    public Metrics getMetricByRule(String perf, String alertRuleUuid) {
        return dao.getMetricByRule(perf, alertRuleUuid);
    }

    @Override
    public List<AlertRuleTemplateEntity> getAllTemplateNo() {
        return dao.getAllTemplateNo();
    }


    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
}
