package topo.service.impl;

import alert.service.AlertService;
import business.entity.BusinessEntity;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.CommonEnum;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.entity.NetworkMonitorEntity;
import monitor.service.MonitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import topo.common.TopoEnum;
import topo.dao.TopoDao;
import topo.entity.*;
import topo.service.TopoService;

import java.util.*;


/**
 * Created by gy on 2018/3/31.
 */
@Service
public class TopoServiceImpl implements TopoService {

    private static final String RULE_ANME_START = "rule_";
    private static final String AVL_RULE_NAME = "_avl";
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String ONE_LEVEL_PERF = "_one_level_perf";
    private static final String TWO_LEVEL_PERF = "_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TopoDao dao;

    @Autowired
    AlertService alertService;


    @Autowired
    MonitorService monitorService;

    @Autowired
    BusinessService businessService;

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Override
    public ResultMsg getAllWeaveTopoData(String relUuid) {

        BusinessTopoData topoData = new BusinessTopoData();
        TopoBusinessNodeEntity businessNode = dao.getBusinessTopoNodeByUuid(relUuid);
        if (null!=businessNode){
            TopoCanvasEntity canvas = dao.getCanvasByUuid(businessNode.getCanvasId());
            topoData.setCanvas(canvas);
            List<TopoBusinessNodeEntity> nodes = dao.getAllWeaveTopoNode(canvas.getUuid());
            topoData.setNodes(nodes);
            List<TopoBusinessLinkEntity> links = dao.getAllWeaveTopoLink(canvas.getUuid());
            topoData.setLinks(links);
        }


        return getCommonResultMsg(topoData);
    }

    @Override
    public ResultMsg getAllWeaveTopoLink(String relUuid) {
        List<TopoBusinessLinkEntity> nodes = dao.getAllWeaveTopoLink(relUuid);
        return getCommonResultMsg(nodes);
    }

    @Override
    public ResultMsg getTopoNodeByCanvasId(String canvasId) {
        return getCommonResultMsg(dao.getTopoNodeByCanvasId(canvasId));
    }

    @Override
    public ResultMsg getTopoLinkByCanvasId(String canvasId) {
        return getCommonResultMsg(dao.getTopoLinkByCanvasId(canvasId));
    }

    @Override
    public ResultMsg getTopoCanvasData() {
        List<TopoCanvasEntity> canvasList = dao.getCanvasByType(TopoEnum.CanvasType.CANVAS_WHOLE_TOPO.value());
        if (canvasList.size() == 1) {
            TopoCanvasEntity canvas = canvasList.get(0);
            List<NetworkMonitorEntity> monitorList = monitorService.getAllNetworkMonitorEntity();
            List<TopoNodeEntity> nodes = dao.getTopoNodeByCanvasId(canvas.getUuid());
            nodes.forEach(x->{
                Optional<NetworkMonitorEntity> monitor = monitorList.stream().filter(y->y.getUuid().equals(x.getUuid())).findFirst();
                monitor.ifPresent(networkMonitorEntity -> x.setIp(networkMonitorEntity.getIp()));
                x.setMonitorUuid(x.getUuid());
            });
            List<TopoLinkEntity> links =dao.getTopoLinkByCanvasId(canvas.getUuid());
            List<TopoPortEntity> ports = dao.getAllPorts();
            TopoCanvasData data = new TopoCanvasData();
            data.setCanvas(canvas);
            data.setNodes(nodes);
            data.setLinks(links);
            data.setPorts(ports);
            return ResCommon.getCommonResultMsg(data);
        } else {
            return null;
        }
    }

    @Override
    public ResultMsg getCustomCanvas(String type) {
        if (type.equals("whole")) {
            List<TopoCanvasEntity> canvas = dao.getCanvasByType(TopoEnum.CanvasType.CANVAS_WHOLE_TOPO.value());
            if (canvas.size() == 1) {
                return getCommonResultMsg(canvas.get(0));
            } else {
                return null;
            }
        } else {
            return getCommonResultMsg(dao.getCanvasByType(TopoEnum.CanvasType.CANVAS_CUSTOM_TOPO.value()));
        }
    }

    @Override
    public ResultMsg getAllPorts() {
        return getCommonResultMsg(dao.getAllPorts());
    }

    @Override
    public ResultMsg getCanvasAlarmInfo(String canvasId) throws JsonProcessingException {
        List<TopoNodeEntity> nodeEntityList = dao.getTopoNodeByCanvasId(canvasId);
        List<String> monitorUuids = new ArrayList<>();
        nodeEntityList.forEach(x -> {
            monitorUuids.add(x.getMonitorUuid());
        });

        return getCommonResultMsg(alertService.getAlertInfoByMonitorUuids(monitorUuids));
    }

