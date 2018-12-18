package topo.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import topo.dao.TopoDao;
import topo.entity.TopoBusinessLinkEntity;
import topo.entity.TopoBusinessNodeEntity;
import java.io.IOException;
import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class TopoDaoImpl implements TopoDao {

    private static final String ip = "http://127.0.0.1";
    private static final String CONFIG_PORT = "8085";
//    private static final String IP = "http://172.17.5.135";
//    private static final String IP = "http://172.31.105.232";
//    private static final String CONFIG_PORT = "30006";
    private static final String MONITOR_PREFIX = "topo";
    private static final String PATH_GET_WEAVE_NODES = "getAllWeaveTopoNode";
    private static final String PATH_GET_WEAVE_LINKS = "getAllWeaveTopoLink";



    private static final String HTTP="http://";


    private String topoPrefix() {
//        try {
//            String ip = EtcdUtil.getClusterIpByServiceName("topo-core-service");
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
    public List<TopoBusinessNodeEntity> getAllWeaveTopoNode() {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_WEAVE_NODES,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoBusinessNodeEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoBusinessLinkEntity> getAllWeaveTopoLink() {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_WEAVE_LINKS,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoBusinessLinkEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
