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
public class UpAvaliable {

    private String uuid;

    private String severity;

    private String description;

    @JsonProperty("quota_uuid")
    private String metricUuid;

    @JsonProperty("template_uuid")
    private String templateUuid;

    @JsonProperty("quota_info")
    MetricInfo quotaInfo;

    @JsonProperty("type_name")
    private String typeName;

    private String name;
}
