package topo.dao;


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
}
