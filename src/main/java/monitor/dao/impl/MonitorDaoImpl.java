package monitor.dao.impl;

import business.entity.PageBean;
import business.entity.PageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.EtcdUtil;
import monitor.dao.MonitorDao;
import monitor.entity.*;
import monitor.entity.view.*;
import monitor.entity.view.k8sView.Container;
import monitor.entity.view.k8sView.Node;
import monitorConfig.entity.metric.Metrics;
import monitorConfig.entity.template.MonitorTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class MonitorDaoImpl implements MonitorDao {

//    private static final String IP = "http://127.0.0.1";
    private static final String MONITOR_PORT = "8084";

//    private static final String IP = "http://172.17.5.135";
//    private static final String IP = "http://172.31.105.232";
//    private static final String MONITOR_PORT = "30004";
    private static final String MONITOR_PREFIX = "monitor";
    private static final String PATH_INSERT_MONITOR_RECORD = "addMonitorRecord";
    private static final String PATH_INSERT_MONITOR_RECORD_LIST = "addMonitorRecordList";
    private static final String PATH_LIGHT="getLightType";
    private static final String PATH_GET_CLUSTERS="getClusterList";
    private static final String PATH_GET_CVK_AND_VM="getCvkAndVmList";
    private static final String PATH_GET_NODE_LIST="getNodeList";
    private static final String PATH_GET_CONTAINER_LIST="getContainerList";
    private static final String PATH_DEL_MONITOR_RECORD="delNetworkMonitorRecord";
    private static final String PATH_GET_ALL_MONITOR_RECORD="getAllMonitorRecord";
    private static final String PATH_GET_MONITOR_RECORD_BY_ROOTID="getMonitorRecordByRootId";
    private static final String PATH_GET_MONITOR_RECORD_BY_UUID="getMonitorRecord";
    private static final String PATH_UPDATE_MONITOR_RECORD = "updateMonitorRecord";
    private static final String PATH_GET_MONITOR_RECORD_BY_TEMPLATE = "getMonitorRecordByTemplateId";
    private static final String PATH_GET_ALL_NETWORK_MONITOR_RECORD="getAllNetworkMonitorRecord";
    private static final String PATH_GET_ALL_TOMCAT_MONITOR_RECORD="getAllTomcatMonitorRecord";
    private static final String PATH_GET_ALL_DB_MONITOR_RECORD="getAllDbMonitorRecord";
    private static final String PATH_GET_ALL_CAS_MONITOR_RECORD="getAllCasMonitorRecord";
    private static final String PATH_GET_ALL_HOST_MONITOR_RECORD="getAllHostMonitorRecord";
    private static final String PATH_GET_ALL_VM_MONITOR_RECORD="getAllVmMonitorRecord";
    private static final String PATH_GET_ALL_K8S_MONITOR_RECORD="getAllK8sMonitorRecord";
    private static final String PATH_GET_ALL_K8SNODE_MONITOR_RECORD="getAllK8snodeMonitorRecord";
    private static final String PATH_GET_ALL_K8SCONTAINER_MONITOR_RECORD="getAllK8scontainerMonitorRecord";

    private static final String PATH_GET_ALL_NODE_AND_CONTAINER_BY_K8S="getAllNodeAndContainerByK8suuid";
    private static final String PATH_GET_ALL_CONTAINER_BY_K8SNODE="getAllContainerByK8sNodeuuid";
    private static final String PATH_GET_ALL_CVK_AND_VM_BY_CAS="getAllCvkAndVmByCasuuid";
    private static final String PATH_GET_ALL_VM_BY_CVK="getAllVmByCvkuuid";
    private static final String PATH_IS_IP_DUPLICATE="isMonitorRecordIpDup";
    private static final String PATH_IS_IP_DUPLICATE_NOTP="isMonitorRecordIpDupNotP";
    private static final String PATH_GET_BUSMONITOR_LIST="getBusMonitorListByPage";
    private static final String PATH_GET_QUOTA_VALUE="getQuotaValueByName";
    private static final String PATH_DB_CAN_ACCESS="dbCanAccess";
    private static final String PATH_K8S_CAN_ACCESS="k8sCanAccess";
    private static final String PATH_TOMCAT_CAN_ACCESS="tomcatCanAccess";






    private static final String HTTP="http://";


    private String monitorPrefix(){
//        try {
            String ip ="127.0.0.1";
//           String ip = EtcdUtil.getClusterIpByServiceName("monitor-core-service");
            return HTTP+ip + ":" + MONITOR_PORT + "/" + MONITOR_PREFIX + "/";
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
    public String getMonitorRecordByUuid(String uuid,String lightType) {
        ResponseEntity<String> response =  rest().getForEntity(monitorPrefix()+PATH_GET_MONITOR_RECORD_BY_UUID+"?uuid={1}&lightType={2}", String.class,uuid,lightType);
        return response.getBody();
    }

    @Override
    public boolean insertMonitorRecord(Object data, String lightType) throws JsonProcessingException {
        return rest().postForObject(monitorPrefix() + PATH_INSERT_MONITOR_RECORD+"?lightType={1}", objectMapper.writeValueAsString(data), boolean.class,lightType);
    }

//    @Override
//    public boolean insertMonitorRecord(OperationMonitorEntity entity) throws JsonProcessingException {
//        return rest().postForObject(monitorPrefix() + PATH_INSERT_MONITOR_RECORD, objectMapper.writeValueAsString(entity), boolean.class);
//    }

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

//    @Override
//    public List<Cluster> getClusterListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException {
//        ResponseEntity<String> response = rest().postForEntity(monitorPrefix()+PATH_GET_CLUSTERS,objectMapper.writeValueAsString(casTransExporterModel),String.class);
//        try {
//            return objectMapper.readValue(response.getBody(),new TypeReference<List<Cluster>>(){});
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

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

    @Override
    public List<Container> getContainerListByExporter(String ip, String port) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_CONTAINER_LIST+"?ip={1}&port={2}",String.class,ip,port);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<Container>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Node> getNodeListByExporter(String ip, String port) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_NODE_LIST+"?ip={1}&port={2}",String.class,ip,port);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<Node>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delMonitorRecord(String uuid,String lightType) {
       return rest().getForObject(monitorPrefix()+PATH_DEL_MONITOR_RECORD+"?uuid={1}&lightType={2}",boolean.class,uuid,lightType);
    }

    @Override
    public List<OperationMonitorEntity> getAllMonitorRecord() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<OperationMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OperationMonitorEntity> getMonitorRecordByRootId(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_MONITOR_RECORD_BY_ROOTID+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<OperationMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getMonitorRecordByTemplateId(String uuid,String lightType) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_MONITOR_RECORD_BY_TEMPLATE+"?uuid={1}&lightType={2}",String.class,uuid,lightType);

        return response.getBody();
    }

    @Override
    public List<NetworkMonitorEntity> getAllNetworkMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_NETWORK_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<NetworkMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TomcatMonitorEntity> getAllTomcatMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_TOMCAT_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TomcatMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DBMonitorEntity> getAllDbMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_DB_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<DBMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CasMonitorEntity> getAllCasMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_CAS_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<CasMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<HostMonitorEntity> getAllHostMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_HOST_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<HostMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<VmMonitorEntity> getAllVmMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_VM_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<VmMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<K8sMonitorEntity> getAllK8sMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_K8S_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<K8sMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<K8snodeMonitorEntity> getAllK8snodeMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_K8SNODE_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<K8snodeMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<K8scontainerMonitorEntity> getAllK8sContainerMonitorEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_K8SCONTAINER_MONITOR_RECORD,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<K8scontainerMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<K8sNodeAndContainerView> getAllNodeAndContainerByK8suuid(String uuid) throws JsonProcessingException {

        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_NODE_AND_CONTAINER_BY_K8S+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<K8sNodeAndContainerView>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<K8scontainerMonitorEntity> getAllContainerByK8sNodeuuid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_CONTAINER_BY_K8SNODE+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<K8scontainerMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CvkAndVmView> getAllCvkAndVmByCasuuid(String uuid) throws JsonProcessingException {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_CVK_AND_VM_BY_CAS+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<CvkAndVmView>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<VmMonitorEntity> getAllVmByCvkuuid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_GET_ALL_VM_BY_CVK+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<VmMonitorEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isMonitorRecordIpDup(String ip, String lightType) {
        return rest().getForObject(monitorPrefix() + PATH_IS_IP_DUPLICATE+ "?ip={1}&lightType={2}", boolean.class, ip,lightType);
    }

    @Override
    public PageBean getBusMonitorListByPage(PageData page) throws JsonProcessingException {
        String response = rest().postForObject(monitorPrefix()+PATH_GET_BUSMONITOR_LIST,objectMapper.writeValueAsString(page),String.class);
        try {
            return objectMapper.readValue(response,PageBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String getQuotaValue(String monitorUuid,String quotaName) {
        String quotaValue = rest().getForObject(monitorPrefix()+PATH_GET_QUOTA_VALUE+"?monitorUUid={1}&quotaName={2}",String.class,monitorUuid,quotaName);
        return quotaValue;
    }

    @Override
    public boolean isMonitorRecordIpDupNotP(String ip, String lightType, String uuid) {
        return rest().getForObject(monitorPrefix() + PATH_IS_IP_DUPLICATE_NOTP+ "?ip={1}&lightType={2}&uuid={3}", boolean.class, ip,lightType,uuid);
    }

    @Override
    public AccessBackView dbCanAccess(DbAccessView view) throws JsonProcessingException {
        String response = rest().postForObject(monitorPrefix()+PATH_DB_CAN_ACCESS,objectMapper.writeValueAsString(view),String.class);
        try {
            return objectMapper.readValue(response,AccessBackView.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AccessBackView k8sCanAccess(K8sAccessView view) throws JsonProcessingException {
        String response = rest().postForObject(monitorPrefix()+PATH_K8S_CAN_ACCESS,objectMapper.writeValueAsString(view),String.class);
        try {
            return objectMapper.readValue(response,AccessBackView.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AccessBackView tomcatCanAccess(TomcatAccessView view) throws JsonProcessingException {
        String response = rest().postForObject(monitorPrefix()+PATH_TOMCAT_CAN_ACCESS,objectMapper.writeValueAsString(view),String.class);
        try {
            return objectMapper.readValue(response,AccessBackView.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
