package monitor.entity.view.k8sView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/10/20.
 */
@Getter
@Setter
public class Node {

    private String uuid;

    @JsonProperty("ip")
    private String nodeIp;

    @JsonProperty("name")
    private String nodeName;

    private List<Pod> pods;

    private boolean beenAdd;
}
