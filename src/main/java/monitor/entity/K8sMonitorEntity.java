package monitor.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import monitorConfig.entity.template.MonitorTemplate;


/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class K8sMonitorEntity {

    private String uuid;

    private String name;

    private String ip;

    private String apiPort;

    private String cadvisorPort;

    private String monitorType;

    private String scrapeInterval;

    private String scrapeTimeout;

    private String templateId;


    private String k8sNTemplateId;


    private String K8scTemplateId;

    private MonitorTemplate monitorTemplate;
}
