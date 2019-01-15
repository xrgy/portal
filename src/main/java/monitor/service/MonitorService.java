package monitor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import monitor.entity.*;
import monitor.entity.view.OperationMonitorView;

import java.io.IOException;
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
     * @param view
     * @return
     */
    public ResultMsg delNetworkMonitorRecord(List<DelMonitorRecordView> view);

    /**
     * 通过uuid获取监控记录
     * @param uuid
     * @return
     */
//    public ResultMsg getMonitorRecord(String uuid);

    /**
     * 更新网络设备监控记录
     * @param view
     * @return
     */
    ResultMsg updateNetworkMonitorRecord(OperationMonitorView view) throws IOException;


    /**
     * 更新tomcat监控记录
     * @param view
     * @return
     * @throws JsonProcessingException
     */
    ResultMsg updateMiddleMonitorRecord(OperationMonitorView view) throws IOException;


    /**
     * 更新mysql监控记录
     * @param view
     * @return
     * @throws JsonProcessingException
     */
    ResultMsg updateDbMonitorRecord(OperationMonitorView view) throws IOException;

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





    public NetworkMonitorEntity getNetworkMonitorEntity(String uuid);


    /**
     * uuid获取Tomcat设备监控实体
     * @param uuid
     * @return
     */
    public TomcatMonitorEntity getTomcatMonitorEntity(String uuid);

    /**
     * uuid获取Mysql设备监控实体
     * @param uuid
     * @return
     */
    public DBMonitorEntity getDbMonitorEntity(String uuid);

    /**
     * uuid获取Cas监控实体
     * @param uuid
     * @return
     */
    public CasMonitorEntity getCasMonitorEntity(String uuid);

    /**
     * uuid获取Host cvk监控实体
     * @param uuid
     * @return
     */
    public HostMonitorEntity getHostMonitorEntity(String uuid);


    /**
     * uuid获取vm监控实体
     * @param uuid
     * @return
     */
    public VmMonitorEntity getVmMonitorEntity(String uuid);



    /**
     * uuid获取k8a监控实体
     * @param uuid
     * @return
     */
    public K8sMonitorEntity getK8sMonitorEntity(String uuid);

    /**
     * uuid获取k8snode监控实体
     * @param uuid
     * @return
     */
    public K8snodeMonitorEntity getK8snodeMonitorEntity(String uuid);


    /**
     * uuid获取k8scontainer监控实体
     * @param uuid
     * @return
     */
    public K8scontainerMonitorEntity getK8sContainerMonitorEntity(String uuid);

}
