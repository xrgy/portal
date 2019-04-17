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
public class CasMonitorEntity {


    private String uuid;

    private String name;

    private String ip;

    private String username;

    private String password;

    private String port;

    private String monitorType;

    private String scrapeInterval;

    private String scrapeTimeout;

    private String templateId;

    private String hostTemplateId;


    private String vmTemplateId;

    private MonitorTemplate monitorTemplate;
}
