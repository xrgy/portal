package monitorConfig.entity.metric;

import lombok.Getter;
import lombok.Setter;
import monitorConfig.entity.metric.MetricInfo;

import java.util.List;

/**
 * Created by gy on 2018/6/9.
 */
@Getter
@Setter
public class RecMetricInfo {

    //type: interface...
    private String type;

    //avl rule uuid


    private List<MetricInfo> data;
}
