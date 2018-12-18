package topo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.common.ResultMsg;
import topo.entity.TopoBusinessLinkEntity;
import topo.entity.TopoBusinessNodeEntity;

import java.util.List;


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

}
