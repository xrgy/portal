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

    @JsonProperty("template_name")
    private String templateName;

//    private String monitorMode;

    @JsonProperty("light_type")
    private String lightType;


    /**
     * 使用的个数
     */
    private int usedCount;

}
