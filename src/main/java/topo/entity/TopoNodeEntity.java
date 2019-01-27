package topo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class TopoNodeEntity {

    String uuid;

    String canvasId;

    String ip;

    String nodeName;

    String monitorUuid;

    int xPoint;

    int yPoint;

    String nodeType;

    String extra;


}
