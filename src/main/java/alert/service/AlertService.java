package alert.service;


import alert.entity.AlertAlarmInfo;
import alert.entity.AlertView;
import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertService {

    List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids) throws JsonProcessingException;


    /**
     * 删除监控记录级联告警删除该monitor的告警记录
     * @param monitorUuid
     * @return
     */
    boolean deleteAlertResourceBymonitoruuid(String monitorUuid);

    ResultMsg getAlertInfo(AlertView view) throws JsonProcessingException;
}
