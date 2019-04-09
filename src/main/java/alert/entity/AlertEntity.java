package alert.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertEntity {


    private String ip;

    private String uuid;

    private String alertSource;

    private String alertInfo;



    private String monitorUuid;

    private int severity;

    private String alertRuleUuid;


    private int resolvedStatus;



    private String description;

    private double currentValue;

//    @Column(name = "threshold")
//    private String threshold;

    private Date createTime;

    private Date resolvedTime;

    private String createT;

    private String resolvedT;

    private String lightType;

}
