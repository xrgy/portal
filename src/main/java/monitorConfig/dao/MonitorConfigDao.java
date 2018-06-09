package monitorConfig.dao;

import monitorConfig.entity.metric.MetricsCollection;
import monitorConfig.entity.metric.MetricsGroup;
import monitorConfig.entity.metric.MetricsType;
import monitorConfig.entity.metric.ResMetricInfo;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorConfigDao {
    public String getJPAInfo();

    public ResMetricInfo getMetricInfo(String lightType, String monitorMode);

}
