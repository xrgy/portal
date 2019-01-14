package monitor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import monitor.entity.CasTransExporterModel;
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


    /**
     * 更新虚拟化监控记录
     * @param view
     * @return
     */
    ResultMsg updateVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException;


    /**
     * 更新容器监控记录
     * @param view
     * @return
     */
    ResultMsg updateContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException;

    /**
     * 通过templateid获取监控数量
     * @param uuid
     * @return
     */
    int getMonitorCountByTemplateId(String uuid);

    /**
     * business获取monitor record进行添加
     * @return
     */
    ResultMsg getBusinessMonitorRecord();


    /**
     * 根据ip和api port获取容器列表
     * @param ip
     * @param apiPort
     * @return
     */
    ResultMsg getContainerListByExporter(String ip, String apiPort);


    /**
     * 获取cvk 和 vm列表
     * @param casTransExporterModel
     * @return
     */
    ResultMsg getCvkAndVmListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException;
}
