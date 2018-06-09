package monitorConfig.entity.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/6/8.
 */
@Getter
@Setter
public class Metrics {

    private String uuid;

    private String name;

    @JsonProperty("metric_type_id")
    private String metricTypeId;

    @JsonProperty("metric_group_id")
    private String metricGroupId;

    @JsonProperty("metric_collection_id")
    private String metricCollectionId;

    @JsonProperty("metric_light_type_id")
    private String metricLightTypeId;

    @JsonProperty("metric_unit")
    private String metricUnit;

    @JsonProperty("metric_display_unit")
    private String metricDisplayUnit;

    @JsonProperty("metric_query_expression")
    private String metricQueryExpression;

    @JsonProperty("description")
    private String description;

}
