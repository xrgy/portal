package topo.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2019/1/29.
 */
@Getter
@Setter
public class BusinessTopoData {

    TopoCanvasEntity canvas;

    List<TopoBusinessNodeEntity> nodes;

    List<TopoBusinessLinkEntity> links;
}
