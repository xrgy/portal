package monitorConfig.entity.template;

import lombok.Getter;
import lombok.Setter;
import monitor.entity.OperationMonitorEntity;

import java.util.List;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class RuleMonitorEntity {

    List<AlertAvlRuleMonitorEntity> avlRuleMonitorList;

    List<AlertPerfRuleMonitorEntity> perfRuleMonitorList;

    AlertRuleTemplateMonitorEntity templateMonitorEntity;
}
