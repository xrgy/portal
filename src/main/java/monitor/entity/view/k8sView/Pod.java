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
public class Pod {

    @JsonProperty("name")
    private String podName;

    @JsonProperty("namespace")
    private String podNamespace;

    private List<Container> containers;

    private String nodeIp;

    private String nodeName;
}
