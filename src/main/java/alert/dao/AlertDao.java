package alert.dao;


import alert.entity.AlertAlarmInfo;
import alert.entity.AlertEntity;
import alert.entity.AlertView;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertDao {
    List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids) throws JsonProcessingException;


    boolean deleteAlertResourceBymonitoruuid(String monitorUuid);

    List<AlertEntity> getAlertInfo(AlertView view) throws JsonProcessingException;
}
