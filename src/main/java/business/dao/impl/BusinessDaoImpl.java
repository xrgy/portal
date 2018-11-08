package business.dao.impl;

import business.dao.BusinessDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;



/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class BusinessDaoImpl implements BusinessDao {

//    private static final String IP = "http://127.0.0.1";
//    private static final String CONFIG_PORT = "8088";
    private static final String IP = "http://172.31.105.232";
    private static final String CONFIG_PORT = "30008";
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

    private String monitorConfigPrefix() {
        return IP + ":" + CONFIG_PORT + "/" + MONITOR_PREFIX + "/";
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Autowired
    ObjectMapper objectMapper;


}
