package monitorConfig.service;

import monitorConfig.common.ResultMsg;
import monitorConfig.entity.metric.NewTemplateView;

/**
 * Created by gy on 2018/3/31.
 */
public interface MonitorConfigService {
    public String getJPA();

    /**
     * 获取指标信息
     * @param lightType
     * @param monitorMode
     * @return
     */
    public ResultMsg getMetricInfo(String lightType,String monitorMode);

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
    public ResultMsg addTemplate(NewTemplateView view);
}
