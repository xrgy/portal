package monitor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class MiddleMonitorInfo {
    
    private String port;


    //是否开启登录认证 “true/false”
    private boolean authentication;

    private boolean ssl;

    private String tomcatManagerUri;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("password")
    private String passWord;


}
