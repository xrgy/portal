package monitor.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.entity.CasTransExporterModel;
import monitor.entity.LightTypeEntity;
import monitor.entity.OperationMonitorEntity;
import monitor.entity.view.Cluster;
import monitor.entity.view.Host;

import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorDao {

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
}
