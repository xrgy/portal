package monitorConfig.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.entity.OperationMonitorEntity;
import monitorConfig.common.CommonEnum;
import monitorConfig.common.ResultMsg;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.metric.ResMetricInfo;
import monitorConfig.entity.template.*;
import monitorConfig.service.MonitorConfigService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

    private static final String RULE_ANME_START="rule";
    private static final String AVL_RULE_NAME="_avl";
    private static final String ONE="one";
    private static final String TWO="two";
    private static final String ONE_LEVEL_PERF="_one_level_perf";
    private static final String TWO_LEVEL_PERF="_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MonitorConfigDao dao;

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
        ResultMsg msg = new ResultMsg();
        boolean res = dao.addTemplate(view);
        if (res) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }

    @Override
    public ResultMsg getTemplateByLightType(String lightType,String monitorMode) {
        ResultMsg msg = new ResultMsg();
        List<RuleTemplate> templateList = dao.getTemplateByLightType(lightType,monitorMode);
        JSONObject object = new JSONObject();
        object.put("lightType",lightType);
        object.put("monitorMode",monitorMode);
        object.put("template",templateList);
        if (null == templateList){
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }else {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(object);
        }
        return msg;

    }

    @Override
    public RuleMonitorEntity addMonitorRecordAlertRule(OperationMonitorEntity operationMonitorEntity) {
        String monitorUuid = operationMonitorEntity.getUuid();
        String templateId = operationMonitorEntity.getTemplateId();
        List<AlertAvlRuleEntity> avlRuleList = dao.getAvlRuleByTemplateId(templateId);
        List<AlertPerfRuleEntity> perfRuleList = dao.getPerfRuleByTemplate(templateId);
        List<AlertAvlRuleMonitorEntity> avlRuleMonitorList = new ArrayList<>();
        List<AlertPerfRuleMonitorEntity> perfRuleMonitorEntityList = new ArrayList<>();
        avlRuleList.forEach(x->{
            AlertAvlRuleMonitorEntity entity = new AlertAvlRuleMonitorEntity();
            String id =UUID.randomUUID().toString();
            entity.setUuid(id);
            entity.setAlertRuleName(RULE_ANME_START+id+AVL_RULE_NAME);
            entity.setMonitorUuid(monitorUuid);
            entity.setAvlRuleUuid(x.getUuid());
            avlRuleMonitorList.add(entity);
        });
        dao.addAvlRuleMonitorList(avlRuleMonitorList);
        perfRuleList.forEach(x->{
            AlertPerfRuleMonitorEntity entity = new AlertPerfRuleMonitorEntity();
            String id = UUID.randomUUID().toString();
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
        dao.addPerfRuleMonitorList(perfRuleMonitorEntityList);
        AlertRuleTemplateMonitorEntity templateMonitorEntity = new AlertRuleTemplateMonitorEntity();
        templateMonitorEntity.setUuid(UUID.randomUUID().toString());
        templateMonitorEntity.setMonitorUuid(monitorUuid);
        templateMonitorEntity.setTemplateUuid(templateId);
        dao.addTemplateMonitorEntity(templateMonitorEntity);

        RuleMonitorEntity ruleMonitorEntity = new RuleMonitorEntity();
        ruleMonitorEntity.setAvlRuleMonitorList(avlRuleMonitorList);
        ruleMonitorEntity.setPerfRuleMonitorList(perfRuleMonitorEntityList);
        ruleMonitorEntity.setTemplateMonitorEntity(templateMonitorEntity);
        return ruleMonitorEntity;
    }

    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
}
