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
public class Cluster {

    private String clusterId;

    private String name;

    private String description;

    private String hostpoolId;

    @JsonProperty("host")
    private List<Host> hostList;
}
