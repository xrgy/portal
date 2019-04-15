package topo.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import topo.entity.*;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoDao {


    TopoBusinessNodeEntity getBusinessTopoNodeByUuid(String uuid);

    TopoCanvasEntity getCanvasByUuid(String uuid);

    List<TopoBusinessNodeEntity> getAllWeaveTopoNode(String canvasId);

    List<TopoBusinessLinkEntity> getAllWeaveTopoLink(String canvasId);

    List<TopoNodeEntity> getTopoNodeByCanvasId(String canvasId);

    List<TopoLinkEntity> getTopoLinkByCanvasId(String canvasId);

    List<TopoCanvasEntity> getCanvasByType(String name);

    List<TopoPortEntity> getAllPorts();

    boolean deleteTopoResourceBymonitoruuid(String monitorUuid);

    boolean deleteTopoLinkByUuid(String uuid);
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

    boolean insertBusinessTopoNodeList(List<TopoBusinessNodeEntity> newNodeList) throws JsonProcessingException;

    /**
     * 删除业务的节点和链路
     * @param businessId
     * @return
     */
    boolean delTopoResourceByBusinessId(String businessId);

    boolean deleteBusTopoNodeByUuid(String uuid);

    boolean deleteBusTopoLinkByUuid(String uuid);
}
