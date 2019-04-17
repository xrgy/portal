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

    private String lightType;

    private String templateName;

    private String monitorMode;

    private int templateType;

    private Date createTime;

}
