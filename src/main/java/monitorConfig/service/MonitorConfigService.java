package monitorConfig.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import monitor.entity.DelMonitorRecordView;
import monitor.entity.OperationMonitorEntity;
import monitor.common.ResultMsg;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.metric.UpTemplateView;
import monitorConfig.entity.template.RuleMonitorEntity;

import java.io.IOException;
import java.util.List;

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
     * @param monitorUuid
     * @param templateId
     */
    RuleMonitorEntity addMonitorRecordAlertRule(String monitorUuid,String templateId);

    /**
     * 生成添加etcd监控模板
     * @param lightTypeId
     * @param templateId
     * @param ruleMonitorEntity
     */
    void addAlertTemplateToEtcd(String lightTypeId, String templateId, RuleMonitorEntity ruleMonitorEntity) throws JsonProcessingException;

    /**
     * 删除告警规则监控模板
     * @param uuids
     * @return
     */
    boolean delAlertRuleByUuids(List<String> uuids);

    /**
     * 更新告警规则监控模板
     * @param uuid
     * @param templateId
     * @return
     */
    RuleMonitorEntity updateMonitorRecordAlertRule(String uuid, String templateId,String oldTemplateId);

    /**
     * 删除监控模板
     * @param templateUuids
     * @return
     */
    ResultMsg delTemplate(List<String> templateUuids);

    /**
     * 修改监控模板
     * @param view
     * @return
     */
    ResultMsg updateTemplate(UpTemplateView view);

    /**
     * 修改打开某个监控模板(模板id)，返回
     * @param uuid
     * @return
     */
    ResultMsg OpenTemplate(String uuid) throws IOException;

    /**
     * 获取监控模板列表，这里面要绑定每个模板被使用的监控记录数量，前端直接如果大于0，则删除按钮是灰色的
     * @return
     */
    ResultMsg getAllTemplate();
}
