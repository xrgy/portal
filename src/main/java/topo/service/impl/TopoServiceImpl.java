package topo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.CommonEnum;
import monitor.common.ResultMsg;
import monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import topo.dao.TopoDao;
import topo.entity.TopoBusinessLinkEntity;
import topo.entity.TopoBusinessNodeEntity;
import topo.service.TopoService;

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
