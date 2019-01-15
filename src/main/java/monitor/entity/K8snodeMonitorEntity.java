package monitor.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class K8snodeMonitorEntity {

    private String uuid;

    private String name;

    private String ip;

    private String k8sUuid;

    private String monitorType;

    private String scrapeInterval;

    private String scrapeTimeout;

    private String templateId;

}
