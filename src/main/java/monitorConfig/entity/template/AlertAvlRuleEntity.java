package monitorConfig.entity.template;


import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertAvlRuleEntity {

    private String uuid;

    private String metricUuid;

    private String templateUuid;

    private int severity;

    private String description;

}
