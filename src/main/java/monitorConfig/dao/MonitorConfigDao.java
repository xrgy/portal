package monitorConfig.dao;

import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.metric.ResMetricInfo;
import monitorConfig.entity.template.*;

import java.util.List;

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


    /**
     * 根据三级规格获取模板
     * @param lightType
     * @param monitorMode
     * @return
     */
    public List<RuleTemplate> getTemplateByLightType(String lightType, String monitorMode);

    /**
     * 根据模板id获取可用性规则列表
     * @param templateId
     * @return
     */
    List<AlertAvlRuleEntity> getAvlRuleByTemplateId(String templateId);

    /**
     * 根据模板id获取性能规则列表
     * @param templateId
     * @return
     */
    List<AlertPerfRuleEntity> getPerfRuleByTemplate(String templateId);

    /**
     * 持久化可用性监控实体列表到数据库
     * @param avlRuleMonitorList
     * @return
     */
    boolean addAvlRuleMonitorList(List<AlertAvlRuleMonitorEntity> avlRuleMonitorList);

    /**
     * 持久化性能监控实体列表到数据库
     * @param perfRuleMonitorList
     * @return
     */
    boolean addPerfRuleMonitorList(List<AlertPerfRuleMonitorEntity> perfRuleMonitorList);

    /**
     * 持久化模板监控实体到数据库
     * @param templateMonitorEntity
     * @return
     */
    boolean addTemplateMonitorEntity(AlertRuleTemplateMonitorEntity templateMonitorEntity);
}
