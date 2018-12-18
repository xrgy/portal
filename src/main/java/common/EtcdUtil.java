package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gy on 2018/12/8.
 */
public class EtcdUtil {
//    private static final String IP="47.105.64.176";
    private static final String IP="47.94.157.199";

    //    private static final String IP="172.31.105.232";
    private static final String ETCD_PORT="2379";
    private static final String ETCD_PREFIX="/v2/keys/registry/services/specs/default";
    private static final String HTTP="http://";

    private static String etcdPrefix() {
        return HTTP+IP + ":" + ETCD_PORT + "/" + ETCD_PREFIX + "/";
    }





    public static String getClusterIpByServiceName(String serviceName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate rest = new RestTemplate();
        String response = rest.getForObject(etcdPrefix()+serviceName,String.class);
        Map<String,Object> resmap = objectMapper.readValue(response,HashMap.class);
        Map<String,String> nodeMap = (Map<String, String>) resmap.get("node");
        String cont =  nodeMap.get("value");
        Map<String,Object> contMap = objectMapper.readValue(cont,HashMap.class);
        Map<String,String> specMap = (Map<String, String>) contMap.get("spec");
        String clusterIP = specMap.get("clusterIP");
        return clusterIP;
    }
}
