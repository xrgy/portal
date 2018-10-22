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
import monitor.entity.view.k8sView.Container;
import monitor.entity.view.k8sView.Node;
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
        operationMonitorEntity.setUpdateTime(new Date());
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
        operationMonitorEntity.setUpdateTime(new Date());
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
        operationMonitorEntity.setUpdateTime(new Date());
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
    public ResultMsg addContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        ResultMsg msg = new ResultMsg();
        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        operationMonitorEntity.setTemplateId(view.getK8sTemplate());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8S.value());
        K8sMonitorInfo monitorInfo = new K8sMonitorInfo();
        monitorInfo.setMasterIp(view.getIp());
        monitorInfo.setApiPort(view.getApiPort());
        monitorInfo.setCadvisorPort(view.getCAdvisorPort());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean insertK8s = dao.insertMonitorRecord(operationMonitorEntity);
        // TODO: 2018/10/14 添加监控实体到etcd
        if (insertK8s) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(operationMonitorEntity);
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }
        List<Node> allNode = dao.getNodeListByExporter(view.getIp(), view.getApiPort());
        //node默认添加全部的
        Map<String, String> nodeMap = new HashMap<>();
        Map<String, String> k8snExtra = new HashMap<>();
        k8snExtra.put("parentId", operationMonitorEntity.getUuid());
        k8snExtra.put("rootId", operationMonitorEntity.getUuid());
        allNode.forEach(node -> {
            if (!node.isBeenAdd()) {
                try {
                    OperationMonitorEntity k8snNode = setNodeMonitorField(view, node);
                    k8snNode.setExtra(objectMapper.writeValueAsString(k8snExtra));
                    boolean insertK8sn = dao.insertMonitorRecord(k8snNode);
                    nodeMap.put(node.getNodeIp(), k8snNode.getUuid());
                    // TODO: 2018/10/14 添加监控实体到etcd
                    if (insertK8sn) {
                        //添加监控对象的告警规则
                        RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(k8snNode);
                        //生成并下发etcd中的告警模板
                        configService.addAlertTemplateToEtcd(k8snNode.getLightTypeId(), k8snNode.getTemplateId(), ruleMonitorEntity);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        List<Container> myContainerList = new ArrayList<>();
        allNode.forEach(node -> {
            node.getPods().forEach(pod -> {
                myContainerList.addAll(pod.getContainers());
            });
        });
        //添加容器
        switch (view.getRadioType()) {
            case MONITOR_1:
                //仅监控k8s
                break;
            case MONITOR_2:
                //监控k8s和所有容器
                myContainerList.forEach(container -> {
                    addContainer(view, container, operationMonitorEntity, nodeMap);
                });

                break;
            case MONITOR_3:
                //监控k8s和指定容器
                view.getContainerIds().forEach(cId -> {
                    Optional<Container> optCon = myContainerList.stream().filter(x -> (!x.isBeenAdd()) && x.getContainerId().equals(cId)).findFirst();
                    optCon.ifPresent(container -> addContainer(view, container, operationMonitorEntity, nodeMap));
                });
                break;
        }

        msg.setCode(HttpStatus.OK.value());
        msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        return msg;
    }

    @Override
    public ResultMsg delNetworkMonitorRecord(List<String> uuids) {

        List<OperationMonitorEntity> allmonitor = dao.getAllMonitorRecord();
        List<OperationMonitorEntity> delOperationMonitorList = allmonitor.stream().
                filter(monitor-> uuids.contains(monitor.getUuid())).collect(Collectors.toList());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
                .equals(MonitorEnum.LightTypeEnum.K8SCONTAINER.value())).findFirst();
        delOperationMonitorList.forEach(del->{
            Optional<LightTypeEntity> light = lightTypeEntityList.stream().filter(x->x.getUuid().equals(del.getLightTypeId())).findFirst();
            if (light.isPresent()){
                if (light.get().getName().equals(MonitorEnum.LightTypeEnum.K8S.value())|| light.get().getName().equals(MonitorEnum.LightTypeEnum.CAS.value())){
                    //k8s和cas的操作相同
                    //根据这个uuid获取所有的node和container extra uuid
                    List<OperationMonitorEntity> needdel = dao.getMonitorRecordByRootId(del.getUuid());
                    boolean delk8s = dao.delMonitorRecord(del.getUuid());
                    if (delk8s){
                        // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid
                        // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid
                        needdel.forEach(node->{
                            //删除该k8s下的node和container
                            boolean delk8snorc = dao.delMonitorRecord(node.getUuid());
                            if (delk8snorc){
                                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid
                                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid
                            }
                        });

                    }
                }else{
                    //其他设备
                    boolean delres = dao.delMonitorRecord(del.getUuid());
                    if (delres) {
                        // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid
                        // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid
                    }
                }
            }
        });
        //从monitorconfig下发，将该告警模板删除
        boolean flag = configService.delAlertRuleByUuids(uuids);

        return null;
    }

    @Override
    public ResultMsg getMonitorRecord(String uuid) {
        OperationMonitorEntity entity = dao.getMonitorRecordByUuid(uuid);
        ResultMsg msg = new ResultMsg();
        if (null!=entity){
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(entity);
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }

    @Override
    public ResultMsg updateNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity oldEntity = dao.getMonitorRecordByUuid(view.getUuid());
        OperationMonitorEntity entity = updateCommonField(view,oldEntity);
        MonitorInfo monitorInfo = new MonitorInfo();
        if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V1.value())) {
            monitorInfo.setSnmpVersion(NUMBER_1);
        } else if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V2.value())) {
            monitorInfo.setSnmpVersion(NUMBER_2);
        }
        monitorInfo.setReadCommunity(view.getReadcommunity());
        monitorInfo.setWriteCommunity(view.getWritecommunity());
        monitorInfo.setPort(view.getPort());
        entity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean flag = dao.updateMonitorRecord(entity);
        return null;
    }

    private OperationMonitorEntity updateCommonField(OperationMonitorView view,OperationMonitorEntity oldEntity){
        OperationMonitorEntity entity = new OperationMonitorEntity();
        entity.setUuid(view.getUuid());
        entity.setIp(view.getIp());
        entity.setMonitorType(oldEntity.getMonitorType());
        entity.setLightTypeId(oldEntity.getLightTypeId());
        entity.setTemplateId(view.getMonitortemplate());
        entity.setName(view.getName());
        entity.setCreateTime(oldEntity.getCreateTime());
        entity.setUpdateTime(new Date());
        entity.setScrapeInterval(view.getTimeinterval());
        entity.setScrapeTimeout(view.getTimeout());
        entity.setDeleted(0);
        return entity;
    }

    private void addContainer(OperationMonitorView view, Container container, OperationMonitorEntity operationMonitorEntity, Map<String, String> nodeMap) {
        try {
            OperationMonitorEntity monitorContainer = setContainerMonitorField(view, container);
            Map<String, String> cExtra = new HashMap<>();
            cExtra.put("rootId", operationMonitorEntity.getUuid());
            cExtra.put("parentId", nodeMap.getOrDefault(container.getNodeIp(), ""));
            monitorContainer.setExtra(objectMapper.writeValueAsString(cExtra));
            boolean insertK8sc = dao.insertMonitorRecord(monitorContainer);
            // TODO: 2018/10/14 添加监控实体到etcd
            if (insertK8sc) {
                //添加监控对象的告警规则
                RuleMonitorEntity ruleMonitorEntityC = configService.addMonitorRecordAlertRule(monitorContainer);
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(monitorContainer.getLightTypeId(), monitorContainer.getTemplateId(), ruleMonitorEntityC);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    private OperationMonitorEntity setContainerMonitorField(OperationMonitorView view, Container container) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(container.getContainerName());
        operationMonitorEntity.setIp(container.getNodeIp());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8SCONTAINER.value());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
                .equals(MonitorEnum.LightTypeEnum.K8SCONTAINER.value())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setTemplateId(view.getK8sContainerTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setUpdateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        K8scMonitorInfo monitorInfo = new K8scMonitorInfo();
        monitorInfo.setMasterIp(view.getIp());
        monitorInfo.setApiPort(view.getApiPort());
        monitorInfo.setCadvisorPort(view.getCAdvisorPort());
        monitorInfo.setNodeIp(container.getNodeIp());
        monitorInfo.setPodName(container.getPodName());
        monitorInfo.setPodNamespace(container.getPodNamespace());
        monitorInfo.setContainerId(container.getContainerId());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        return operationMonitorEntity;
    }

    private OperationMonitorEntity setNodeMonitorField(OperationMonitorView view, Node node) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
        operationMonitorEntity.setName(node.getNodeName());
        operationMonitorEntity.setIp(node.getNodeIp());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8SNODE.value());
        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
                .equals(MonitorEnum.LightTypeEnum.K8SNODE.value())).findFirst();
        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
        operationMonitorEntity.setTemplateId(view.getK8sNodeTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(new Date());
        operationMonitorEntity.setUpdateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        K8snMonitorInfo monitorInfo = new K8snMonitorInfo();
        monitorInfo.setMasterIp(view.getIp());
        monitorInfo.setApiPort(view.getApiPort());
        monitorInfo.setCadvisorPort(view.getCAdvisorPort());
        monitorInfo.setNodeIp(node.getNodeIp());
        monitorInfo.setNodeName(node.getNodeName());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        return operationMonitorEntity;
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
        operationMonitorEntity.setUpdateTime(new Date());
        operationMonitorEntity.setDeleted(0);
        return operationMonitorEntity;
    }


}
