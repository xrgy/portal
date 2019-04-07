package monitor.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class OperationMonitorEntity {


    private String uuid;

    private String name;

    private String ip;

//    private String highTypeId;

//    private String middleTypeId;

    private String lightTypeId;

    private String monitorType;

    private String monitorInfo;

    private String templateId;
    private String templateName;

    private String scrapeInterval;

    private String scrapeTimeout;

    private Date createTime;

    private Date updateTime;

    private String extra;

    private int deleted;

    //资源状态，正常为1，其他不正常
    private String status;

    private String middleType;

}
