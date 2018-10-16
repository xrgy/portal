package monitor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class DBMonitorInfo {

    private String port;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("password")
    private String passWord;

    @JsonProperty("databasename")
    private String databaseName;

}
