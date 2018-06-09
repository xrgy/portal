package monitorConfig.entity.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/6/8.
 */
@Getter
@Setter
public class ResMetricInfo {

    /**
     * 监控方式
     */
    @JsonProperty("monitor_mode")
    String monitorMode;

    /**
     * 可用性指标数据
     */
    List<MetricInfo> available;

    /**
     * 性能指标数据
     */
    List<MetricInfo> performance;
}
