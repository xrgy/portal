package topo.service.impl;

import alert.service.AlertService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.CommonEnum;
import monitor.common.ResultMsg;
import monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import topo.common.TopoEnum;
import topo.dao.TopoDao;
import topo.entity.TopoBusinessLinkEntity;
import topo.entity.TopoBusinessNodeEntity;
import topo.entity.TopoNodeEntity;
import topo.service.TopoService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
@Service
public class TopoServiceImpl implements TopoService {

    private static final String RULE_ANME_START="rule_";
    private static final String AVL_RULE_NAME="_avl";
    private static final String ONE="one";
    private static final String TWO="two";
    private static final String ONE_LEVEL_PERF="_one_level_perf";
    private static final String TWO_LEVEL_PERF="_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TopoDao dao;

    @Autowired
    AlertService alertService;


    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }

    @Override
    public ResultMsg getAllWeaveTopoNode() {
        List<TopoBusinessNodeEntity> nodes = dao.getAllWeaveTopoNode();
        return getCommonResultMsg(nodes);
    }

    @Override
    public ResultMsg getAllWeaveTopoLink() {
        List<TopoBusinessLinkEntity> nodes = dao.getAllWeaveTopoLink();
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
    public ResultMsg getCustomCanvas() {
        return getCommonResultMsg(dao.getCanvasByType(TopoEnum.CanvasType.CANVAS_CUSTOM_TOPO.value()));
    }

    @Override
    public ResultMsg getAllPorts() {
        return getCommonResultMsg(dao.getAllPorts());
    }

    @Override
    public ResultMsg getCanvasAlarmInfo(String canvasId) throws JsonProcessingException {
        List<TopoNodeEntity> nodeEntityList = dao.getTopoNodeByCanvasId(canvasId);
        List<String> monitorUuids = new ArrayList<>();
        nodeEntityList.forEach(x->{
            monitorUuids.add(x.getMonitorUuid());
        });

        return getCommonResultMsg(alertService.getAlertInfoByMonitorUuids(monitorUuids));
    }

    @Override
    public boolean deleteTopoResourceBymonitoruuid(String monitorUuid) {
        return dao.deleteTopoResourceBymonitoruuid(monitorUuid);
    }

    private ResultMsg getCommonResultMsg(Object o){
        ResultMsg msg = new ResultMsg();
        if (null != o) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(o);
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }


}
