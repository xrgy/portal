package monitor.entity.view.k8sView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/20.
 */
@Getter
@Setter
public class Container {

    @JsonProperty("id")
    private String containerId;

    @JsonProperty("name")
    private String containerName;

    @JsonProperty("status")
    private String containerStatus;

    private String podName;

    private String podNamespace;

    private String nodeIp;

    private String nodeName;

    private boolean beenAdd;

}
