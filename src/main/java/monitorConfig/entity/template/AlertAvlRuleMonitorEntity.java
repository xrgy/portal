package monitorConfig.entity.template;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertAvlRuleMonitorEntity {

    private String uuid;

    private String monitorUuid;

    private String avlRuleUuid;

    private String alertRuleName;

}
