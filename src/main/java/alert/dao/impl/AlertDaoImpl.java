package alert.dao.impl;

import alert.dao.AlertDao;
import alert.entity.AlertAlarmInfo;
import business.dao.BusinessDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.EtcdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class AlertDaoImpl implements AlertDao {

//    private static final String ip = "127.0.0.1";
//    private static final String CONFIG_PORT = "8086";
    private static final String IP = "http://172.31.105.232";
    private static final String CONFIG_PORT = "8095";
    private static final String ALERT_PREFIX = "alerts";
    private static final String PATH_GET_ALARM_INFO_MONITOR_UUID = "getAlertInfoByMonitorUuids";
    private static final String PATH_DELTE_ALERT_BY_MONITOR = "deleteAlertResourceBymonitoruuid";


    private static final String HTTP="http://";


    private String alertPrefix() {
                try {
            String ip = EtcdUtil.getClusterIpByServiceName("alert-coll-service");
        return HTTP+ip + ":" + CONFIG_PORT + "/" + ALERT_PREFIX + "/";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Autowired
    ObjectMapper objectMapper;


    @Override
    public List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids) throws JsonProcessingException {
        String response = rest().postForObject(alertPrefix()+PATH_GET_ALARM_INFO_MONITOR_UUID,objectMapper.writeValueAsString(monitorUuids),String.class);
        try {
            return objectMapper.readValue(response,new TypeReference<List<AlertAlarmInfo>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteAlertResourceBymonitoruuid(String monitorUuid) {
        return rest().getForObject(alertPrefix()+PATH_DELTE_ALERT_BY_MONITOR+"?monitorUuid={1}",boolean.class,monitorUuid);
    }
}
