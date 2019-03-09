package alert.service.impl;

import alert.dao.AlertDao;
import alert.entity.AlertAlarmInfo;
import alert.entity.AlertEntity;
import alert.service.AlertService;
import business.dao.BusinessDao;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.service.MonitorService;
import monitorConfig.entity.metric.Metrics;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class AlertServiceImpl implements AlertService {

    private static final String RULE_ANME_START="rule";
    private static final String AVL_RULE_NAME="_avl";
    private static final String ONE="one";
    private static final String TWO="two";
    private static final String ONE_LEVEL_PERF="_one_level_perf";
    private static final String TWO_LEVEL_PERF="_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AlertDao dao;

    @Autowired
    MonitorConfigService monitorConfigService;


    @Override
    public List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids) throws JsonProcessingException {
        return dao.getAlertInfoByMonitorUuids(monitorUuids);
    }

    @Override
    public boolean deleteAlertResourceBymonitoruuid(String monitorUuid) {
        return dao.deleteAlertResourceBymonitoruuid(monitorUuid);
    }

    @Override
    public ResultMsg getAlertInfo(int severity, int resolve, String uuid) {
        List<AlertEntity> alertList = dao.getAlertInfo(severity, resolve, uuid);
        alertList.forEach(x->{
            String description = x.getDescription();
            x.setAlertSource(getNameFromDescription(description));
            Metrics metric = null;
            if (description.contains("当前值")){
                //通过性能表 找指标名
                metric = monitorConfigService.getMetricByRule("perf",x.getAlertRuleUuid());
            }else {
                //通过可用性表 找指标名
                metric =  monitorConfigService.getMetricByRule("avl",x.getAlertRuleUuid());
            }
            x.setAlertInfo(metric.getName());

        });
        return ResCommon.getCommonResultMsg(alertList);
    }

    public String getNameFromDescription(String description){
        String[] split = description.split("\\(");
        if (split.length>0){
            return split[0];
        }else {
            return "";
        }
    }
}
