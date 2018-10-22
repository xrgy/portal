package monitor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import monitor.entity.OperationMonitorEntity;
import monitor.entity.view.OperationMonitorView;

import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorService {

    /**
     * 插入网络设备监控记录
     * @return
     */
    public ResultMsg addNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException;


    /**
     * 插入中间件监控记录
     * @return
     */
    public ResultMsg addMiddleWareMonitorRecord(OperationMonitorView view) throws JsonProcessingException;


    /**
     * 插入数据库监控记录
     * @return
     */
    public ResultMsg addDataBaseMonitorRecord(OperationMonitorView view) throws JsonProcessingException;


    /**
     * 插入虚拟化监控记录
     * @return
     */
    public ResultMsg addVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException;


    /**
     * 插入容器监控记录
     * @return
     */
    public ResultMsg addContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException;

    /**
     * 删除监控记录
     * @param uuids
     * @return
     */
    public ResultMsg delNetworkMonitorRecord(List<String> uuids);

    /**
     * 通过uuid获取监控记录
     * @param uuid
     * @return
     */
    public ResultMsg getMonitorRecord(String uuid);

    /**
     * 更新网络设备监控记录
     * @param view
     * @return
     */
    ResultMsg updateNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException;
}
