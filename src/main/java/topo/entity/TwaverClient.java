package topo.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/22.
 */
@Getter
@Setter
public class TwaverClient {

    private String uuid;

    private String monitorUuid;

    private String canvasId;

    private String fromNodeId;

    private String fromPortId;

    private String toNodeId;

    private String toPortId;


}
