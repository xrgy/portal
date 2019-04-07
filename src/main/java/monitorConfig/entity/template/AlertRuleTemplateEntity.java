package monitorConfig.entity.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertRuleTemplateEntity {

    private String uuid;

    @JsonProperty("light_type")
    private String lightType;

    @JsonProperty("template_name")
    private String templateName;

    @JsonProperty("monitor_mode")
    private String monitorMode;

    @JsonProperty("template_type")
    private int templateType;

    @JsonProperty("create_time")
    private Date createTime;

}
