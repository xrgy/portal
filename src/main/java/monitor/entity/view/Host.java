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
public class Host {

    private String uuid;

    // host name
    private String name;

    //host id
    private String id;

    //host status
    private String status;

    //host ip
    private String ip;

    private String clusterId;

    private String hostpoolId;

    private boolean beenAdd;

    @JsonProperty("vm")
    private List<VirtualMachine> virtualMachineList;
}
