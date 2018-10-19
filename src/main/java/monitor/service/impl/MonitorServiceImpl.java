package monitor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.MonitorEnum;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.dao.MonitorDao;
import monitor.entity.*;
import monitor.entity.view.Cluster;
import monitor.entity.view.Host;
import monitor.entity.view.OperationMonitorView;
import monitor.entity.view.VirtualMachine;
import monitor.service.MonitorService;
import monitorConfig.common.CommonEnum;
import monitorConfig.entity.template.RuleMonitorEntity;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private static final String NUMBER_1 = "1";
    private static final String NUMBER_2 = "2";
    private static final String MONITOR_1 = "1";
    private static final String MONITOR_2 = "2";
    private static final String MONITOR_3 = "3";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MonitorDao dao;

    @Autowired
    MonitorConfigService configService;

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Override
    public ResultMsg addNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.SNMP.value());
        operationMonitorEntity.setTemplateId(view.getMonitortemplate());
        //monitorinfo
        MonitorInfo monitorInfo = new MonitorInfo();
        if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V1.value())) {
            monitorInfo.setSnmpVersion(NUMBER_1);
        } else if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V2.value())) {
            monitorInfo.setSnmpVersion(NUMBER_2);
        }
        monitorInfo.setReadCommunity(view.getReadcommunity());
        monitorInfo.setWriteCommunity(view.getWritecommunity());
        monitorInfo.setPort(view.getPort());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean insert = dao.insertMonitorRecord(operationMonitorEntity);
        // TODO: 2018/10/14 添加监控实体到etcd

        if (insert) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(operationMonitorEntity);
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), ruleMonitorEntity);


        }

        return ResCommon.genSimpleResByBool(insert);
    }

    @Override
    public ResultMsg addMiddleWareMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        operationMonitorEntity.setTemplateId(view.getMonitortemplate());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.TOMCAT.value());
        MiddleMonitorInfo monitorInfo = new MiddleMonitorInfo();
        monitorInfo.setPort(view.getPort());
        monitorInfo.setAuthentication(view.isAuthentication());
        if (view.isAuthentication()) {
            monitorInfo.setUserName(view.getUserName());
            monitorInfo.setPassWord(view.getPassword());
        }
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean insert = dao.insertMonitorRecord(operationMonitorEntity);
        // TODO: 2018/10/14 添加监控实体到etcd

        if (insert) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(operationMonitorEntity);
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }

        return ResCommon.genSimpleResByBool(insert);
    }

    @Override
    public ResultMsg addDataBaseMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        operationMonitorEntity.setTemplateId(view.getMonitortemplate());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.MYSQL.value());
        DBMonitorInfo monitorInfo = new DBMonitorInfo();
        monitorInfo.setDatabaseName(view.getDatabaseName());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean insert = dao.insertMonitorRecord(operationMonitorEntity);
        // TODO: 2018/10/14 添加监控实体到etcd

        if (insert) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(operationMonitorEntity);
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), ruleMonitorEntity);


        }
        return ResCommon.genSimpleResByBool(insert);
    }

    @Override
    public ResultMsg addVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        ResultMsg msg = new ResultMsg();
        //添加cas
        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        operationMonitorEntity.setTemplateId(view.getCasTemplate());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.CAS.value());
        CasMonitorInfo monitorInfo = new CasMonitorInfo();
        monitorInfo.setIp(view.getIp());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean insertCas = dao.insertMonitorRecord(operationMonitorEntity);
        // TODO: 2018/10/14 添加监控实体到etcd
        if (insertCas) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(operationMonitorEntity);
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }

        CasTransExporterModel casTransExporterModel = new CasTransExporterModel();

        casTransExporterModel.setIp(view.getIp());
        casTransExporterModel.setPort(view.getPort());
        casTransExporterModel.setUsername(view.getUserName());
        casTransExporterModel.setPassword(view.getPassword());
        List<Cluster> clusterList = dao.getClusterListByExporter(casTransExporterModel);
        List<OperationMonitorEntity> clusterMonitorList = new ArrayList<>();
        //将所有的集群插入数据库
        Map<String, String> map = new HashMap<>();
        map.put("parentId", operationMonitorEntity.getUuid());
        map.put("parentName", view.getName());
        map.put("rootId", operationMonitorEntity.getUuid());
        map.put("rootName", view.getName());
        Map<String, String> clusterIdMap = new HashMap<>();
        clusterList.forEach(cluster -> {
            try {
                OperationMonitorEntity clusterMonitor = setClusterMonitorFiled(cluster, view);
                clusterMonitor.setExtra(objectMapper.writeValueAsString(map));
                clusterIdMap.put(cluster.getClusterId(), clusterMonitor.getUuid());
                boolean insertCasCluster = dao.insertMonitorRecord(clusterMonitor);
                // TODO: 2018/10/14 添加监控实体到etcd

                if (insertCasCluster) {
                    //添加监控对象的告警规则
                    RuleMonitorEntity cluterRuleMonitorEntity = configService.addMonitorRecordAlertRule(clusterMonitor);
                    //生成并下发etcd中的告警模板
                    configService.addAlertTemplateToEtcd(clusterMonitor.getLightTypeId(), clusterMonitor.getTemplateId(), cluterRuleMonitorEntity);
                }

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
        //添加cvk
        List<Host> hostList = dao.getCvkAndVmListByExporter(casTransExporterModel);
        switch (view.getRadioType()) {
            case MONITOR_1:
                //仅监控cvk主机
                hostList.forEach(host -> {
                    //如果有集群，则parentId为集群Id，否则为casUuid
                    if (null != host.getClusterId()) {
                        addHost(host, view, operationMonitorEntity, host.getClusterId());
                    } else {
                        addHost(host, view, operationMonitorEntity, operationMonitorEntity.getUuid());
                    }
                });

                break;
            case MONITOR_2:
                //监控所有cvk和vm
                hostList.forEach(host -> {
                    //如果有集群，则parentId为集群Id，否则为casUuid
                    String insertCvkt = "";
                    if (null != host.getClusterId()) {
                        insertCvkt = addHost(host, view, operationMonitorEntity, host.getClusterId());
                    } else {
                        insertCvkt = addHost(host, view, operationMonitorEntity, operationMonitorEntity.getUuid());
                    }

                    if (!insertCvkt.equals("")) {
                        String finalInsertCvkt = insertCvkt;
                        host.getVirtualMachineList().forEach(vm -> {
                            addVm(vm, host, operationMonitorEntity, view, finalInsertCvkt);
                        });
                    }
                });
                break;
            case MONITOR_3:
                //监控指定的cvk和vm
                view.getCvkIds().forEach(transCvkId -> {
                    Optional<Host> optHost = hostList.stream().filter(x -> (!x.isBeenAdd()) && x.getId().equals(transCvkId.getCvkId())).findFirst();
                    if (optHost.isPresent()) {
                        String insertCvkt3 = "";
                        if (null != optHost.get().getClusterId()) {
                            insertCvkt3 = addHost(optHost.get(), view, operationMonitorEntity, clusterIdMap.get(optHost.get().getClusterId()));
                        } else {
                            insertCvkt3 = addHost(optHost.get(), view, operationMonitorEntity, operationMonitorEntity.getUuid());
                        }
                        if (!insertCvkt3.equals("")) {
                            List<VirtualMachine> virtualMachines = optHost.get().getVirtualMachineList().stream().
                                    filter(v -> (!v.isBeenAdd()) && transCvkId.getVmIds().contains(v.getId())).collect(Collectors.toList());

                            String finalInsertCvkt3 = insertCvkt3;
                            virtualMachines.forEach(vm -> {
                                addVm(vm, optHost.get(), operationMonitorEntity, view, finalInsertCvkt3);
                            });
                        }
                    }
                });
                break;

        }
        msg.setCode(HttpStatus.OK.value());
        msg.setMsg(CommonEnum.MSG_SUCCESS.value());


        return msg;
    }

    private boolean addVm(VirtualMachine vm, Host host, OperationMonitorEntity operationMonitorEntity, OperationMonitorView view, String parentUuid) {
        OperationMonitorEntity vmMonitor = null;
        Map<String, String> vmExtra = new HashMap<>();
        vmExtra.put("parentId", parentUuid);
        vmExtra.put("parentName", host.getName());
        vmExtra.put("rootId", operationMonitorEntity.getUuid());
        vmExtra.put("rootName", view.getName());

        try {

            if (null != host.getClusterId()) {
                vmMonitor = setVmMonitorField(vm, view, host.getHostpoolId(), host.getClusterId(), host.getId());
                vmMonitor.setExtra(objectMapper.writeValueAsString(vmExtra));
            } else {
                vmMonitor = setVmMonitorField(vm, view, host.getHostpoolId(), "", host.getId());
                vmMonitor.setExtra(objectMapper.writeValueAsString(vmExtra));
            }
            boolean insertVm = dao.insertMonitorRecord(vmMonitor);
            if (insertVm) {
                //添加监控对象的告警规则
                RuleMonitorEntity vmRuleMonitorEntity = configService.addMonitorRecordAlertRule(vmMonitor);
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(vmMonitor.getLightTypeId(), vmMonitor.getTemplateId(), vmRuleMonitorEntity);
            }
            return insertVm;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private OperationMonitorEntity setVmMonitorField(VirtualMachine vm, OperationMonitorView view, String hostpoolId, String clusterId, String hostId) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(vm.getName());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.VIRTUALMACHINE.value());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
                .equals(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setTemplateId(view.getVmTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        CasMonitorInfo monitorInfo = new CasMonitorInfo();
        monitorInfo.setIp(view.getIp());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        if (!clusterId.equals("")) {
            monitorInfo.setClusterId(clusterId);
        }
        monitorInfo.setHostId(hostId);
        monitorInfo.setHostpoolId(hostpoolId);
        monitorInfo.setVmId(vm.getId());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));

        return operationMonitorEntity;
    }

    private String addHost(Host host, OperationMonitorView view, OperationMonitorEntity operationMonitorEntity, String clusterId) {
        try {
            OperationMonitorEntity hostMonitor = setCvkMonitorFiled(host, view);
            Map<String, String> extra = new HashMap<>();
            //如果有集群，则parentId为集群Id，否则为casUuid
            extra.put("parentId", clusterId);
            extra.put("rootId", operationMonitorEntity.getUuid());
            extra.put("rootName", view.getName());
            hostMonitor.setExtra(objectMapper.writeValueAsString(extra));
            boolean insertCvk = dao.insertMonitorRecord(hostMonitor);
            // TODO: 2018/10/14 添加监控实体到etcd

            if (insertCvk) {
                //添加监控对象的告警规则
                RuleMonitorEntity hostRuleMonitorEntity = configService.addMonitorRecordAlertRule(hostMonitor);
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(hostMonitor.getLightTypeId(), hostMonitor.getTemplateId(), hostRuleMonitorEntity);
            }
            return hostMonitor.getUuid();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }

    }

    private OperationMonitorEntity setCvkMonitorFiled(Host host, OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(host.getName());
        operationMonitorEntity.setIp(host.getIp());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.CVK.value());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
                .equals(MonitorEnum.LightTypeEnum.CVK.value())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setTemplateId(view.getCvkTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        CasMonitorInfo monitorInfo = new CasMonitorInfo();
        monitorInfo.setIp(view.getIp());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        if (null != host.getClusterId()) {
            monitorInfo.setClusterId(host.getClusterId());
        }
        monitorInfo.setHostId(host.getId());
        monitorInfo.setHostpoolId(host.getHostpoolId());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));

        return operationMonitorEntity;
    }

    private OperationMonitorEntity setClusterMonitorFiled(Cluster cluster, OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(cluster.getName());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.CASCLUSTER.value());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
                .equals(MonitorEnum.LightTypeEnum.CASCLUSTER.value())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setTemplateId(view.getCasClusterTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        CasMonitorInfo monitorInfo = new CasMonitorInfo();
        monitorInfo.setIp(view.getIp());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        monitorInfo.setClusterId(cluster.getClusterId());
        monitorInfo.setHostpoolId(cluster.getHostpoolId());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        return operationMonitorEntity;
    }

    @Override
    public ResultMsg addContainerMonitorRecord(OperationMonitorView view) {
        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        return null;
    }


    private OperationMonitorEntity setCommonMonitorFiled(OperationMonitorView view) {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(view.getName());
        operationMonitorEntity.setIp(view.getIp());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName().equals(view.getLightType())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        return operationMonitorEntity;
    }


}
