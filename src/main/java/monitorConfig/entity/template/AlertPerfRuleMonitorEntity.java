package monitorConfig.entity.template;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertPerfRuleMonitorEntity {

    private String uuid;

    private String monitorUuid;

    private String perfRuleUuid;

    private String alertRuleName;

}
