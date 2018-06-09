package monitorConfig.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.metric.ResMetricInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class MonitorConfigDaoImpl implements MonitorConfigDao {

    private static final String IP = "http://127.0.0.1";
    private static final String CONFIG_PORT = "8086";
    private static final String MONITOR_PREFIX = "monitorConfig";
    private static final String PATH_METRIC_INFO = "getMetricInfo";

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
}
