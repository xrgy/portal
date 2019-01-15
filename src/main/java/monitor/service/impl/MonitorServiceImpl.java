package monitor.service.impl;

import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.MonitorEnum;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitor.dao.MonitorDao;
import monitor.entity.*;
import monitor.entity.view.*;
import monitor.entity.view.k8sView.Container;
import monitor.entity.view.k8sView.Node;
import monitor.service.MonitorService;
import monitor.common.CommonEnum;
import monitorConfig.entity.metric.UpTemplateView;
import monitorConfig.entity.template.RuleMonitorEntity;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.nio.ch.Net;

import java.awt.event.MouseListener;
import java.io.IOException;
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

    @Autowired
    BusinessService businessService;

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Override
    public ResultMsg addNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
//        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        NetworkMonitorEntity networkMonitorEntity = setNetworkMonitorFiled(view);
        boolean insert = dao.insertMonitorRecord(networkMonitorEntity, view.getLightType());
        if (insert) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(networkMonitorEntity.getUuid(), networkMonitorEntity.getTemplateId());
            //生成并下发etcd中的告警模板
            //lightype
            configService.addAlertTemplateToEtcd(networkMonitorEntity.getLightType(), networkMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }

        return ResCommon.genSimpleResByBool(insert);
    }

    private NetworkMonitorEntity setNetworkMonitorFiled(OperationMonitorView view) {
        NetworkMonitorEntity networkMonitorEntity = new NetworkMonitorEntity();
        networkMonitorEntity.setUuid(UUID.randomUUID().toString());
        networkMonitorEntity.setName(view.getName());
        networkMonitorEntity.setIp(view.getIp());
        networkMonitorEntity.setLightType(view.getLightType());
        networkMonitorEntity.setScrapeInterval(view.getTimeinterval());
        networkMonitorEntity.setScrapeTimeout(view.getTimeout());
        networkMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.SNMP.value());
        networkMonitorEntity.setTemplateId(view.getMonitortemplate());
        if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V1.value())) {
            networkMonitorEntity.setSnmpVersion(NUMBER_1);
        } else if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V2.value())) {
            networkMonitorEntity.setSnmpVersion(NUMBER_2);
        }
        networkMonitorEntity.setReadCommunity(view.getReadcommunity());
        networkMonitorEntity.setWriteCommunity(view.getWritecommunity());
        networkMonitorEntity.setPort(view.getPort());
        return networkMonitorEntity;
    }

    @Override
    public ResultMsg addMiddleWareMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
//        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        TomcatMonitorEntity tomcatMonitorEntity = setMiddleMonitorFiled(view);
        boolean insert = dao.insertMonitorRecord(tomcatMonitorEntity, view.getLightType());
        if (insert) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(tomcatMonitorEntity.getUuid(), tomcatMonitorEntity.getTemplateId());
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(view.getLightType(), tomcatMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }

        return ResCommon.genSimpleResByBool(insert);
    }

    private TomcatMonitorEntity setMiddleMonitorFiled(OperationMonitorView view) {
        TomcatMonitorEntity tomcatMonitorEntity = new TomcatMonitorEntity();
        tomcatMonitorEntity.setUuid(UUID.randomUUID().toString());
        tomcatMonitorEntity.setName(view.getName());
        tomcatMonitorEntity.setIp(view.getIp());
        tomcatMonitorEntity.setScrapeInterval(view.getTimeinterval());
        tomcatMonitorEntity.setScrapeTimeout(view.getTimeout());
        tomcatMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.TOMCAT.value());
        tomcatMonitorEntity.setTemplateId(view.getMonitortemplate());
        tomcatMonitorEntity.setPort(view.getPort());
        if (view.isAuthentication()) {
            tomcatMonitorEntity.setAuthentication(1);
            tomcatMonitorEntity.setUsername(view.getUserName());
            tomcatMonitorEntity.setPort(view.getPassword());
        } else {
            tomcatMonitorEntity.setAuthentication(0);
        }
        return tomcatMonitorEntity;
    }

    @Override
    public ResultMsg addDataBaseMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
//        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        DBMonitorEntity dbMonitorEntity = setDbMonitorFiled(view);
        boolean insert = dao.insertMonitorRecord(dbMonitorEntity, view.getLightType());
        if (insert) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(dbMonitorEntity.getUuid(), dbMonitorEntity.getTemplateId());
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(view.getLightType(), dbMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }
        return ResCommon.genSimpleResByBool(insert);
    }

    private DBMonitorEntity setDbMonitorFiled(OperationMonitorView view) {
        DBMonitorEntity dbMonitorEntity = new DBMonitorEntity();
        dbMonitorEntity.setUuid(UUID.randomUUID().toString());
        dbMonitorEntity.setName(view.getName());
        dbMonitorEntity.setIp(view.getIp());
        dbMonitorEntity.setScrapeInterval(view.getTimeinterval());
        dbMonitorEntity.setScrapeTimeout(view.getTimeout());
        dbMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.MYSQL.value());
        dbMonitorEntity.setTemplateId(view.getMonitortemplate());
        dbMonitorEntity.setDatabasename(view.getDatabaseName());
        dbMonitorEntity.setPort(view.getPort());
        dbMonitorEntity.setUsername(view.getUserName());
        dbMonitorEntity.setPassword(view.getPassword());
        return dbMonitorEntity;
    }

    @Override
    public ResultMsg addVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        ResultMsg msg = new ResultMsg();
        //添加cas
//        OperationMonitorEntity operationMonitorEntity = setCommonMonitorFiled(view);
        CasMonitorEntity casMonitorEntity = setCasMonitorFiled(view);
        String casuuid = casMonitorEntity.getUuid();
        boolean insertCas = dao.insertMonitorRecord(casMonitorEntity, view.getLightType());
        if (insertCas) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(casMonitorEntity.getUuid(), casMonitorEntity.getTemplateId());
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(view.getLightType(), casMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }

        CasTransExporterModel casTransExporterModel = new CasTransExporterModel();

        casTransExporterModel.setIp(view.getIp());
        casTransExporterModel.setPort(view.getPort());
        casTransExporterModel.setUsername(view.getUserName());
        casTransExporterModel.setPassword(view.getPassword());
//        List<Cluster> clusterList = dao.getClusterListByExporter(casTransExporterModel);
//        List<OperationMonitorEntity> clusterMonitorList = new ArrayList<>();
        //将所有的集群插入数据库  新决定 不再添加集群
