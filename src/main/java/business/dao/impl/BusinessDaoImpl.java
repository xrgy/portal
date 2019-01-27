package business.dao.impl;

import business.dao.BusinessDao;
import business.entity.BusinessEntity;
import business.entity.BusinessResourceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class BusinessDaoImpl implements BusinessDao {

    //    private static final String IP = "http://127.0.0.1";
//    private static final String CONFIG_PORT = "8088";
    private static final String ip = "127.0.0.1";
    private static final String CONFIG_PORT = "8088";
    private static final String BUSINESS_PREFIX = "business";
    private static final String PATH_BUSINESS_LIST = "getBusinessList";
    private static final String PATH_ADD_BUSINESS_RESOURCE_LIST = "addBusinessResourceList";
    private static final String PATH_ADD_BUSINESS_RESOURCE_LIST_BY_MONITOR = "getBusinessResourceByMonitorUuid";
    private static final String PATH_GET_BUSINESS_NODE = "getBusinessNode";
    private static final String HTTP = "http://";

    private String businessPrefix() {
        //        try {
//            String ip = EtcdUtil.getClusterIpByServiceName("business-core-service");
        return HTTP + ip + ":" + CONFIG_PORT + "/" + BUSINESS_PREFIX + "/";
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
    public List<BusinessEntity> getBusinessList() {
        ResponseEntity<String> response = rest().getForEntity(businessPrefix()+PATH_BUSINESS_LIST,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<BusinessEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertBusinessResourceList(List<BusinessResourceEntity> resourceList) throws JsonProcessingException {
        return rest().postForObject(businessPrefix() + PATH_ADD_BUSINESS_RESOURCE_LIST, objectMapper.writeValueAsString(resourceList), boolean.class);
    }

    @Override
    public List<BusinessResourceEntity> getBusinessResourceByMonitorUuid(String monitorUuid) {
        ResponseEntity<String> response = rest().getForEntity(businessPrefix()+PATH_ADD_BUSINESS_RESOURCE_LIST_BY_MONITOR+"?monitorUuid={1}",String.class,monitorUuid);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<BusinessResourceEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BusinessEntity getBusinessNode(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(businessPrefix() + PATH_GET_BUSINESS_NODE + "?uuid={1}", String.class, uuid);
        try {
            return objectMapper.readValue(response.getBody(), BusinessEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
