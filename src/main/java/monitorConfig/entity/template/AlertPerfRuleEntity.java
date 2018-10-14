package monitorConfig.entity.template;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertPerfRuleEntity {

    private String uuid;

    private String metricUuid;

    private String templateUuid;

    private int severity;

    private int alertFirstCondition;

    private String firstThreshold;

    private String expressionMore;

    private int alertSecondCondition;

    private String secondThreshold;

    private String description;

    private String alertLevel;

}
