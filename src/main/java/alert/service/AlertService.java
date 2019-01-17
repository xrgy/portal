package alert.service;


import alert.entity.AlertAlarmInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertService {

    List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids) throws JsonProcessingException;


}