    @Override
    public boolean deleteTopoResourceBymonitoruuid(String monitorUuid) {
        return dao.deleteTopoResourceBymonitoruuid(monitorUuid);
    }

    @Override
    public ResultMsg getInterfaceRate(String monitorUuid, String linkRate) {
        return ResCommon.getCommonResultMsg(dao.getInterfaceRate(monitorUuid, linkRate));
    }

    @Override
    public ResultMsg saveTopo(TwaverBox twaverBox) throws JsonProcessingException {
        TwaverDatas[] datas = twaverBox.getDatas();
        if (null!=datas && datas.length>0){
            List<TopoNodeEntity> oldNodeList  = dao.getTopoNodeByCanvasId(twaverBox.getDataBox().getTwaverClient().getUuid());
            List<TopoNodeEntity> newNodeList = new ArrayList<>();
            for (TwaverDatas data : datas) {
                if (data.getClassName().equals("twaver.Node")){
                    TwaverClient client = data.getTwaverClient();
                    Map<String,Integer> location = data.getTwaverPosition().getLocation();
                    Optional<TopoNodeEntity> oldNode = oldNodeList.stream().filter(x->x.getUuid().equals(client.getUuid())).findFirst();
                    if (oldNode.isPresent()){
                        TopoNodeEntity node = new TopoNodeEntity();
                        BeanUtils.copyProperties(oldNode.get(),node);
                        node.setXPoint(location.get("x"));
                        node.setYPoint(location.get("y"));
                        newNodeList.add(node);
                    }
                }else if (data.getClassName().equals("twaver.Link")){
                    //不需要做什么处理
                }
            }
            dao.insertTopoNodeList(newNodeList);
        }
        return ResCommon.genSimpleResByBool(true);
    }

    @Override
    public ResultMsg getBusinessNode(String uuid) {
        BusinessEntity node =businessService.getBusinessNode(uuid);
        return ResCommon.getCommonResultMsg(node);
    }

    @Override
    public boolean deleteTopoLinkByUuid(String uuid) {
        return dao.deleteTopoLinkByUuid(uuid);
    }

    @Override
    public ResultMsg saveBusinessTopo(TwaverBox twaverBox) throws JsonProcessingException {
        TwaverDatas[] datas = twaverBox.getDatas();
        if (null!=datas && datas.length>0){
            List<TopoBusinessNodeEntity> oldNodeList  = dao.getAllWeaveTopoNode(twaverBox.getDataBox().getTwaverClient().getUuid());
            List<TopoBusinessNodeEntity> newNodeList = new ArrayList<>();
            for (TwaverDatas data : datas) {
                if (data.getClassName().equals("twaver.Node")){
                    TwaverClient client = data.getTwaverClient();
                    Map<String,Integer> location = data.getTwaverPosition().getLocation();
                    Optional<TopoBusinessNodeEntity> oldNode = oldNodeList.stream().filter(x->x.getUuid().equals(client.getUuid())).findFirst();
                    if (oldNode.isPresent()){
                        TopoBusinessNodeEntity node = new TopoBusinessNodeEntity();
                        BeanUtils.copyProperties(oldNode.get(),node);
                        node.setXPoint(location.get("x"));
                        node.setYPoint(location.get("y"));
                        newNodeList.add(node);
                    }
                }else if (data.getClassName().equals("twaver.Link")){
                    //不需要做什么处理
                }
            }
            dao.insertBusinessTopoNodeList(newNodeList);
        }
        return ResCommon.genSimpleResByBool(true);
    }

    @Override
    public boolean delTopoResourceByBusinessId(String businessId) {

        return dao.delTopoResourceByBusinessId(businessId);
    }

    @Override
    public boolean deleteBusTopoNodeOrLink(String canvasId, String uuid, String type) {
        if (type.equals("twaver.Node")){
            //删除节点 也是只从拓扑数据库中删除节点  和删除业务没有关系
            return dao.deleteBusTopoNodeByUuid(uuid);
        }else if (type.equals("twaver.Link")){
            //删除链路 只从拓扑数据库中删除链路
            return dao.deleteBusTopoLinkByUuid(uuid);
        }
        return true;
    }

    @Override
    public boolean deleteNetTopoNodeOrLink(String canvasId, String uuid, String type) {
        if (type.equals("twaver.Node")){
            //删除节点 也是只从拓扑数据库中删除节点 端口 链路
            return dao.deleteTopoResourceBymonitoruuid(uuid);
        }else if (type.equals("twaver.Link")){
            //删除链路 只从拓扑数据库中删除链路 删除端口
            return dao.deleteTopoLinkByUuid(uuid);
        }
        return true;
    }

    private ResultMsg getCommonResultMsg(Object o) {
        ResultMsg msg = new ResultMsg();
        if (null != o) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(o);
        } else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }


}
