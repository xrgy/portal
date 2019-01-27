package topo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import topo.entity.TwaverBox;


/**
 * Created by gy on 2018/3/31.
 */
public interface TopoService {

    /**
     * 获取weave节点
     * @return
     */
    public ResultMsg getAllWeaveTopoNode();

    /**
     * 获取weave链路
     * @return
     */
    public ResultMsg getAllWeaveTopoLink();

    /**
     * 获取网络拓扑的所有节点
     * @param canvasId
     * @return
     */
    ResultMsg getTopoNodeByCanvasId(String canvasId);

    /**
     * 获取网络拓扑的所有链路
     * @param canvasId
     * @return
     */
    ResultMsg getTopoLinkByCanvasId(String canvasId);

    /**
     * 获取拓扑画布
     * @return
     * @param type
     */
    ResultMsg getCustomCanvas(String type);

    /**
     *
     * @param
     * @return
     */
    ResultMsg getTopoCanvasData();

    /**
     * 获取所有端口
     * @return
     */
    ResultMsg getAllPorts();


    /**
     * 获取画布告警信息
     * @param canvasId
     * @return
     */
    ResultMsg getCanvasAlarmInfo(String canvasId) throws JsonProcessingException;



    /**
     * 删除监控记录时候需要级联删除拓扑中的设备
     * @param monitorUuid
     * @return
     */
    boolean deleteTopoResourceBymonitoruuid(String monitorUuid);

    /**
     * 获取画布链路流量
     * @param monitorUuid
     * @param linkRate
     * @return
     */
    ResultMsg getInterfaceRate(String monitorUuid, String linkRate);

    ResultMsg saveTopo(TwaverBox twaverBox) throws JsonProcessingException;

    /**
     * 获取业务节点信息
     * @param uuid
     * @return
     */
    ResultMsg getBusinessNode(String uuid);
}
