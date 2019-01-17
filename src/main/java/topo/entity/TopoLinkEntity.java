package topo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class TopoLinkEntity {

    String uuid;

    String canvasId;

    String fromPortId;

    String toPortId;
}
