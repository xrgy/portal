package topo.dao;


import topo.entity.TopoBusinessLinkEntity;
import topo.entity.TopoBusinessNodeEntity;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoDao {


    List<TopoBusinessNodeEntity> getAllWeaveTopoNode();

    List<TopoBusinessLinkEntity> getAllWeaveTopoLink();

}
