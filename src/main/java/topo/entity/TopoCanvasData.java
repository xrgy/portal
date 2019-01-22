package topo.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2019/1/22.
 */
@Getter
@Setter
public class TopoCanvasData {

    private TopoCanvasEntity canvas;

    private List<TopoNodeEntity> nodes;

    private List<TopoLinkEntity> links;

    private List<TopoPortEntity> ports;
}
