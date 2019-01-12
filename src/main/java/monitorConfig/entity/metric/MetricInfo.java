package monitorConfig.entity.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/6/8.
 */
@Getter
@Setter
public class MetricInfo {

    private String uuid;

    private String name;

    @JsonProperty("metric_type")
    private String metricType;

    @JsonProperty("metric_group")
    private String metricGroup;

    @JsonProperty("metric_collection")
    private String metricCollection;

    @JsonProperty("metric_light_type")
    private String metricLightType;

    @JsonProperty("metric_unit")
    private String metricUnit;

    @JsonProperty("metric_display_unit")
    private String metricDisplayUnit;

    @JsonProperty("metric_query_expression")
    private String metricQueryExpression;

    private String description;
//
//    @JsonProperty("collection_name")
//    private String collectionName;
//
//    @JsonProperty("type_name")
//    private String typeName;
//
//    @JsonProperty("group_name")
//    private String groupName;

    //告警级别
    private String severity;

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

}
