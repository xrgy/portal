package monitor.entity.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/10/19.
 */
@Getter
@Setter
public class HostPool {

    private String id;

    private String hostpoolId;

    private String hostpoolName;

    private String name;

    @JsonProperty("cluster")
    private List<Cluster> clusterList;

    @JsonProperty("host")
    private List<Host> hostList;
}