//        Map<String, String> map = new HashMap<>();
//        map.put("parentId", operationMonitorEntity.getUuid());
//        map.put("parentName", view.getName());
//        map.put("rootId", operationMonitorEntity.getUuid());
//        map.put("rootName", view.getName());
//        Map<String, String> clusterIdMap = new HashMap<>();
//        clusterList.forEach(cluster -> {
//            addCasCluster(view, cluster, map, clusterIdMap);
//        });
        //添加cvk
        List<Host> hostList = dao.getCvkAndVmListByExporter(casTransExporterModel);
        switch (view.getRadioType()) {
            case MONITOR_1:
                //仅监控cvk主机
                hostList.forEach(host -> {
                    //如果有集群，则parentId为集群Id，否则为casUuid //新决定 不考虑集群
                    addHost(host, view, casuuid);
                });

                break;
            case MONITOR_2:
                //监控所有cvk和vm
                hostList.forEach(host -> {
                    //如果有集群，则parentId为集群Id，否则为casUuid
                    String insertCvkt = "";
                    insertCvkt = addHost(host, view, casuuid);

                    if (!insertCvkt.equals("")) {
                        String finalInsertCvkt = insertCvkt;
                        host.getVirtualMachineList().forEach(vm -> {
                            addVm(vm, view, finalInsertCvkt);
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
                        insertCvkt3 = addHost(optHost.get(), view, casuuid);
                        if (!insertCvkt3.equals("")) {
                            List<VirtualMachine> virtualMachines = optHost.get().getVirtualMachineList().stream().
                                    filter(v -> (!v.isBeenAdd()) && transCvkId.getVmIds().contains(v.getId())).collect(Collectors.toList());

                            String finalInsertCvkt3 = insertCvkt3;
                            virtualMachines.forEach(vm -> {
                                addVm(vm, view, finalInsertCvkt3);
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

    private CasMonitorEntity setCasMonitorFiled(OperationMonitorView view) {
        CasMonitorEntity casMonitorEntity = new CasMonitorEntity();
        String casuuid = UUID.randomUUID().toString();
        casMonitorEntity.setUuid(casuuid);
        casMonitorEntity.setName(view.getName());
        casMonitorEntity.setIp(view.getIp());
        casMonitorEntity.setScrapeInterval(view.getTimeinterval());
        casMonitorEntity.setScrapeTimeout(view.getTimeout());
        casMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.SNMP.value());
        casMonitorEntity.setTemplateId(view.getMonitortemplate());
        casMonitorEntity.setPort(view.getPort());
        casMonitorEntity.setUsername(view.getUserName());
        casMonitorEntity.setPassword(view.getPassword());
        return casMonitorEntity;
    }

//    private void addCasCluster(OperationMonitorView view, Cluster cluster, Map<String, String> map, Map<String, String> clusterIdMap) {
//        try {
//            OperationMonitorEntity clusterMonitor = setClusterMonitorFiled(cluster, view);
//            clusterMonitor.setExtra(objectMapper.writeValueAsString(map));
//            clusterIdMap.put(cluster.getClusterId(), clusterMonitor.getUuid());
//            boolean insertCasCluster = dao.insertMonitorRecord(clusterMonitor);
//            if (insertCasCluster) {
//                //添加监控对象的告警规则
//                RuleMonitorEntity cluterRuleMonitorEntity = configService.addMonitorRecordAlertRule(clusterMonitor.getUuid(), clusterMonitor.getTemplateId());
//                //生成并下发etcd中的告警模板
//                configService.addAlertTemplateToEtcd(clusterMonitor.getLightTypeId(), clusterMonitor.getTemplateId(), cluterRuleMonitorEntity);
//            }
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private boolean addVm(VirtualMachine vm, OperationMonitorView view, String parentUuid) {

        //        OperationMonitorEntity vmMonitor = null;
//        Map<String, String> vmExtra = new HashMap<>();
//        vmExtra.put("parentId", parentUuid);
//        vmExtra.put("parentName", host.getName());
//        vmExtra.put("rootId", operationMonitorEntity.getUuid());
//        vmExtra.put("rootName", view.getName());

        try {

            VmMonitorEntity vmMonitor = setVmMonitorField(vm, view, parentUuid);

            boolean insertVm = dao.insertMonitorRecord(vmMonitor, view.getLightType());
            if (insertVm) {
                //添加监控对象的告警规则
                RuleMonitorEntity vmRuleMonitorEntity = configService.addMonitorRecordAlertRule(vmMonitor.getUuid(), vmMonitor.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(view.getLightType(), vmMonitor.getTemplateId(), vmRuleMonitorEntity);
            }
            return insertVm;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private VmMonitorEntity setVmMonitorField(VirtualMachine vm, OperationMonitorView view, String parentId) throws JsonProcessingException {
        VmMonitorEntity vmMonitorEntity = new VmMonitorEntity();
        vmMonitorEntity.setUuid(UUID.randomUUID().toString());
        vmMonitorEntity.setIp(vm.getIp());
        vmMonitorEntity.setName(vm.getName());
        vmMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.VIRTUALMACHINE.value());
        vmMonitorEntity.setTemplateId(view.getVmTemplate());
        vmMonitorEntity.setScrapeInterval(view.getTimeinterval());
        vmMonitorEntity.setScrapeTimeout(view.getTimeout());
        vmMonitorEntity.setCvkUuid(parentId);
        vmMonitorEntity.setVmId(vm.getId());
        return vmMonitorEntity;
    }

    private String addHost(Host host, OperationMonitorView view, String casUuid) {
        try {
            HostMonitorEntity hostMonitor = setCvkMonitorFiled(host, view, casUuid);
//            Map<String, String> extra = new HashMap<>();
            //如果有集群，则parentId为集群Id，否则为casUuid
//            extra.put("parentId", clusterId);
//            extra.put("rootId", operationMonitorEntity.getUuid());
//            extra.put("rootName", view.getName());
//            hostMonitor.setExtra(objectMapper.writeValueAsString(extra));
            boolean insertCvk = dao.insertMonitorRecord(hostMonitor, view.getLightType());
            if (insertCvk) {
                //添加监控对象的告警规则
                RuleMonitorEntity hostRuleMonitorEntity = configService.addMonitorRecordAlertRule(hostMonitor.getUuid(), hostMonitor.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(view.getLightType(), hostMonitor.getTemplateId(), hostRuleMonitorEntity);
            }
            return hostMonitor.getUuid();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }

    }

    private HostMonitorEntity setCvkMonitorFiled(Host host, OperationMonitorView view, String casUuid) throws JsonProcessingException {
        HostMonitorEntity cvkMonitorEntity = new HostMonitorEntity();
        cvkMonitorEntity.setUuid(UUID.randomUUID().toString());
        cvkMonitorEntity.setName(host.getName());
        cvkMonitorEntity.setIp(host.getIp());
        cvkMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.CVK.value());
        cvkMonitorEntity.setTemplateId(view.getCvkTemplate());
        cvkMonitorEntity.setScrapeInterval(view.getTimeinterval());
        cvkMonitorEntity.setScrapeTimeout(view.getTimeout());
        cvkMonitorEntity.setHostId(host.getId());
        cvkMonitorEntity.setHostpoolId(host.getHostpoolId());
        cvkMonitorEntity.setCasUuid(casUuid);
        return cvkMonitorEntity;
    }
//
//    private OperationMonitorEntity setClusterMonitorFiled(Cluster cluster, OperationMonitorView view) throws JsonProcessingException {
//        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
//        operationMonitorEntity.setUuid(UUID.randomUUID().toString());
//        operationMonitorEntity.setName(cluster.getName());
//        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.CASCLUSTER.value());
//        List<LightTypeEntity> lightTypeEntityList = dao.getLightTypeEntity();
//        Optional<LightTypeEntity> lightTypeEntity = lightTypeEntityList.stream().filter(x -> x.getName()
//                .equals(MonitorEnum.LightTypeEnum.CASCLUSTER.value())).findFirst();
//        lightTypeEntity.ifPresent(lightTypeEntity1 -> operationMonitorEntity.setLightTypeId(lightTypeEntity1.getUuid()));
//        operationMonitorEntity.setTemplateId(view.getCasClusterTemplate());
//        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
//        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
//        operationMonitorEntity.setCreateTime(new Date());
//        operationMonitorEntity.setUpdateTime(new Date());
//        operationMonitorEntity.setDeleted(0);
//        CasMonitorInfo monitorInfo = new CasMonitorInfo();
//        monitorInfo.setIp(view.getIp());
//        monitorInfo.setPort(view.getPort());
//        monitorInfo.setUserName(view.getUserName());
//        monitorInfo.setPassWord(view.getPassword());
//        monitorInfo.setClusterId(cluster.getClusterId());
//        monitorInfo.setHostpoolId(cluster.getHostpoolId());
//        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
//        return operationMonitorEntity;
//    }

    @Override
    public ResultMsg addContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        ResultMsg msg = new ResultMsg();
        K8sMonitorEntity k8sMonitorEntity = setK8sMonitorFiled(view);
        String k8suuid = k8sMonitorEntity.getUuid();
        boolean insertK8s = dao.insertMonitorRecord(k8sMonitorEntity, view.getLightType());
        if (insertK8s) {
            //添加监控对象的告警规则
            RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(k8sMonitorEntity.getUuid(), k8sMonitorEntity.getTemplateId());
            //生成并下发etcd中的告警模板
            configService.addAlertTemplateToEtcd(view.getLightType(), k8sMonitorEntity.getTemplateId(), ruleMonitorEntity);
        }
        List<Node> allNode = dao.getNodeListByExporter(view.getIp(), view.getApiPort());
        //node默认添加全部的
        Map<String, String> nodeMap = new HashMap<>();
//        Map<String, String> k8snExtra = new HashMap<>();
//        k8snExtra.put("parentId", operationMonitorEntity.getUuid());
//        k8snExtra.put("rootId", operationMonitorEntity.getUuid());
        allNode.forEach(node -> {
            if (!node.isBeenAdd()) {
                addK8sNode(view, node, k8suuid, nodeMap);
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
                    addContainer(view, container, nodeMap);
                });

                break;
            case MONITOR_3:
                //监控k8s和指定容器
                view.getContainerIds().forEach(cId -> {
                    Optional<Container> optCon = myContainerList.stream().filter(x -> (!x.isBeenAdd()) && x.getContainerId().equals(cId)).findFirst();
                    optCon.ifPresent(container -> addContainer(view, container, nodeMap));
                });
                break;
        }

        msg.setCode(HttpStatus.OK.value());
        msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        return msg;
    }

    private K8sMonitorEntity setK8sMonitorFiled(OperationMonitorView view) {
        K8sMonitorEntity k8sMonitorEntity = new K8sMonitorEntity();
        k8sMonitorEntity.setUuid(UUID.randomUUID().toString());
        k8sMonitorEntity.setIp(view.getIp());
        k8sMonitorEntity.setName(view.getName());
        k8sMonitorEntity.setScrapeInterval(view.getTimeinterval());
        k8sMonitorEntity.setScrapeTimeout(view.getTimeout());
        k8sMonitorEntity.setTemplateId(view.getK8sTemplate());
        k8sMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8S.value());
        k8sMonitorEntity.setApiPort(view.getApiPort());
        k8sMonitorEntity.setCadvisorPort(view.getCAdvisorPort());
        return k8sMonitorEntity;
    }

    @Override
    public ResultMsg delNetworkMonitorRecord(List<DelMonitorRecordView> view) {
        List<String> monitoUuidTemplateUUid = new ArrayList<>();
        view.forEach(x -> {
            try {
                delCommonOper(x.getUuid(), x.getLightType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String lightType = x.getLightType();
            if (lightType.equals(MonitorEnum.LightTypeEnum.SWITCH.value()) || lightType.equals(MonitorEnum.LightTypeEnum.ROUTER.value())
                    || lightType.equals(MonitorEnum.LightTypeEnum.LB.value()) || lightType.equals(MonitorEnum.LightTypeEnum.FIREWALL.value())) {
                NetworkMonitorEntity net = getNetworkMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+net.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.MYSQL.value())) {
                DBMonitorEntity db = getDbMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+db.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.TOMCAT.value())) {
                TomcatMonitorEntity tomcat = getTomcatMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+tomcat.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.CAS.value())) {
                CasMonitorEntity cas = getCasMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+cas.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.CVK.value())) {
                HostMonitorEntity cvk = getHostMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+cvk.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value())) {
                VmMonitorEntity vm = getVmMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+vm.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8S.value())) {
                K8sMonitorEntity k8s = getK8sMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+k8s.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8SNODE.value())) {
                K8snodeMonitorEntity k8sn = getK8snodeMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+k8sn.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8SCONTAINER.value())) {
                K8scontainerMonitorEntity k8sc = getK8sContainerMonitorEntity(x.getUuid());
                String tempuuid = (x.getUuid()+k8sc.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
            }
        });


        //从monitorconfig下发，将该告警模板删除
        boolean flag = configService.delAlertRuleByUuids(monitoUuidTemplateUUid);

        return ResCommon.genSimpleResByBool(flag);
    }

    boolean delCommonOper(String uuid, String lightype) throws IOException {
        // TODO: 2018/10/25 加入业务监控的设备不能删除
        if (businessService.isJoinBusinessMonitor(uuid)) {
            return false;
        }
        if (lightype.equals(MonitorEnum.LightTypeEnum.K8S.value())) {
            //k8s和cas的操作相同
            //根据这个uuid获取所有的node和container extra uuid
            boolean delk8s = dao.delMonitorRecord(uuid,lightype);
            if (delk8s){
                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除k8s
                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除k8s

                List<K8sNodeAndContainerView> view = dao.getAllNodeAndContainerByK8suuid(uuid);
                view.forEach(x->{
                    K8snodeMonitorEntity node = x.getK8snode();
                    boolean delk8snorc = dao.delMonitorRecord(node.getUuid(),MonitorEnum.LightTypeEnum.K8SNODE.value());
                    if (delk8snorc) {
                        // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除node
                        // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除node

                        x.getK8sContainerList().forEach(y->{
                            boolean delk8scrc = dao.delMonitorRecord(y.getUuid(),MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                            if (delk8scrc) {
                                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除container
                                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除container
                            }
                        });

                    }
                });
                return true;
            }
        } else if (lightype.equals(MonitorEnum.LightTypeEnum.K8SNODE.value())) {
                      boolean delk8sn = dao.delMonitorRecord(uuid,lightype);
            if (delk8sn){
                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除node
                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除node
                List<K8scontainerMonitorEntity> k8scList =  dao.getAllContainerByK8sNodeuuid(uuid);
                k8scList.forEach(y->{
                    boolean delk8scrc = dao.delMonitorRecord(y.getUuid(),MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                    if (delk8scrc) {
                        // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除container
                        // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除container

                    }
                });
                return true;
            }

        } else if (lightype.equals(MonitorEnum.LightTypeEnum.CAS.value())) {
            boolean delcas = dao.delMonitorRecord(uuid,lightype);
            if (delcas){
                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除cas
                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除cas

                List<CvkAndVmView> view = dao.getAllCvkAndVmByCasuuid(uuid);
                view.forEach(x->{
                    HostMonitorEntity host = x.getHostMonitor();
                    boolean delk8snorc = dao.delMonitorRecord(host.getUuid(),MonitorEnum.LightTypeEnum.CVK.value());
                    if (delk8snorc) {
                        // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除cvk
                        // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除cvk

                        x.getVmMonitorList().forEach(y->{
                            boolean delk8scrc = dao.delMonitorRecord(y.getUuid(),MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                            if (delk8scrc) {
                                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除vm
                                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除vm


                            }
                        });

                    }
                });
                return true;
            }
        } else if (lightype.equals(MonitorEnum.LightTypeEnum.CVK.value())) {

            boolean dekcvk = dao.delMonitorRecord(uuid,lightype);
            if (dekcvk){
                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除cvk
                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除cvk

                List<VmMonitorEntity> vmList =  dao.getAllVmByCvkuuid(uuid);
                vmList.forEach(y->{
                    boolean delvmrc = dao.delMonitorRecord(y.getUuid(),MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                    if (delvmrc) {
                        // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除vm
                        // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除vm

                    }
                });
                return true;
            }
        }else {
            //其他设备
            boolean delres = dao.delMonitorRecord(uuid, lightype);
            if (delres) {
                // TODO: 2018/10/22 调用拓扑的deleteBymonitoruuid
                // TODO: 2018/10/22 调用告警记录的deleteByMOnitorUuid

                return true;
            }
        }
        return false;
    }


//    @Override
//    public ResultMsg getMonitorRecord(String uuid) {
//        OperationMonitorEntity entity = dao.getMonitorRecordByUuid(uuid);
//        ResultMsg msg = new ResultMsg();
//        if (null != entity) {
//            msg.setCode(HttpStatus.OK.value());
//            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
//            msg.setData(entity);
//        } else {
//            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            msg.setMsg(CommonEnum.MSG_ERROR.value());
//        }
//        return msg;
//    }

    @Override
    public ResultMsg updateNetworkMonitorRecord(OperationMonitorView view) throws IOException {
        NetworkMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.FIREWALL.value()),NetworkMonitorEntity.class);
        NetworkMonitorEntity entity = updateNetworkCommonField(view, oldEntity);
        boolean flag = dao.insertMonitorRecord(entity,entity.getLightType());
        if (flag) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(entity.getUuid(), entity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(entity.getLightType(), entity.getTemplateId(), updateAlertRule);
            }
        }
        return ResCommon.genSimpleResByBool(flag);
    }

    @Override
    public ResultMsg updateMiddleMonitorRecord(OperationMonitorView view) throws IOException {
        TomcatMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.TOMCAT.value()),TomcatMonitorEntity.class);
        TomcatMonitorEntity entity = updateMiddleCommonField(view, oldEntity);
        boolean flag = dao.insertMonitorRecord(entity,MonitorEnum.LightTypeEnum.TOMCAT.value());
        if (flag) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(entity.getUuid(), entity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.TOMCAT.value(), entity.getTemplateId(), updateAlertRule);
            }
        }
        return ResCommon.genSimpleResByBool(flag);
    }

    private TomcatMonitorEntity updateMiddleCommonField(OperationMonitorView view, TomcatMonitorEntity oldEntity) {
        TomcatMonitorEntity entity = new TomcatMonitorEntity();
        entity.setUuid(oldEntity.getUuid());
        entity.setIp(view.getIp());
        entity.setMonitorType(oldEntity.getMonitorType());
        entity.setName(view.getName());
        entity.setScrapeInterval(view.getTimeinterval());
        entity.setScrapeTimeout(view.getTimeout());
        entity.setTemplateId(view.getMonitortemplate());
        if (view.isAuthentication()){
            entity.setAuthentication(1);
            entity.setUsername(view.getUserName());
            entity.setPassword(view.getPassword());
        }else {
            entity.setAuthentication(0);
        }
        entity.setPort(view.getPort());
        return entity;
    }

    @Override
    public ResultMsg updateDbMonitorRecord(OperationMonitorView view) throws IOException {
        DBMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.MYSQL.value()),DBMonitorEntity.class);
        DBMonitorEntity entity = updateDbCommonField(view, oldEntity);
        boolean flag = dao.insertMonitorRecord(entity,MonitorEnum.LightTypeEnum.MYSQL.value());
        if (flag) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(entity.getUuid(), entity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.MYSQL.value(), entity.getTemplateId(), updateAlertRule);
            }
        }
        return ResCommon.genSimpleResByBool(flag);
    }

    private DBMonitorEntity updateDbCommonField(OperationMonitorView view, DBMonitorEntity oldEntity) {
        DBMonitorEntity entity = new DBMonitorEntity();
        entity.setUuid(oldEntity.getUuid());
        entity.setIp(view.getIp());
        entity.setMonitorType(oldEntity.getMonitorType());
        entity.setName(view.getName());
        entity.setScrapeInterval(view.getTimeinterval());
        entity.setScrapeTimeout(view.getTimeout());
        entity.setTemplateId(view.getMonitortemplate());
        entity.setPort(view.getPort());
        entity.setDatabasename(view.getDatabaseName());
        entity.setUsername(view.getUserName());
        entity.setPassword(view.getPassword());
        return entity;
    }

    @Override
    public ResultMsg updateVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        //先修改cas
        OperationMonitorEntity oldEntity = dao.getMonitorRecordByUuid(view.getUuid());
        OperationMonitorEntity operationMonitorEntity = updateCommonField(view, oldEntity);
        operationMonitorEntity.setTemplateId(view.getCasTemplate());
        CasMonitorInfo monitorInfo = new CasMonitorInfo();
        monitorInfo.setIp(view.getIp());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean updateCas = dao.insertMonitorRecord(operationMonitorEntity);
        if (updateCas) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(operationMonitorEntity.getUuid(), operationMonitorEntity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), updateAlertRule);
            }
            //修改cas下的资源
            CasTransExporterModel casTransExporterModel = new CasTransExporterModel();

            casTransExporterModel.setIp(view.getIp());
            casTransExporterModel.setPort(view.getPort());
            casTransExporterModel.setUsername(view.getUserName());
            casTransExporterModel.setPassword(view.getPassword());
            List<Cluster> clusterList = dao.getClusterListByExporter(casTransExporterModel);
            Map<String, String> map = new HashMap<>();
            map.put("parentId", operationMonitorEntity.getUuid());
            map.put("parentName", view.getName());
            map.put("rootId", operationMonitorEntity.getUuid());
            map.put("rootName", view.getName());
            Map<String, String> clusterIdMap = new HashMap<>();
            clusterList.forEach(cluster -> {
                if (cluster.isBeenAdd()) {
                    //更新cluster
                    try {
                        OperationMonitorEntity oldCluster = dao.getMonitorRecordByUuid(cluster.getUuid());
                        OperationMonitorEntity updateCluster = updateCasClusterField(view, oldCluster, cluster);
                        updateCluster.setExtra(objectMapper.writeValueAsString(map));
                        clusterIdMap.put(cluster.getClusterId(), updateCluster.getUuid());
                        boolean updateClusterres = dao.insertMonitorRecord(updateCluster);
                        if (updateClusterres) {
                            //更新告警模板
                            RuleMonitorEntity updateClusterAlertRule = configService.updateMonitorRecordAlertRule(updateCluster.getUuid(), updateCluster.getTemplateId());
                            if (null != updateClusterAlertRule) {
                                configService.addAlertTemplateToEtcd(updateCluster.getLightTypeId(), updateCluster.getTemplateId(), updateClusterAlertRule);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                } else {
                    //新插入cluster
                    addCasCluster(view, cluster, map, clusterIdMap);
                }
            });
            //更新cvk vm
            List<Host> hostList = dao.getCvkAndVmListByExporter(casTransExporterModel);
            switch (view.getRadioType()) {
                case MONITOR_1:
                    //只修改或增加cvk
                    hostList.forEach(host -> {
                        //如果有集群，则parentId为集群Id，否则为casUuid
                        if (host.isBeenAdd()) {
                            //修改cvk
                            OperationMonitorEntity oldHost = dao.getMonitorRecordByUuid(host.getUuid());
                            if (null != host.getClusterId()) {
                                updateHost(host, view, operationMonitorEntity, clusterIdMap.get(host.getClusterId()), oldHost);
                            } else {
                                updateHost(host, view, operationMonitorEntity, operationMonitorEntity.getUuid(), oldHost);
                            }

                        } else {
                            //添加cvk
                            if (null != host.getClusterId()) {
                                addHost(host, view, operationMonitorEntity, clusterIdMap.get(host.getClusterId()));
                            } else {
                                addHost(host, view, operationMonitorEntity, operationMonitorEntity.getUuid());
                            }
                        }


                    });

                    break;
                case MONITOR_2:
                    //监控所有cvk和vm
                    hostList.forEach(host -> {
                        String reHostId = "";
                        //如果有集群，则parentId为集群Id，否则为casUuid
                        if (host.isBeenAdd()) {
                            //修改cvk
                            OperationMonitorEntity oldHost = dao.getMonitorRecordByUuid(host.getUuid());
                            if (null != host.getClusterId()) {
                                reHostId = updateHost(host, view, operationMonitorEntity, clusterIdMap.get(host.getClusterId()), oldHost);
                            } else {
                                reHostId = updateHost(host, view, operationMonitorEntity, operationMonitorEntity.getUuid(), oldHost);
                            }
                        } else {
                            //添加cvk
                            if (null != host.getClusterId()) {
                                reHostId = addHost(host, view, operationMonitorEntity, clusterIdMap.get(host.getClusterId()));
                            } else {
                                reHostId = addHost(host, view, operationMonitorEntity, operationMonitorEntity.getUuid());
                            }
                        }
                        if (!reHostId.equals("")) {
                            String finalInserthostt = reHostId;
                            host.getVirtualMachineList().forEach(vm -> {
                                if (vm.isBeenAdd()) {
                                    //修改vm
                                    OperationMonitorEntity oldVm = dao.getMonitorRecordByUuid(vm.getUuid());
                                    updateVm(vm, host, operationMonitorEntity, view, finalInserthostt, oldVm);
                                } else {
                                    addVm(vm, host, operationMonitorEntity, view, finalInserthostt);
                                }
                            });
                        }
                    });
                    break;
                case MONITOR_3:
                    //监控指定的cvk和vm
                    view.getCvkIds().forEach(transCvkId -> {
                        Optional<Host> optHost = hostList.stream().filter(x -> x.getId().equals(transCvkId.getCvkId())).findFirst();
                        if (optHost.isPresent()) {
                            String reHostId3 = "";
                            if (optHost.get().isBeenAdd()) {
                                //修改cvk
                                //修改cvk
                                OperationMonitorEntity oldHost = dao.getMonitorRecordByUuid(optHost.get().getUuid());
                                if (null != optHost.get().getClusterId()) {
                                    reHostId3 = updateHost(optHost.get(), view, operationMonitorEntity, clusterIdMap.get(optHost.get().getClusterId()), oldHost);
                                } else {
                                    reHostId3 = updateHost(optHost.get(), view, operationMonitorEntity, operationMonitorEntity.getUuid(), oldHost);
                                }
                            } else {
                                //添加cvk
                                if (null != optHost.get().getClusterId()) {
                                    reHostId3 = addHost(optHost.get(), view, operationMonitorEntity, clusterIdMap.get(optHost.get().getClusterId()));
                                } else {
                                    reHostId3 = addHost(optHost.get(), view, operationMonitorEntity, operationMonitorEntity.getUuid());
                                }
                            }

                            if (!reHostId3.equals("")) {
                                List<VirtualMachine> virtualMachines = optHost.get().getVirtualMachineList().stream().
                                        filter(v -> transCvkId.getVmIds().contains(v.getId())).collect(Collectors.toList());

                                String finalInsertCvkt3 = reHostId3;

                                virtualMachines.forEach(vm -> {
                                    if (vm.isBeenAdd()) {
                                        //修改vm
                                        OperationMonitorEntity oldVm = dao.getMonitorRecordByUuid(vm.getUuid());
                                        updateVm(vm, optHost.get(), operationMonitorEntity, view, finalInsertCvkt3, oldVm);
                                    } else {
                                        addVm(vm, optHost.get(), operationMonitorEntity, view, finalInsertCvkt3);
                                    }
                                });
                            }
                        }
                    });
                    break;
            }
        }
        return null;
    }

    private void updateVm(VirtualMachine vm, Host host, OperationMonitorEntity operationMonitorEntity, OperationMonitorView view, String parentUuid, OperationMonitorEntity oldVm) {
        OperationMonitorEntity vmMonitor = null;
        Map<String, String> vmExtra = new HashMap<>();
        vmExtra.put("parentId", parentUuid);
        vmExtra.put("parentName", host.getName());
        vmExtra.put("rootId", operationMonitorEntity.getUuid());
        vmExtra.put("rootName", view.getName());

        try {

            if (null != host.getClusterId()) {
                vmMonitor = updateVmMonitorField(vm, view, host.getHostpoolId(), host.getClusterId(), host.getId(), oldVm);
                vmMonitor.setExtra(objectMapper.writeValueAsString(vmExtra));
            } else {
                vmMonitor = updateVmMonitorField(vm, view, host.getHostpoolId(), "", host.getId(), oldVm);
                vmMonitor.setExtra(objectMapper.writeValueAsString(vmExtra));
            }
            boolean updateVm = dao.insertMonitorRecord(vmMonitor);
            if (updateVm) {
                //更新告警模板
                RuleMonitorEntity updateVmAlertRule = configService.updateMonitorRecordAlertRule(vmMonitor.getUuid(), vmMonitor.getTemplateId());
                if (null != updateVmAlertRule) {
                    configService.addAlertTemplateToEtcd(vmMonitor.getLightTypeId(), vmMonitor.getTemplateId(), updateVmAlertRule);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private OperationMonitorEntity updateVmMonitorField(VirtualMachine vm, OperationMonitorView view, String hostpoolId, String clusterId, String hostId, OperationMonitorEntity oldVm) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(oldVm.getUuid());
        operationMonitorEntity.setName(vm.getName());
        operationMonitorEntity.setMonitorType(oldVm.getMonitorType());
        operationMonitorEntity.setLightTypeId(oldVm.getLightTypeId());
        operationMonitorEntity.setTemplateId(view.getVmTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(oldVm.getCreateTime());
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

    private String updateHost(Host host, OperationMonitorView view, OperationMonitorEntity casMonitor, String parentId, OperationMonitorEntity oldHost) {
        try {
            OperationMonitorEntity hostMonitor = updateCvkMonitorFiled(host, view, oldHost);
            Map<String, String> extra = new HashMap<>();
            extra.put("parentId", parentId);
            extra.put("rootId", casMonitor.getUuid());
            extra.put("rootName", view.getName());
            hostMonitor.setExtra(objectMapper.writeValueAsString(extra));
            boolean updateCvkres = dao.insertMonitorRecord(hostMonitor);
            if (updateCvkres) {
                //更新告警模板
                RuleMonitorEntity updateCvkAlertRule = configService.updateMonitorRecordAlertRule(hostMonitor.getUuid(), hostMonitor.getTemplateId());
                if (null != updateCvkAlertRule) {
                    configService.addAlertTemplateToEtcd(hostMonitor.getLightTypeId(), hostMonitor.getTemplateId(), updateCvkAlertRule);
                    return hostMonitor.getUuid();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private OperationMonitorEntity updateCvkMonitorFiled(Host host, OperationMonitorView view, OperationMonitorEntity oldHost) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(oldHost.getUuid());
        operationMonitorEntity.setName(host.getName());
        operationMonitorEntity.setIp(host.getIp());
        operationMonitorEntity.setMonitorType(oldHost.getMonitorType());
        operationMonitorEntity.setLightTypeId(oldHost.getLightTypeId());
        operationMonitorEntity.setTemplateId(view.getCvkTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(oldHost.getCreateTime());
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

    @Override
    public ResultMsg updateContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        OperationMonitorEntity oldK8s = dao.getMonitorRecordByUuid(view.getUuid());
        OperationMonitorEntity operationMonitorEntity = updateCommonField(view, oldK8s);
        operationMonitorEntity.setTemplateId(view.getK8sTemplate());
        operationMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8S.value());
        K8sMonitorInfo monitorInfo = new K8sMonitorInfo();
        monitorInfo.setMasterIp(view.getIp());
        monitorInfo.setApiPort(view.getApiPort());
        monitorInfo.setCadvisorPort(view.getCAdvisorPort());
        operationMonitorEntity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        boolean updateK8s = dao.insertMonitorRecord(operationMonitorEntity);
        if (updateK8s) {
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(operationMonitorEntity.getUuid(), operationMonitorEntity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(operationMonitorEntity.getLightTypeId(), operationMonitorEntity.getTemplateId(), updateAlertRule);
            }
        }
        List<Node> allNode = dao.getNodeListByExporter(view.getIp(), view.getApiPort());
        //node默认添加全部的
        Map<String, String> nodeMap = new HashMap<>();
        Map<String, String> k8snExtra = new HashMap<>();
        k8snExtra.put("parentId", operationMonitorEntity.getUuid());
        k8snExtra.put("rootId", operationMonitorEntity.getUuid());
        allNode.forEach(node -> {
            if (node.isBeenAdd()) {
                //修改 node
                try {
                    OperationMonitorEntity oldK8sNode = dao.getMonitorRecordByUuid(node.getUuid());
                    OperationMonitorEntity k8snNode = updateNodeMonitorField(view, node, oldK8sNode);
                    k8snNode.setExtra(objectMapper.writeValueAsString(k8snExtra));
                    boolean updateK8sn = dao.insertMonitorRecord(k8snNode);
                    nodeMap.put(node.getNodeIp(), k8snNode.getUuid());
                    if (updateK8sn) {
                        //更新告警模板
                        RuleMonitorEntity updateClusterAlertRule = configService.updateMonitorRecordAlertRule(k8snNode.getUuid(), k8snNode.getTemplateId());
                        if (null != updateClusterAlertRule) {
                            configService.addAlertTemplateToEtcd(k8snNode.getLightTypeId(), k8snNode.getTemplateId(), updateClusterAlertRule);
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                //添加node
                addK8sNode(view, node, k8snExtra, nodeMap);
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
                    if (container.isBeenAdd()) {
                        //修改container
                        OperationMonitorEntity oldContainer = dao.getMonitorRecordByUuid(container.getUuid());
                        updateContainer(view, container, operationMonitorEntity, nodeMap, oldContainer);
                    } else {
                        //添加container
                        addContainer(view, container, operationMonitorEntity, nodeMap);
                    }
                });

                break;
            case MONITOR_3:
                //监控k8s和指定容器
                view.getContainerIds().forEach(cId -> {
                    Optional<Container> optCon = myContainerList.stream().filter(x -> x.getContainerId().equals(cId)).findFirst();
                    if (optCon.isPresent()) {
                        if (optCon.get().isBeenAdd()) {
                            //修改container
                            OperationMonitorEntity oldContainer = dao.getMonitorRecordByUuid(optCon.get().getUuid());
                            updateContainer(view, optCon.get(), operationMonitorEntity, nodeMap, oldContainer);
                        } else {
                            //添加container
                            addContainer(view, optCon.get(), operationMonitorEntity, nodeMap);
                        }
                    }
                });
                break;
        }

        return null;
    }

    @Override
    public int getMonitorCountByTemplateId(String uuid) {
        List<OperationMonitorEntity> list = dao.getMonitorRecordByTemplateId(uuid);
        return list.size();
    }

    private void updateContainer(OperationMonitorView view, Container container, OperationMonitorEntity operationMonitorEntity, Map<String, String> nodeMap, OperationMonitorEntity oldContainer) {

        try {
            OperationMonitorEntity monitorContainer = updateContainerMonitorField(view, container, oldContainer);
            Map<String, String> cExtra = new HashMap<>();
            cExtra.put("rootId", operationMonitorEntity.getUuid());
            cExtra.put("parentId", nodeMap.getOrDefault(container.getNodeIp(), ""));
            monitorContainer.setExtra(objectMapper.writeValueAsString(cExtra));
            boolean updateK8sc = dao.insertMonitorRecord(monitorContainer);
            if (updateK8sc) {
                RuleMonitorEntity updateContainerAlertRule = configService.updateMonitorRecordAlertRule(monitorContainer.getUuid(), monitorContainer.getTemplateId());
                if (null != updateContainerAlertRule) {
                    configService.addAlertTemplateToEtcd(monitorContainer.getLightTypeId(), monitorContainer.getTemplateId(), updateContainerAlertRule);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private OperationMonitorEntity updateContainerMonitorField(OperationMonitorView view, Container container, OperationMonitorEntity oldContainer) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(oldContainer.getUuid());
        operationMonitorEntity.setName(container.getContainerName());
        operationMonitorEntity.setIp(container.getNodeIp());
        operationMonitorEntity.setMonitorType(oldContainer.getMonitorType());
        operationMonitorEntity.setLightTypeId(oldContainer.getLightTypeId());
        operationMonitorEntity.setTemplateId(view.getK8sContainerTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(oldContainer.getCreateTime());
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

    private void addK8sNode(OperationMonitorView view, Node node, String k8sUuid, Map<String, String> nodeMap) {
        try {
            K8snodeMonitorEntity k8snNode = setNodeMonitorField(view, node, k8sUuid);
            nodeMap.put(node.getNodeIp(), k8snNode.getUuid());
            boolean insertK8sn = dao.insertMonitorRecord(k8snNode, view.getLightType());
            if (insertK8sn) {
                //添加监控对象的告警规则
                RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(k8snNode.getUuid(), k8snNode.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(view.getLightType(), k8snNode.getTemplateId(), ruleMonitorEntity);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private OperationMonitorEntity updateNodeMonitorField(OperationMonitorView view, Node node, OperationMonitorEntity oldK8sNode) throws JsonProcessingException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        operationMonitorEntity.setUuid(oldK8sNode.getUuid());
        operationMonitorEntity.setName(node.getNodeName());
        operationMonitorEntity.setIp(node.getNodeIp());
        operationMonitorEntity.setMonitorType(oldK8sNode.getMonitorType());
        operationMonitorEntity.setLightTypeId(oldK8sNode.getLightTypeId());
        operationMonitorEntity.setTemplateId(view.getK8sNodeTemplate());
        operationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        operationMonitorEntity.setScrapeTimeout(view.getTimeout());
        operationMonitorEntity.setCreateTime(oldK8sNode.getCreateTime());
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

    private OperationMonitorEntity updateCasClusterField(OperationMonitorView view, OperationMonitorEntity oldEntity, Cluster cluster) throws JsonProcessingException {
        OperationMonitorEntity entity = new OperationMonitorEntity();
        entity.setUuid(oldEntity.getUuid());
        entity.setMonitorType(oldEntity.getMonitorType());
        entity.setLightTypeId(oldEntity.getLightTypeId());
        entity.setName(cluster.getName());
        entity.setTemplateId(view.getCasClusterTemplate());
        entity.setCreateTime(oldEntity.getCreateTime());
        entity.setUpdateTime(new Date());
        entity.setScrapeInterval(view.getTimeinterval());
        entity.setScrapeTimeout(view.getTimeout());
        entity.setDeleted(0);
        CasMonitorInfo monitorInfo = new CasMonitorInfo();
        monitorInfo.setIp(view.getIp());
        monitorInfo.setPort(view.getPort());
        monitorInfo.setUserName(view.getUserName());
        monitorInfo.setPassWord(view.getPassword());
        monitorInfo.setClusterId(cluster.getClusterId());
        monitorInfo.setHostpoolId(cluster.getHostpoolId());
        entity.setMonitorInfo(objectMapper.writeValueAsString(monitorInfo));
        return entity;
    }

    private NetworkMonitorEntity updateNetworkCommonField(OperationMonitorView view, NetworkMonitorEntity oldEntity) {
        NetworkMonitorEntity entity = new NetworkMonitorEntity();
        entity.setUuid(oldEntity.getUuid());
        entity.setIp(view.getIp());
        entity.setMonitorType(oldEntity.getMonitorType());
        entity.setName(view.getName());
        entity.setLightType(oldEntity.getLightType());
        entity.setScrapeInterval(view.getTimeinterval());
        entity.setScrapeTimeout(view.getTimeout());
        entity.setTemplateId(view.getMonitortemplate());
        if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V1.value())) {
            entity.setSnmpVersion(NUMBER_1);
        } else if (view.getMonitorMode().equals(MonitorEnum.MonitorRecordEnum.SNMP_VERSION_V2.value())) {
            entity.setSnmpVersion(NUMBER_2);
        }
        entity.setReadCommunity(view.getReadcommunity());
        entity.setWriteCommunity(view.getWritecommunity());
        entity.setPort(view.getPort());
        return entity;
    }

    private void addContainer(OperationMonitorView view, Container container, Map<String, String> nodeMap) {
        try {
            K8scontainerMonitorEntity monitorContainer = setContainerMonitorField(view, container, nodeMap);
            boolean insertK8sc = dao.insertMonitorRecord(monitorContainer, view.getLightType());
            if (insertK8sc) {
                //添加监控对象的告警规则
                RuleMonitorEntity ruleMonitorEntityC = configService.addMonitorRecordAlertRule(monitorContainer.getUuid(), monitorContainer.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(view.getLightType(), monitorContainer.getTemplateId(), ruleMonitorEntityC);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    private K8scontainerMonitorEntity setContainerMonitorField(OperationMonitorView view, Container container, Map<String, String> nodeMap) throws JsonProcessingException {
        K8scontainerMonitorEntity k8scMonitorEntity = new K8scontainerMonitorEntity();
        k8scMonitorEntity.setUuid(UUID.randomUUID().toString());
        k8scMonitorEntity.setName(container.getContainerName());
        k8scMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8SCONTAINER.value());
        k8scMonitorEntity.setTemplateId(view.getK8sContainerTemplate());
        k8scMonitorEntity.setScrapeInterval(view.getTimeinterval());
        k8scMonitorEntity.setScrapeTimeout(view.getTimeout());
        k8scMonitorEntity.setContainer_id(container.getContainerId());
        k8scMonitorEntity.setPod_name(container.getPodName());
        k8scMonitorEntity.setPod_namespace(container.getPodNamespace());
        k8scMonitorEntity.setK8snodeUuid(nodeMap.getOrDefault(container.getNodeIp(), ""));
        return k8scMonitorEntity;
    }

    private K8snodeMonitorEntity setNodeMonitorField(OperationMonitorView view, Node node, String k8suuid) throws JsonProcessingException {
        K8snodeMonitorEntity k8snodeMonitorEntity = new K8snodeMonitorEntity();
        k8snodeMonitorEntity.setUuid(UUID.randomUUID().toString());
        k8snodeMonitorEntity.setName(node.getNodeName());
        k8snodeMonitorEntity.setIp(node.getNodeIp());
        k8snodeMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.K8SNODE.value());
        k8snodeMonitorEntity.setTemplateId(view.getK8sNodeTemplate());
        k8snodeMonitorEntity.setScrapeInterval(view.getTimeinterval());
        k8snodeMonitorEntity.setScrapeTimeout(view.getTimeout());
        k8snodeMonitorEntity.setK8sUuid(k8suuid);
        return k8snodeMonitorEntity;
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

    @Override
    public ResultMsg getBusinessMonitorRecord() {
        List<OperationMonitorEntity> allMonitor = new ArrayList<>();

        List<TomcatMonitorEntity> tomcatMonitor = dao.getAllTomcatMonitorEntity();
        tomcatMonitor.forEach(x->{
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            allMonitor.add(entity);
        });
        List<DBMonitorEntity> dbMonitor = dao.getAllDbMonitorEntity();
        dbMonitor.forEach(x->{
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            allMonitor.add(entity);
        });
        List<HostMonitorEntity> hostMonitor = dao.getAllHostMonitorEntity();
        hostMonitor.forEach(x->{
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            allMonitor.add(entity);
        });
        List<VmMonitorEntity> vmMonitor = dao.getAllVmMonitorEntity();
        vmMonitor.forEach(x->{
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            allMonitor.add(entity);
        });
        List<K8snodeMonitorEntity> k8snodeMonitor = dao.getAllK8snodeMonitorEntity();
        k8snodeMonitor.forEach(x->{
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            allMonitor.add(entity);
        });
        List<K8scontainerMonitorEntity> k8scontainerMOnitor = dao.getAllK8sContainerMonitorEntity();
        k8scontainerMOnitor.forEach(x->{
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            allMonitor.add(entity);
        });

        return ResCommon.getCommonResultMsg(allMonitor);
    }

    @Override
    public ResultMsg getContainerListByExporter(String ip, String apiPort) {
        List<Container> containerList = dao.getContainerListByExporter(ip, apiPort);
        return ResCommon.getCommonResultMsg(containerList);
    }

    @Override
    public ResultMsg getCvkAndVmListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException {
//        List<Host> hosts = dao.getCvkAndVmListByExporter(casTransExporterModel);

        List<Host> hosts = new ArrayList<>();
        Host host1 = new Host();
        host1.setName("cvk1");
        host1.setId("111111");
        host1.setStatus("1");
        host1.setIp("172.17.5.135");
        host1.setBeenAdd(false);
        host1.setClusterId("1");
        host1.setHostpoolId("1");
        List<VirtualMachine> vms1 = new ArrayList<>();
        VirtualMachine vm1 = new VirtualMachine();
        vm1.setName("vm1");
        vm1.setId("1");
        vm1.setStatus("1");
        vm1.setOs("linux");
        vm1.setIp("172.17.5.133");
        vm1.setBeenAdd(false);
        vm1.setCvkId("111111");
        vms1.add(vm1);
        VirtualMachine vm2 = new VirtualMachine();
        vm2.setName("vm2");
        vm2.setId("2");
        vm2.setStatus("1");
        vm2.setOs("linux");
        vm2.setIp("172.17.5.134");
        vm2.setBeenAdd(false);
        vm2.setCvkId("111111");
        vms1.add(vm2);
        host1.setVirtualMachineList(vms1);
        hosts.add(host1);

        Host host2 = new Host();
        host2.setName("cvk2");
        host2.setId("111112");
        host2.setStatus("1");
        host2.setIp("172.17.5.133");
        host2.setBeenAdd(false);
        host2.setClusterId("1");
        host2.setHostpoolId("1");
        List<VirtualMachine> vms2 = new ArrayList<>();
        VirtualMachine vm3 = new VirtualMachine();
        vm3.setName("vm3");
        vm3.setId("3");
        vm3.setStatus("1");
        vm3.setOs("linux");
        vm3.setIp("172.17.5.139");
        vm3.setCvkId("111112");
        vm3.setBeenAdd(false);
        vms2.add(vm3);
        host2.setVirtualMachineList(vms2);
        hosts.add(host2);
        return ResCommon.getCommonResultMsg(hosts);
    }

    @Override
    public NetworkMonitorEntity getNetworkMonitorEntity(String uuid) {
        //lightype 网络设备中随便一个lightype都可以
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.FIREWALL.value());
        try {
            return objectMapper.readValue(response, NetworkMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TomcatMonitorEntity getTomcatMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.TOMCAT.value());
        try {
            return objectMapper.readValue(response, TomcatMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DBMonitorEntity getDbMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.MYSQL.value());
        try {
            return objectMapper.readValue(response, DBMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CasMonitorEntity getCasMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.CAS.value());
        try {
            return objectMapper.readValue(response, CasMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HostMonitorEntity getHostMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.CVK.value());
        try {
            return objectMapper.readValue(response, HostMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VmMonitorEntity getVmMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
        try {
            return objectMapper.readValue(response, VmMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K8sMonitorEntity getK8sMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.K8S.value());
        try {
            return objectMapper.readValue(response, K8sMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K8snodeMonitorEntity getK8snodeMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.K8SNODE.value());
        try {
            return objectMapper.readValue(response, K8snodeMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K8scontainerMonitorEntity getK8sContainerMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid,MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
        try {
            return objectMapper.readValue(response, K8scontainerMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
