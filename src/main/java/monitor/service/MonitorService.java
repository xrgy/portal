package monitor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import monitor.entity.view.OperationMonitorView;


/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorService {

    /**
     * 插入网络设备监控记录
     * @return
     */
    public ResultMsg addNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException;
}
