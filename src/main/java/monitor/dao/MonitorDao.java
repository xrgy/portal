package monitor.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.entity.*;
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

//    /**
//     * 插入监控记录
//     * @param entity
//     * @return
//     */
//    public boolean insertMonitorRecord(OperationMonitorEntity entity) throws JsonProcessingException;


    /**
     * 插入监控记录
     * @param data
     * @param lightType
     * @return
     */
    boolean insertMonitorRecord(Object data, String lightType) throws JsonProcessingException;



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
//    List<Cluster> getClusterListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException;

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



    public List<NetworkMonitorEntity> getAllNetworkMonitorEntity();



    /**
     * 获取所有的tomcat
     * @return
     */
    public List<TomcatMonitorEntity> getAllTomcatMonitorEntity();


    /**
     * 获取所有的mysql
     * @return
     */
    public List<DBMonitorEntity> getAllDbMonitorEntity();


    /**
     * 获取所有的cas
     * @return
     */
    public List<CasMonitorEntity> getAllCasMonitorEntity();


    /**
     * 获取所有的cvk
     * @return
     */
    public List<HostMonitorEntity> getAllHostMonitorEntity();


    /**
     * 获取所有的vm
     * @return
     */
    public List<VmMonitorEntity> getAllVmMonitorEntity();


    /**
     * 获取所有的k8s
     * @return
     */
    public List<K8sMonitorEntity> getAllK8sMonitorEntity();



    /**
     * 获取所有的k8snode
     * @return
     */
    public List<K8snodeMonitorEntity> getAllK8snodeMonitorEntity();



    /**
     * 获取所有的k8scontainer
     * @return
     */

    public List<K8scontainerMonitorEntity> getAllK8sContainerMonitorEntity();

}
