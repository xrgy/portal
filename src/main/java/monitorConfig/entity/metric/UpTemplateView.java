package monitorConfig.entity.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/6/9.
 */
@Getter
@Setter
public class UpTemplateView {

    private String uuid;


    /**
     * 使用的个数
     */
    private int usedCount;



    /**
     * 模板名称
     */
    @JsonProperty("template_name")
    private String templateName;

    /**
     * 监控方式
     */
    @JsonProperty("monitor_mode")
    private String monitorMode;

    /**
     * 0内置,1自定义
     */
    @JsonProperty("template_type")
    private int templateType;

    /**
     * 资源三级规格id
     */
    @JsonProperty("resource_uuid")
    private String resourceUuid;

    /**
     * 可用性数据
     */
    private List<UpAvaliable> available;

    /**
     * 性能数据
     */
    private List<UpPerformance> performance;
}
