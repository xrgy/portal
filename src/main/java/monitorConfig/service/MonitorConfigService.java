package monitorConfig.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.entity.OperationMonitorEntity;
import monitorConfig.common.ResultMsg;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.template.RuleMonitorEntity;

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

    /**
     * 根据三级规格获取
     * @param lightType
     * @return
     */
    public ResultMsg getTemplateByLightType(String lightType,String monitorMode);

    /**
     * 根据监控记录id和模板id生成监控实体规则
     * @param operationMonitorEntity
     */
    RuleMonitorEntity addMonitorRecordAlertRule(OperationMonitorEntity operationMonitorEntity);

    /**
     * 生成添加etcd监控模板
     * @param lightTypeId
     * @param templateId
     * @param ruleMonitorEntity
     */
    void addAlertTemplateToEtcd(String lightTypeId, String templateId, RuleMonitorEntity ruleMonitorEntity) throws JsonProcessingException;
}
