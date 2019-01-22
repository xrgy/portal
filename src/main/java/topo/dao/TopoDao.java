package topo.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import topo.entity.*;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoDao {


    List<TopoBusinessNodeEntity> getAllWeaveTopoNode();

    List<TopoBusinessLinkEntity> getAllWeaveTopoLink();

    List<TopoNodeEntity> getTopoNodeByCanvasId(String canvasId);

    List<TopoLinkEntity> getTopoLinkByCanvasId(String canvasId);

    List<TopoCanvasEntity> getCanvasByType(String name);

    List<TopoPortEntity> getAllPorts();

    boolean deleteTopoResourceBymonitoruuid(String monitorUuid);


    /**
     * 获取设备端口流量
     * @param monitorUuid
     * @param quotaName
     * @return
     */
    List<TopoLinkRateView> getInterfaceRate(String monitorUuid, String quotaName);

    /**
     * 保存节点列表
     * @param nodes
     * @return
     */
    boolean insertTopoNodeList(List<TopoNodeEntity> nodes) throws JsonProcessingException;
}
