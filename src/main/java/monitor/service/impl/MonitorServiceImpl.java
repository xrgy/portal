package monitor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.MonitorEnum;
import monitor.common.ResultMsg;
import monitor.dao.MonitorDao;
import monitor.entity.LightTypeEntity;
import monitor.entity.MonitorInfo;
import monitor.entity.OperationMonitorEntity;
import monitor.entity.view.OperationMonitorView;
import monitor.service.MonitorService;
import monitorConfig.common.CommonEnum;
import monitorConfig.entity.template.RuleMonitorEntity;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.management.monitor.Monitor;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private static final String NUMBER_1="1";
    private static final String NUMBER_2="2";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MonitorDao dao;

    @Autowired
    MonitorConfigService configService;

    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }

    @Override
    public ResultMsg addNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        ResultMsg msg = new ResultMsg();
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(view.getName());
        operationMonitorEntity.setIp(view.getIp());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x->x.getName().equals(view.getLightType())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorRecordEnum.SNMP.value());
        operationMonitorEntity.setTemplateId(view.getMonitortemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        //monitorinfo
        MonitorInfo monitorInfo = new MonitorInfo();
        if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V1.value())){
            monitorInfo.setSnmpVersion(NUMBER_1);
        }else if(view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V2.value())){
            monitorInfo.setSnmpVersion(NUMBER_2);
        }
        monitorInfo.setReadCommunity(view.getReadcommunity());
        monitorInfo.setWriteCommunity(view.getWritecommunity());
        monitorInfo.setPort(view.getPort());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean insert = dao.insertMonitorRecord(operationMonitorEntity);
        // TODO: 2018/10/14 添加到etcd

        if(insert){
            //添加监控对象的告警规则
           RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(operationMonitorEntity);
           //生成并下发etcd中的告警模板


        }
        if (insert) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }
}
