package monitorConfig.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.EtcdUtil;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.metric.Metrics;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.metric.ResMetricInfo;
import monitorConfig.entity.metric.UpTemplateView;
import monitorConfig.entity.template.*;
import org.apache.tomcat.util.digester.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class MonitorConfigDaoImpl implements MonitorConfigDao {

//    private static final String  = "http://127.0.0.1";
    private static final String CONFIG_PORT = "8081";
//    private static final String IP = "http://172.17.5.135";
//    private static final String IP = "http://172.31.105.232";
//    private static final String CONFIG_PORT = "30006";
    private static final String MONITOR_PREFIX = "monitorConfig";
    private static final String PATH_METRIC_INFO = "getMetricInfo";
    private static final String PATH_NAME_DUP = "isTemplateNameDup";
    private static final String PATH_ADD_TEMPLATE = "template";
    private static final String PATH_GET_TEMPLATE = "getTemplate";
    private static final String PATH_GET_AVL_RULE = "getAvlRule";
    private static final String PATH_GET_PERF_RULE = "getPerfRule";
    private static final String PATH_ADD_AVL_MONITOR = "addAvlRuleMonitorList";
    private static final String PATH_ADD_PERF_MONITOR = "addPerfRuleMonitorList";
    private static final String PATH_ADD_TEMPLATE_MONITOR = "addTemplateMonitor";
    private static final String PATH_GET_METRICS_BY_LIGHT = "getMetricsUseLight";
    private static final String PATH_ADD_TEMPLATE_ETCD = "addAlertTemplateToEtcd";
    private static final String PATH_DEL_ALERT_MONITOR_RULE = "delAlertMonitorRule";
    private static final String PATH_GET_OPEN_TEMPLATE_DATA = "getOpenTemplateData";
    private static final String PATH_UPDATE_TEMPLATE = "updateTemplate";
    private static final String PATH_GET_ALL_TEMPLATE = "getAllTemplate";

    private static final String HTTP="http://";


    private String monitorConfigPrefix() {
//        try {
            String ip= "127.0.0.1";
//            String ip = EtcdUtil.getClusterIpByServiceName("monitorconfig-core-service");
            return HTTP+ip + ":" + CONFIG_PORT + "/" + MONITOR_PREFIX + "/";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String getJPAInfo() {
//        objectMapper.writeValueAsString();
//        objectMapper.readValue("",TestEntity.class);
        ResponseEntity<TestEntity> response = rest().getForEntity("http://127.0.0.1:8085/monitorConfig/jpa", TestEntity.class);
        return response.getBody().getName();
    }



    @Override
    @SuppressWarnings("unchecked")
    public ResMetricInfo getMetricInfo(String lightType, String monitorMode) {
//        Map map = new HashMap();
//        map.put("lightType",lightType);
//        map.put("monitorMode",monitorMode);
        ResponseEntity<ResMetricInfo> response = rest().getForEntity(monitorConfigPrefix() + PATH_METRIC_INFO +
                "?lightType={1}&monitorMode={2}", ResMetricInfo.class, lightType, monitorMode);
        return response.getBody();
    }

    @Override
    public boolean isTemplateNameDup(String name) {
        return rest().getForObject(monitorConfigPrefix() + PATH_NAME_DUP + "?name={1}", boolean.class, name);
    }

    @Override
    public boolean addTemplate(NewTemplateView view) {
        boolean t = false;
        try {
            t = rest().postForObject(monitorConfigPrefix() + PATH_ADD_TEMPLATE, objectMapper.writeValueAsString(view), boolean.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public MonitorTemplate getTemplateByLightType(String lightType) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_TEMPLATE + "?lightType={1}", String.class, lightType);
        try {
            return objectMapper.readValue(response.getBody(), MonitorTemplate.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AlertAvlRuleEntity> getAvlRuleByTemplateId(String templateId) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_AVL_RULE + "?templateId={1}", String.class, templateId);
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<List<AlertAvlRuleEntity>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AlertPerfRuleEntity> getPerfRuleByTemplate(String templateId) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_PERF_RULE + "?templateId={1}", String.class, templateId);
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<List<AlertPerfRuleEntity>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public boolean addAvlRuleMonitorList(List<AlertAvlRuleMonitorEntity> avlRuleMonitorList) {
//
//        boolean t = false;
//        try {
//            t = rest().postForObject(monitorConfigPrefix() + PATH_ADD_AVL_MONITOR, objectMapper.writeValueAsString(avlRuleMonitorList), boolean.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return t;
//    }
//
//    @Override
//    public boolean addPerfRuleMonitorList(List<AlertPerfRuleMonitorEntity> perfRuleMonitorList) {
//        boolean t = false;
//        try {
//            t = rest().postForObject(monitorConfigPrefix() + PATH_ADD_PERF_MONITOR, objectMapper.writeValueAsString(perfRuleMonitorList), boolean.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return t;
//    }
//
//    @Override
//    public boolean addTemplateMonitorEntity(AlertRuleTemplateMonitorEntity templateMonitorEntity) {
//        boolean t = false;
//        try {
//            t = rest().postForObject(monitorConfigPrefix() + PATH_ADD_TEMPLATE_MONITOR, objectMapper.writeValueAsString(templateMonitorEntity), boolean.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return t;
//    }

    @Override
    public List<Metrics> getMetricsByLightType(String lightTypeId) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_METRICS_BY_LIGHT + "?lightTypeId={1}", String.class, lightTypeId);
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Metrics>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addAlertTemplateToEtcd(String lightTypeId, String templateId, RuleMonitorEntity ruleMonitorEntity) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("lightTypeId", lightTypeId);
        map.put("templateId", templateId);
        map.put("ruleMonitorEntity", objectMapper.writeValueAsString(ruleMonitorEntity));
        rest().postForObject(monitorConfigPrefix() + PATH_ADD_TEMPLATE_ETCD, map, String.class);
    }

    @Override
    public boolean delAlertRuleByUuid(String uuid) {
        rest().delete(monitorConfigPrefix() + PATH_DEL_ALERT_MONITOR_RULE + "?uuid={1}", uuid);
        return true;
    }

    @Override
    public boolean delTemplate(String templateUuid) {
        rest().delete(monitorConfigPrefix() + PATH_ADD_TEMPLATE + "/{1}", templateUuid);
        return false;
    }

    @Override
    public boolean updateTemplate(UpTemplateView view) {
        boolean t = false;
        try {
            t = rest().postForObject(monitorConfigPrefix() + PATH_UPDATE_TEMPLATE, objectMapper.writeValueAsString(view), boolean.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public UpTemplateView getOpenTemplateData(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_OPEN_TEMPLATE_DATA + "?uuid={1}", String.class, uuid);
        try {
            return objectMapper.readValue(response.getBody(), UpTemplateView.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<RuleTemplate> getAllTemplate() {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_ALL_TEMPLATE, String.class);
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<List<RuleTemplate>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
