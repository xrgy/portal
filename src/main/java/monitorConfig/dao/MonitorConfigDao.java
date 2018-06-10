package monitorConfig.dao;

import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.metric.ResMetricInfo;

/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorConfigDao {
    public String getJPAInfo();

    public ResMetricInfo getMetricInfo(String lightType, String monitorMode);

    /**
     * 判断模板名字是否被使用
     * @param name
     * @return
     */
    public boolean isTemplateNameDup(String name);


    /**
     * 新建模板
     * @param view
     * @return
     */
    public boolean addTemplate(NewTemplateView view);
}
