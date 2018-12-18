package topo.entity;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class TopoBusinessLinkEntity {

    String uuid;

    String fromNodeId;

    String toNodeId;

    String canvasId;
}
