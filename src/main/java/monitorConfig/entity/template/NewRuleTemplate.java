package monitorConfig.entity.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/4/7.
 */
@Getter
@Setter
public class NewRuleTemplate {


    private String uuid;


    private String templateName;


    private String lightType;


    /**
     * 使用的个数
     */
    private int usedCount;


}
