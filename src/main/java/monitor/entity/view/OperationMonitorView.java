package monitor.entity.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class OperationMonitorView {

    private String ip;

    private String name;

    private String lightType;

    private String monitorMode;

    private String readcommunity;

    private String writecommunity;

    private String port;

    private String timeinterval;

    private String timeout;

    private String monitortemplate;
}
