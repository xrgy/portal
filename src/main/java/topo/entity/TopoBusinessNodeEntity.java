package topo.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class TopoBusinessNodeEntity {

    private String uuid;

    private String canvasId;

    private String nodeName;

    private int xPoint;

    private int yPoint;

    private String extra;


}
