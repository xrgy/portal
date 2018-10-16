package monitor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class CasMonitorInfo {


    private String ip;
    
    private String port;

    private String hostId;

    private String vmId;

    private String hostpoolId;

    private String clusterId;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("password")
    private String passWord;


}
