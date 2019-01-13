package monitor.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.entity.CasTransExporterModel;
import monitor.entity.LightTypeEntity;
import monitor.entity.OperationMonitorEntity;
import monitor.entity.view.Cluster;
import monitor.entity.view.Host;
import monitor.entity.view.k8sView.Container;
import monitor.entity.view.k8sView.Node;

import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorDao {

    /**
     * 通过uuid获取监控对象
     * @param uuid
     * @return
     */
    public OperationMonitorEntity getMonitorRecordByUuid(String uuid);

    /**
     * 插入监控记录
     * @param entity
     * @return
     */
    public boolean insertMonitorRecord(OperationMonitorEntity entity) throws JsonProcessingException;

    /**
     * 获取三级规格列表
     * @return
     */
    public List<LightTypeEntity> getLightTypeEntity();

    /**
     * 获取集群列表
     * @param casTransExporterModel
     * @return
     */
    List<Cluster> getClusterListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException;

    /**
     * 插入监控记录列表
     * @param entity
     * @return
     */
    public boolean insertMonitorRecordList(List<OperationMonitorEntity> entity) throws JsonProcessingException;

    /**
     * 获取cvk和vm列表
     * @param casTransExporterModel
     * @return
     */
    List<Host> getCvkAndVmListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException;

    /**
     * 获取容器列表
     */
    List<Container> getContainerListByExporter(String ip, String port);

    /**
     * 获取Node列表
     */
    List<Node> getNodeListByExporter(String ip, String port);

    /**
     * 通过uuid删除监控记录
     * @param uuid
     * @return
     */
    boolean delMonitorRecord(String uuid);

    /**
     * 获取所有的监控记录
     * @return
     */
    List<OperationMonitorEntity> getAllMonitorRecord();

    /**
     * 在extra中查找有该uuid的监控记录(parentId或rootId)
     * @param uuid
     * @return
     */
    List<OperationMonitorEntity> getMonitorRecordByRootId(String uuid);


    /**
     * 通过templateid获取监控设备
     * @param uuid
     * @return
     */
    List<OperationMonitorEntity> getMonitorRecordByTemplateId(String uuid);

}
