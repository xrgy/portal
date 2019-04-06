package monitorConfig.entity.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/10/24.
 */
@Getter
@Setter
public class UpPerformance {

    private String levelOneUuid;


    private String description;

    @JsonProperty("quota_uuid")
    private String metricUuid;

    @JsonProperty("template_uuid")
    private String templateUuid;

    @JsonProperty("level_one_severity")
    private String levelOneSeverity;

    @JsonProperty("level_one_alert_first_condition")
    private String levelOneAlertFirstCondition;

    @JsonProperty("level_one_first_threshold")
    private String levelOneFirstThreshold;

    @JsonProperty("level_one_expression_more")
    private String levelOneExpressionMore;

    @JsonProperty("level_one_alert_second_condition")
    private String levelOneAlertSecondCondition;

    @JsonProperty("level_one_second_threshold")
    private String levelOneSecondThreshold;

    private String levelTwoUuid;


    @JsonProperty("level_two_severity")
    private String levelTwoSeverity;

    @JsonProperty("level_two_alert_first_condition")
    private String levelTwoAlertFirstCondition;

    @JsonProperty("level_two_first_threshold")
    private String levelTwoFirstThreshold;

    @JsonProperty("level_two_expression_more")
    private String levelTwoExpressionMore;

    @JsonProperty("level_two_alert_second_condition")
    private String levelTwoAlertSecondCondition;

    @JsonProperty("level_two_second_threshold")
    private String levelTwoSecondThreshold;

    @JsonProperty("quota_info")
    MetricInfo quotaInfo;

    @JsonProperty("type_name")
    private String typeName;

    private String name;
}
