package monitorConfig.entity.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/13.
 */
@Getter
@Setter
public class RuleTemplate {

    private String uuid;

//    @JsonProperty("resourceUuid")
//    private String lightTypeId;

    private String templateName;

//    private String monitorMode;

    private String lightType;
}
