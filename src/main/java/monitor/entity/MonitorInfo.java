package monitor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class MonitorInfo {

    @JsonProperty("snmp_version")
    private String snmpVersion;

    @JsonProperty("read_community")
    private String readCommunity;

    @JsonProperty("write_community")
    private String writeCommunity;

    private String port;


}
