package monitorConfig.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.metric.ResMetricInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class MonitorConfigDaoImpl implements MonitorConfigDao {

    private static final String IP = "http://127.0.0.1";
    private static final String CONFIG_PORT = "8086";
    private static final String MONITOR_PREFIX = "monitorConfig";
    private static final String PATH_METRIC_INFO = "getMetricInfo";
    private static final String PATH_NAME_DUP="isTemplateNameDup";
    private static final String PATH_ADD_TEMPLATE="addTemplate";

    private String monitorConfigPrefix() {
        return IP + ":" + CONFIG_PORT + "/" + MONITOR_PREFIX + "/";
    }

    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String getJPAInfo() {
//        objectMapper.writeValueAsString();
//        objectMapper.readValue("",TestEntity.class);
        ResponseEntity<TestEntity> response = rest().getForEntity("http://127.0.0.1:8085/monitorConfig/jpa",TestEntity.class);
        return response.getBody().getName();
    }



    @Override
    @SuppressWarnings("unchecked")
    public ResMetricInfo getMetricInfo(String lightType, String monitorMode) {
//        Map map = new HashMap();
//        map.put("lightType",lightType);
//        map.put("monitorMode",monitorMode);
        ResponseEntity<ResMetricInfo> response = rest().getForEntity(monitorConfigPrefix()+PATH_METRIC_INFO+
                "?lightType={1}&monitorMode={2}",ResMetricInfo.class,lightType,monitorMode);
        return response.getBody();
    }

    @Override
    public boolean isTemplateNameDup(String name) {
        return rest().getForObject(monitorConfigPrefix()+PATH_NAME_DUP+"?name={1}",boolean.class,name);
    }

    @Override
    public boolean addTemplate(NewTemplateView view) {
        boolean t = false;
        try {
            t = rest().postForObject(monitorConfigPrefix()+PATH_ADD_TEMPLATE,objectMapper.writeValueAsString(view),boolean.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }
}
