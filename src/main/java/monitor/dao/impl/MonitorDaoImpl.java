package monitor.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.dao.MonitorDao;
import monitor.entity.CasTransExporterModel;
import monitor.entity.LightTypeEntity;
import monitor.entity.OperationMonitorEntity;
import monitor.entity.view.Cluster;
import monitor.entity.view.Host;
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
public class MonitorDaoImpl implements MonitorDao {

    private static final String IP = "http://127.0.0.1";
    private static final String MONITOR_PORT = "8084";
    private static final String MONITOR_PREFIX = "monitor";
    private static final String PATH_INSERT_MONITOR_RECORD = "addMonitorRecord";
    private static final String PATH_INSERT_MONITOR_RECORD_LIST = "addMonitorRecordList";
    private static final String PATH_LIGHT="getLightType";
    private static final String PATH_GET_CLUSTERS="getClusterList";
    private static final String PATH_GET_CVK_AND_VM="getCvkAndVmList";

    private String monitorPrefix() {
        return IP + ":" + MONITOR_PORT + "/" + MONITOR_PREFIX + "/";
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public boolean insertMonitorRecord(OperationMonitorEntity entity) throws JsonProcessingException {
        return rest().postForObject(monitorPrefix() + PATH_INSERT_MONITOR_RECORD, objectMapper.writeValueAsString(entity), boolean.class);
    }

    @Override
    public List<LightTypeEntity> getLightTypeEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_LIGHT,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<LightTypeEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cluster> getClusterListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException {
        ResponseEntity<String> response = rest().postForEntity(monitorPrefix()+PATH_GET_CLUSTERS,objectMapper.writeValueAsString(casTransExporterModel),String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<Cluster>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertMonitorRecordList(List<OperationMonitorEntity> entity) throws JsonProcessingException {
        return rest().postForObject(monitorPrefix() + PATH_INSERT_MONITOR_RECORD_LIST, objectMapper.writeValueAsString(entity), boolean.class);

    }

    @Override
    public List<Host> getCvkAndVmListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException {
        ResponseEntity<String> response = rest().postForEntity(monitorPrefix()+PATH_GET_CVK_AND_VM,objectMapper.writeValueAsString(casTransExporterModel),String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<Host>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
