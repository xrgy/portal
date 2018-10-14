package monitor.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.entity.LightTypeEntity;
import monitor.entity.OperationMonitorEntity;

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

}
