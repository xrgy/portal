package topo.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.EtcdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import topo.dao.TopoDao;
import topo.entity.*;

import java.io.IOException;
import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class TopoDaoImpl implements TopoDao {

//    private static final String ip = "127.0.0.1";
    private static final String CONFIG_PORT = "8085";
//    private static final String IP = "http://172.17.5.135";
//    private static final String IP = "http://172.31.105.232";
//    private static final String CONFIG_PORT = "30006";
    private static final String MONITOR_PREFIX = "topo";
    private static final String PATH_GET_WEAVE_NODES = "getAllWeaveTopoNode";
    private static final String PATH_GET_WEAVE_LINKS = "getAllWeaveTopoLink";
    private static final String PATH_GET_BUSINESS_TOPO_NODE= "getBusinessNodeByUuid";
    private static final String PATH_GET_CANVAS_BY_UUID= "getCanvasByUUid";

    private static final String PATH_GET_NET_TOPO_NODES = "getAllNetTopoNode";
    private static final String PATH_GET_NET_TOPO_LINKS = "getAllNetTopoLink";
    private static final String PATH_GET_CANVAS_BY_TYPE = "getCanvasByType";
    private static final String PATH_GET_NET_TOPO_PORT = "getAllNetTopoPort";
    private static final String PATH_DELETE_NET_TOPO_RESOURCE_MONITOR = "deleteTopoResourceBymonitoruuid";
    private static final String PATH_GET_TOPO_NET_LINK_RATE = "getInterfaceRate";
    private static final String PATH_SAVE_TOPO_NODES = "insertTopoNodeList";



    private static final String HTTP="http://";


    private String topoPrefix() {
        try {
            String ip = EtcdUtil.getClusterIpByServiceName("topo-core-service");
            return HTTP+ip + ":" + CONFIG_PORT + "/" + MONITOR_PREFIX + "/";
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
    public TopoBusinessNodeEntity getBusinessTopoNodeByUuid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_BUSINESS_TOPO_NODE+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),TopoBusinessNodeEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TopoCanvasEntity getCanvasByUuid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_CANVAS_BY_UUID+"?uuid={1}",String.class,uuid);
        try {
            return objectMapper.readValue(response.getBody(),TopoCanvasEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoBusinessNodeEntity> getAllWeaveTopoNode(String canvasId) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_WEAVE_NODES+"?canvasId={1}",String.class,canvasId);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoBusinessNodeEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoBusinessLinkEntity> getAllWeaveTopoLink(String canvasId) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_WEAVE_LINKS+"?canvasId={1}",String.class,canvasId);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoBusinessLinkEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoNodeEntity> getTopoNodeByCanvasId(String canvasId) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_NET_TOPO_NODES+"?canvasId={1}",String.class,canvasId);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoNodeEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoLinkEntity> getTopoLinkByCanvasId(String canvasId) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_NET_TOPO_LINKS+"?canvasId={1}",String.class,canvasId);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoLinkEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoCanvasEntity> getCanvasByType(String name) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_CANVAS_BY_TYPE+"?name={1}",String.class,name);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoCanvasEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TopoPortEntity> getAllPorts() {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_NET_TOPO_PORT,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoPortEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteTopoResourceBymonitoruuid(String monitorUuid) {
        return rest().getForObject(topoPrefix()+PATH_DELETE_NET_TOPO_RESOURCE_MONITOR+"?monitorUuid={1}",boolean.class,monitorUuid);
    }

    @Override
    public List<TopoLinkRateView> getInterfaceRate(String monitorUuid, String quotaName) {
        ResponseEntity<String> response = rest().getForEntity(topoPrefix()+PATH_GET_TOPO_NET_LINK_RATE,String.class);
        try {
            return objectMapper.readValue(response.getBody(),new TypeReference<List<TopoLinkRateView>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertTopoNodeList(List<TopoNodeEntity> nodes) throws JsonProcessingException {
        return rest().postForObject(topoPrefix() + PATH_SAVE_TOPO_NODES, objectMapper.writeValueAsString(nodes), boolean.class);

    }


}
