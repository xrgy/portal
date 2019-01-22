package topo.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/18.
 */
@Getter
@Setter
public class TopoLinkRateView {

    //link uuid
    private String uuid;

    //link status
    private String linkStatus;

    private String formNodeRate;

    private String toNodeRate;
}
