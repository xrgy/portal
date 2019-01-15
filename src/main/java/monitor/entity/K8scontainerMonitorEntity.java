package monitor.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class K8scontainerMonitorEntity {


    private String uuid;

    private String name;

    private String pod_name;

    private String pod_namespace;

    private String container_id;

    private String k8snodeUuid;
    
    private String monitorType;

    private String scrapeInterval;

    private String scrapeTimeout;

    private String templateId;

}
