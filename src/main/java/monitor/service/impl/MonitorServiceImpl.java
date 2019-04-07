package monitor.service.impl;

import alert.service.AlertService;
import business.entity.PageData;
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
import monitorConfig.entity.template.AlertRuleTemplateEntity;
import monitorConfig.entity.template.MonitorTemplate;
import monitorConfig.entity.template.RuleMonitorEntity;
import monitorConfig.service.MonitorConfigService;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.nio.ch.Net;
import topo.service.TopoService;

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

    @Autowired
    TopoService topoService;

    @Autowired
    AlertService alertService;

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

    private List<CvkId> genCvkIdsObject(String str) {
        List<CvkId> cvkIds = new ArrayList<>();
        JSONObject jsonObject = JSONObject.fromObject(str);
        Iterator<String> i = jsonObject.keys();
        while (i.hasNext()) {
            CvkId cvkId = new CvkId();
            String key = i.next();
            JSONArray array = jsonObject.getJSONArray(key);
            List<String> vms = new ArrayList<>();
            array.forEach(x -> {
                vms.add(x.toString());
            });
            cvkId.setCvkId(key);
            cvkId.setVmIds(vms);
            cvkIds.add(cvkId);
        }
        return cvkIds;
    }

    private List<String> genk8sconObject(String str) {
        List<String> k8scontainerIds = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(str);
        jsonArray.forEach(x -> {
            k8scontainerIds.add(x.toString());
        });
        return k8scontainerIds;
    }

    @Override
    public ResultMsg addVirtualMonitorRecord(OperationMonitorView view) throws IOException {
        ResultMsg msg = new ResultMsg();

        List<CvkId> cvkIds = genCvkIdsObject(view.getCvkIds());
        // List<CvkId> cvkIds= objectMapper.readValue(view.getCvkIds(),new TypeReference<List<CvkId>>(){});
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
                cvkIds.forEach(transCvkId -> {
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
        casMonitorEntity.setMonitorType(MonitorEnum.MonitorTypeEnum.CAS.value());
        casMonitorEntity.setTemplateId(view.getCasTemplate());
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

            boolean insertVm = dao.insertMonitorRecord(vmMonitor, MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
            if (insertVm) {
                //添加监控对象的告警规则
                RuleMonitorEntity vmRuleMonitorEntity = configService.addMonitorRecordAlertRule(vmMonitor.getUuid(), vmMonitor.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value(), vmMonitor.getTemplateId(), vmRuleMonitorEntity);
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
            boolean insertCvk = dao.insertMonitorRecord(hostMonitor, MonitorEnum.LightTypeEnum.CVK.value());
            if (insertCvk) {
                //添加监控对象的告警规则
                RuleMonitorEntity hostRuleMonitorEntity = configService.addMonitorRecordAlertRule(hostMonitor.getUuid(), hostMonitor.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.CVK.value(), hostMonitor.getTemplateId(), hostRuleMonitorEntity);
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
    public ResultMsg addContainerMonitorRecord(OperationMonitorView view) throws IOException {
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
            if (node.getPods() != null) {
                node.getPods().forEach(pod -> {
                    myContainerList.addAll(pod.getContainers());
                });
            }
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
                List<String> containerIds = genk8sconObject(view.getContainerIds());
                containerIds.forEach(cId -> {
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
        if (null!=view && view.size()>0){
            view.forEach(x -> {
                try {
                    List<String> list  = delCommonOper(x.getUuid(), x.getLightType());
                    if (list==null){
                        //已经加入业务监控 不能删除 结束
                        return;
                    }else {
                        monitoUuidTemplateUUid.addAll(list);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //从monitorconfig下发，将该告警模板删除
            boolean flag = configService.delAlertRuleByUuids(monitoUuidTemplateUUid);

            return ResCommon.genSimpleResByBool(flag);
        }else {
            return ResCommon.genSimpleResByBool(true);
        }

    }

    List<String> delCommonOper(String uuid, String lightype) throws IOException {
        //  2018/10/25 加入业务监控的设备不能删除
        if (businessService.isJoinBusinessMonitor(uuid)) {
            return null;
        }
        List<String> monitoUuidTemplateUUid = new ArrayList<>();
        if (lightype.equals(MonitorEnum.LightTypeEnum.K8S.value())) {
            //k8s和cas的操作相同
            //根据这个uuid获取所有的node和container extra uuid
            K8sMonitorEntity k8sEntity = getK8sMonitorEntity(uuid);
            List<K8sNodeAndContainerView> view = dao.getAllNodeAndContainerByK8suuid(uuid);
            view.forEach(x -> {
                K8snodeMonitorEntity node = x.getK8snode();
                x.getK8sContainerList().forEach(y -> {
                    boolean delk8scrc = dao.delMonitorRecord(y.getUuid(), MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                    String tempuuid = (y.getUuid() + y.getTemplateId()).replaceAll("-", "");
                    monitoUuidTemplateUUid.add(tempuuid);
                    if (delk8scrc) {
                        //: 2018/10/22 调用拓扑的deleteBymonitoruuid 删除container
                        topoService.deleteTopoResourceBymonitoruuid(y.getUuid());
                        // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除container
                        alertService.deleteAlertResourceBymonitoruuid(y.getUuid());
                    }
                });
                boolean delk8snorc = dao.delMonitorRecord(node.getUuid(), MonitorEnum.LightTypeEnum.K8SNODE.value());
                String tempuuid = (node.getUuid() + node.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
                if (delk8snorc) {
                    // 2018/10/22 调用拓扑的deleteBymonitoruuid 删除node
                    topoService.deleteTopoResourceBymonitoruuid(node.getUuid());
                    // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除node
                    alertService.deleteAlertResourceBymonitoruuid(node.getUuid());

                }
            });
            boolean delk8s = dao.delMonitorRecord(uuid, lightype);
            String tempuuid = (uuid + k8sEntity.getTemplateId()).replaceAll("-", "");
            monitoUuidTemplateUUid.add(tempuuid);
            if (delk8s) {
                //  2018/10/22 调用拓扑的deleteBymonitoruuid 删除k8s
                topoService.deleteTopoResourceBymonitoruuid(uuid);
                // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除k8s
                alertService.deleteAlertResourceBymonitoruuid(uuid);
            }
        } else if (lightype.equals(MonitorEnum.LightTypeEnum.K8SNODE.value())) {
            K8snodeMonitorEntity nodeEntity = getK8snodeMonitorEntity(uuid);
            List<K8scontainerMonitorEntity> k8scList = dao.getAllContainerByK8sNodeuuid(uuid);
            k8scList.forEach(y -> {
                boolean delk8scrc = dao.delMonitorRecord(y.getUuid(), MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                String tempuuid = (y.getUuid() + y.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
                if (delk8scrc) {
                    //  2018/10/22 调用拓扑的deleteBymonitoruuid 删除container
                    topoService.deleteTopoResourceBymonitoruuid(y.getUuid());
                    // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除container
                    alertService.deleteAlertResourceBymonitoruuid(y.getUuid());

                }
            });
            boolean delk8sn = dao.delMonitorRecord(uuid, lightype);
            String tempuuid = (uuid + nodeEntity.getTemplateId()).replaceAll("-", "");
            monitoUuidTemplateUUid.add(tempuuid);
            if (delk8sn) {
                // : 2018/10/22 调用拓扑的deleteBymonitoruuid 删除node
                topoService.deleteTopoResourceBymonitoruuid(uuid);
                // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除node
                alertService.deleteAlertResourceBymonitoruuid(uuid);

            }

        } else if (lightype.equals(MonitorEnum.LightTypeEnum.CAS.value())) {
            CasMonitorEntity casEntity = getCasMonitorEntity(uuid);
            List<CvkAndVmView> view = dao.getAllCvkAndVmByCasuuid(uuid);
            view.forEach(x -> {
                HostMonitorEntity host = x.getHostMonitor();
                //先删除vm 在删除cvk 和cas 不然有外键约束
                x.getVmMonitorList().forEach(y -> {
                    boolean delk8scrc = dao.delMonitorRecord(y.getUuid(), MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                    String tempuuid = (y.getUuid() + y.getTemplateId()).replaceAll("-", "");
                    monitoUuidTemplateUUid.add(tempuuid);
                    if (delk8scrc) {
                        // : 2018/10/22 调用拓扑的deleteBymonitoruuid 删除vm
                        //     topoService.deleteTopoResourceBymonitoruuid(y.getUuid());
                        // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除vm
                        //     alertService.deleteAlertResourceBymonitoruuid(y.getUuid());
                    }
                });
                boolean delk8snorc = dao.delMonitorRecord(host.getUuid(), MonitorEnum.LightTypeEnum.CVK.value());
                String tempuuid = (host.getUuid() + host.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
                if (delk8snorc) {
                    // : 2018/10/22 调用拓扑的deleteBymonitoruuid 删除cvk
                    //    topoService.deleteTopoResourceBymonitoruuid(host.getUuid());
                    // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除cvk
                    //    alertService.deleteAlertResourceBymonitoruuid(host.getUuid());
                }

            });
            boolean delcas = dao.delMonitorRecord(uuid, lightype);
            String tempuuid = (casEntity.getUuid() + casEntity.getTemplateId()).replaceAll("-", "");
            monitoUuidTemplateUUid.add(tempuuid);
            if (delcas) {
                //  2018/10/22 调用拓扑的deleteBymonitoruuid 删除cas
                //    topoService.deleteTopoResourceBymonitoruuid(uuid);
                // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除cas
                //    alertService.deleteAlertResourceBymonitoruuid(uuid);
            }
        } else if (lightype.equals(MonitorEnum.LightTypeEnum.CVK.value())) {
            HostMonitorEntity hostEntity = getHostMonitorEntity(uuid);
            List<VmMonitorEntity> vmList = dao.getAllVmByCvkuuid(uuid);
            vmList.forEach(y -> {
                boolean delvmrc = dao.delMonitorRecord(y.getUuid(), MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                String tempuuid = (y.getUuid() + y.getTemplateId()).replaceAll("-", "");
                monitoUuidTemplateUUid.add(tempuuid);
                if (delvmrc) {
                    // : 2018/10/22 调用拓扑的deleteBymonitoruuid 删除vm
                    topoService.deleteTopoResourceBymonitoruuid(y.getUuid());
                    // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除vm
                    alertService.deleteAlertResourceBymonitoruuid(y.getUuid());
                }
            });
            boolean dekcvk = dao.delMonitorRecord(uuid, lightype);
            String tempuuid = (hostEntity.getUuid() + hostEntity.getTemplateId()).replaceAll("-", "");
            monitoUuidTemplateUUid.add(tempuuid);
            if (dekcvk) {
                // : 2018/10/22 调用拓扑的deleteBymonitoruuid 删除cvk
                topoService.deleteTopoResourceBymonitoruuid(uuid);
                // : 2018/10/22 调用告警记录的deleteByMOnitorUuid 删除cvk
                alertService.deleteAlertResourceBymonitoruuid(uuid);
            }
        } else {
            //其他设备

            String tempTempId = "";
            if (lightype.equals(MonitorEnum.LightTypeEnum.SWITCH.value()) || lightype.equals(MonitorEnum.LightTypeEnum.ROUTER.value())
                    || lightype.equals(MonitorEnum.LightTypeEnum.LB.value()) || lightype.equals(MonitorEnum.LightTypeEnum.FIREWALL.value())) {
                NetworkMonitorEntity net = getNetworkMonitorEntity(uuid);
                tempTempId = net.getTemplateId();
            }else if (lightype.equals(MonitorEnum.LightTypeEnum.MYSQL.value())) {
                DBMonitorEntity db = getDbMonitorEntity(uuid);
                tempTempId = db.getTemplateId();
            }else if (lightype.equals(MonitorEnum.LightTypeEnum.TOMCAT.value())) {
                TomcatMonitorEntity tomcat = getTomcatMonitorEntity(uuid);
                tempTempId = tomcat.getTemplateId();
            }else if (lightype.equals(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value())) {
                VmMonitorEntity vm = getVmMonitorEntity(uuid);
                tempTempId = vm.getTemplateId();
            } else if (lightype.equals(MonitorEnum.LightTypeEnum.K8SCONTAINER.value())) {
                K8scontainerMonitorEntity k8sc = getK8sContainerMonitorEntity(uuid);
                tempTempId = k8sc.getTemplateId();
            }
            boolean delres = dao.delMonitorRecord(uuid, lightype);
            String tempuuid = (uuid + tempTempId).replaceAll("-", "");
            monitoUuidTemplateUUid.add(tempuuid);
            if (delres) {
                // : 2018/10/22 调用拓扑的deleteBymonitoruuid
         //       topoService.deleteTopoResourceBymonitoruuid(uuid);
                // : 2018/10/22 调用告警记录的deleteByMOnitorUuid
        //        alertService.deleteAlertResourceBymonitoruuid(uuid);

//                return true;
            }
        }
        return monitoUuidTemplateUUid;
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
        NetworkMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.FIREWALL.value()), NetworkMonitorEntity.class);
        NetworkMonitorEntity entity = updateNetworkCommonField(view, oldEntity);
        boolean flag = dao.insertMonitorRecord(entity, entity.getLightType());
        if (flag && !oldEntity.getTemplateId().equals(entity.getTemplateId())) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(entity.getUuid(), entity.getTemplateId(), oldEntity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(entity.getLightType(), entity.getTemplateId(), updateAlertRule);
            }
        }
        return ResCommon.genSimpleResByBool(flag);
    }

    @Override
    public ResultMsg updateMiddleMonitorRecord(OperationMonitorView view) throws IOException {
        TomcatMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.TOMCAT.value()), TomcatMonitorEntity.class);
        TomcatMonitorEntity entity = updateMiddleCommonField(view, oldEntity);
        boolean flag = dao.insertMonitorRecord(entity, MonitorEnum.LightTypeEnum.TOMCAT.value());
        if (flag && !oldEntity.getTemplateId().equals(entity.getTemplateId())) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(entity.getUuid(), entity.getTemplateId(), oldEntity.getTemplateId());
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
        if (view.isAuthentication()) {
            entity.setAuthentication(1);
            entity.setUsername(view.getUserName());
            entity.setPassword(view.getPassword());
        } else {
            entity.setAuthentication(0);
        }
        entity.setPort(view.getPort());
        return entity;
    }

    @Override
    public ResultMsg updateDbMonitorRecord(OperationMonitorView view) throws IOException {
        DBMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.MYSQL.value()), DBMonitorEntity.class);
        DBMonitorEntity entity = updateDbCommonField(view, oldEntity);
        boolean flag = dao.insertMonitorRecord(entity, MonitorEnum.LightTypeEnum.MYSQL.value());
        if (flag && !oldEntity.getTemplateId().equals(entity.getTemplateId())) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(entity.getUuid(), entity.getTemplateId(), oldEntity.getTemplateId());
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
    public ResultMsg updateVirtualMonitorRecord(OperationMonitorView view) throws IOException {
        ResultMsg msg = new ResultMsg();
        List<CvkId> cvkIds = genCvkIdsObject(view.getCvkIds());
        //先修改cas
        CasMonitorEntity oldEntity = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.CAS.value()), CasMonitorEntity.class);
        CasMonitorEntity operationMonitorEntity = updateCasCommonField(view, oldEntity);
        boolean updateCas = dao.insertMonitorRecord(operationMonitorEntity, MonitorEnum.LightTypeEnum.CAS.value());
        if (updateCas && !oldEntity.getTemplateId().equals(operationMonitorEntity.getTemplateId())) {
            //更新告警模板
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(operationMonitorEntity.getUuid(), operationMonitorEntity.getTemplateId(), oldEntity.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.CAS.value(), operationMonitorEntity.getTemplateId(), updateAlertRule);
            }
            //修改cas下的资源
            CasTransExporterModel casTransExporterModel = new CasTransExporterModel();

            casTransExporterModel.setIp(view.getIp());
            casTransExporterModel.setPort(view.getPort());
            casTransExporterModel.setUsername(view.getUserName());
            casTransExporterModel.setPassword(view.getPassword());

            //不再管cas cluster
//            List<Cluster> clusterList = dao.getClusterListByExporter(casTransExporterModel);
//            Map<String, String> map = new HashMap<>();
//            map.put("parentId", operationMonitorEntity.getUuid());
//            map.put("parentName", view.getName());
//            map.put("rootId", operationMonitorEntity.getUuid());
//            map.put("rootName", view.getName());
//            Map<String, String> clusterIdMap = new HashMap<>();
//            clusterList.forEach(cluster -> {
//                if (cluster.isBeenAdd()) {
//                    //更新cluster
//                    try {
//                        OperationMonitorEntity oldCluster = dao.getMonitorRecordByUuid(cluster.getUuid());
//                        OperationMonitorEntity updateCluster = updateCasClusterField(view, oldCluster, cluster);
//                        updateCluster.setExtra(objectMapper.writeValueAsString(map));
//                        clusterIdMap.put(cluster.getClusterId(), updateCluster.getUuid());
//                        boolean updateClusterres = dao.insertMonitorRecord(updateCluster);
//                        if (updateClusterres) {
//                            //更新告警模板
//                            RuleMonitorEntity updateClusterAlertRule = configService.updateMonitorRecordAlertRule(updateCluster.getUuid(), updateCluster.getTemplateId());
//                            if (null != updateClusterAlertRule) {
//                                configService.addAlertTemplateToEtcd(updateCluster.getLightTypeId(), updateCluster.getTemplateId(), updateClusterAlertRule);
//                            }
//                        }
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    //新插入cluster
//                    addCasCluster(view, cluster, map, clusterIdMap);
//                }
//            });
            //更新cvk vm
            List<CvkAndVmView> cvkVmMonitorList = dao.getAllCvkAndVmByCasuuid(view.getUuid());
            List<String> cvkMonitroList = new ArrayList<>();
            List<String> vmMonitorList = new ArrayList<>();
            cvkVmMonitorList.forEach(x -> {
                cvkMonitroList.add(x.getHostMonitor().getUuid());
                x.getVmMonitorList().forEach(y -> {
                    vmMonitorList.add(y.getUuid());
                });
            });

            List<Host> hostList = dao.getCvkAndVmListByExporter(casTransExporterModel);
            switch (view.getRadioType()) {
                case MONITOR_1:
                    //只修改或增加cvk
                    hostList.forEach(host -> {
                        //如果有集群，则parentId为集群Id，否则为casUuid
                        if (host.isBeenAdd()) {
                            int index = cvkMonitroList.indexOf(host.getUuid());
                            cvkMonitroList.remove(index);
                            //修改cvk
                            HostMonitorEntity oldHost = null;
                            try {
                                oldHost = objectMapper.readValue(dao.getMonitorRecordByUuid(host.getUuid(), MonitorEnum.LightTypeEnum.CVK.value()), HostMonitorEntity.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            updateHost(host, view, operationMonitorEntity.getUuid(), oldHost);
                        } else {
                            //添加cvk
                            addHost(host, view, operationMonitorEntity.getUuid());
                        }


                    });

                    break;
                case MONITOR_2:
                    //监控所有cvk和vm
                    hostList.forEach(host -> {
                        String reHostId = "";
                        //如果有集群，则parentId为集群Id，否则为casUuid
                        if (host.isBeenAdd()) {
                            int index = cvkMonitroList.indexOf(host.getUuid());
                            cvkMonitroList.remove(index);
                            //修改cvk
                            HostMonitorEntity oldHost = null;
                            try {
                                oldHost = objectMapper.readValue(dao.getMonitorRecordByUuid(host.getUuid(), MonitorEnum.MonitorTypeEnum.CVK.value()), HostMonitorEntity.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            reHostId = updateHost(host, view, operationMonitorEntity.getUuid(), oldHost);

                        } else {
                            //添加cvk
                            reHostId = addHost(host, view, operationMonitorEntity.getUuid());
                        }
                        if (!reHostId.equals("")) {
                            String finalInserthostt = reHostId;
                            host.getVirtualMachineList().forEach(vm -> {
                                if (vm.isBeenAdd()) {
                                    int index = vmMonitorList.indexOf(vm.getUuid());
                                    vmMonitorList.remove(index);
                                    //修改vm
                                    VmMonitorEntity oldVm = null;
                                    try {
                                        oldVm = objectMapper.readValue(dao.getMonitorRecordByUuid(vm.getUuid(), MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value()), VmMonitorEntity.class);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    updateVm(vm, host, view, finalInserthostt, oldVm);
                                } else {
                                    addVm(vm, view, finalInserthostt);
                                }
                            });
                        }
                    });
                    break;
                case MONITOR_3:
                    //监控指定的cvk和vm
                    cvkIds.forEach(transCvkId -> {
                        Optional<Host> optHost = hostList.stream().filter(x -> x.getId().equals(transCvkId.getCvkId())).findFirst();
                        if (optHost.isPresent()) {
                            String reHostId3 = "";
                            if (optHost.get().isBeenAdd()) {
                                int index = cvkMonitroList.indexOf(optHost.get().getUuid());
                                cvkMonitroList.remove(index);
                                //修改cvk
                                HostMonitorEntity oldHost = null;
                                try {
                                    oldHost = objectMapper.readValue(dao.getMonitorRecordByUuid(optHost.get().getUuid(), MonitorEnum.LightTypeEnum.CVK.value()), HostMonitorEntity.class);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                reHostId3 = updateHost(optHost.get(), view, operationMonitorEntity.getUuid(), oldHost);

                            } else {
                                //添加cvk
                                reHostId3 = addHost(optHost.get(), view, operationMonitorEntity.getUuid());
                            }

                            if (!reHostId3.equals("")) {
                                List<VirtualMachine> virtualMachines = optHost.get().getVirtualMachineList().stream().
                                        filter(v -> transCvkId.getVmIds().contains(v.getId())).collect(Collectors.toList());

                                String finalInsertCvkt3 = reHostId3;

                                virtualMachines.forEach(vm -> {
                                    if (vm.isBeenAdd()) {
                                        int index = vmMonitorList.indexOf(vm.getUuid());
                                        vmMonitorList.remove(index);
                                        //修改vm
                                        VmMonitorEntity oldVm = null;
                                        try {
                                            oldVm = objectMapper.readValue(dao.getMonitorRecordByUuid(vm.getUuid(), MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value()), VmMonitorEntity.class);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        updateVm(vm, optHost.get(), view, finalInsertCvkt3, oldVm);
                                    } else {
                                        addVm(vm, view, finalInsertCvkt3);
                                    }
                                });
                            }
                        }
                    });
                    break;
            }
            List<DelMonitorRecordView> delNodeMonitorRecordViews = new ArrayList<>();
            if (cvkMonitroList.size() > 0) {
                cvkMonitroList.forEach(x -> {
                    DelMonitorRecordView del = new DelMonitorRecordView();
                    del.setUuid(x);
                    del.setLightType(MonitorEnum.LightTypeEnum.CVK.value());
                    delNodeMonitorRecordViews.add(del);
                });

            }
            if (vmMonitorList.size() > 0) {
                vmMonitorList.forEach(x -> {
                    DelMonitorRecordView del = new DelMonitorRecordView();
                    del.setUuid(x);
                    del.setLightType(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                    delNodeMonitorRecordViews.add(del);
                });

            }
            delNetworkMonitorRecord(delNodeMonitorRecordViews);
        }

        msg.setCode(HttpStatus.OK.value());
        msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        return msg;
    }

    private CasMonitorEntity updateCasCommonField(OperationMonitorView view, CasMonitorEntity oldEntity) {
        CasMonitorEntity entity = new CasMonitorEntity();
        entity.setUuid(oldEntity.getUuid());
        entity.setIp(view.getIp());
        entity.setMonitorType(oldEntity.getMonitorType());
        entity.setName(view.getName());
        entity.setScrapeInterval(view.getTimeinterval());
        entity.setScrapeTimeout(view.getTimeout());
        entity.setTemplateId(view.getCasTemplate());
        entity.setPort(view.getPort());
        entity.setUsername(view.getUserName());
        entity.setPassword(view.getPassword());
        return entity;
    }

    private void updateVm(VirtualMachine vm, Host host, OperationMonitorView view, String cvkUuid, VmMonitorEntity oldVm) {
        VmMonitorEntity vmMonitor = null;

        try {

            vmMonitor = updateVmMonitorField(vm, view, oldVm);
            vmMonitor.setCvkUuid(cvkUuid);
            boolean updateVm = dao.insertMonitorRecord(vmMonitor, MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
            if (updateVm && !oldVm.getTemplateId().equals(vmMonitor.getTemplateId())) {
                //更新告警模板
                RuleMonitorEntity updateVmAlertRule = configService.updateMonitorRecordAlertRule(vmMonitor.getUuid(), vmMonitor.getTemplateId(), oldVm.getTemplateId());
                if (null != updateVmAlertRule) {
                    configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value(), vmMonitor.getTemplateId(), updateVmAlertRule);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private VmMonitorEntity updateVmMonitorField(VirtualMachine vm, OperationMonitorView view, VmMonitorEntity oldVm) throws JsonProcessingException {
        VmMonitorEntity vmoperationMonitorEntity = new VmMonitorEntity();
        vmoperationMonitorEntity.setUuid(oldVm.getUuid());
        vmoperationMonitorEntity.setIp(vm.getIp());
        vmoperationMonitorEntity.setName(vm.getName());
        vmoperationMonitorEntity.setMonitorType(oldVm.getMonitorType());
        vmoperationMonitorEntity.setTemplateId(view.getVmTemplate());
        vmoperationMonitorEntity.setScrapeInterval(view.getTimeinterval());
        vmoperationMonitorEntity.setScrapeTimeout(view.getTimeout());
        vmoperationMonitorEntity.setVmId(vm.getId());
        return vmoperationMonitorEntity;
    }

    private String updateHost(Host host, OperationMonitorView view, String casuuid, HostMonitorEntity oldHost) {
        try {
            HostMonitorEntity hostMonitor = updateCvkMonitorFiled(host, view, oldHost);
            hostMonitor.setCasUuid(casuuid);
            boolean updateCvkres = dao.insertMonitorRecord(hostMonitor, MonitorEnum.LightTypeEnum.CVK.value());
            if (updateCvkres && !oldHost.getTemplateId().equals(hostMonitor.getTemplateId())) {
                //更新告警模板
                RuleMonitorEntity updateCvkAlertRule = configService.updateMonitorRecordAlertRule(hostMonitor.getUuid(), hostMonitor.getTemplateId(), oldHost.getTemplateId());
                if (null != updateCvkAlertRule) {
                    configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.CVK.value(), hostMonitor.getTemplateId(), updateCvkAlertRule);
                    return hostMonitor.getUuid();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private HostMonitorEntity updateCvkMonitorFiled(Host host, OperationMonitorView view, HostMonitorEntity oldHost) throws JsonProcessingException {
        HostMonitorEntity hostMonitorEntity = new HostMonitorEntity();
        hostMonitorEntity.setUuid(oldHost.getUuid());
        hostMonitorEntity.setName(host.getName());
        hostMonitorEntity.setIp(host.getIp());
        hostMonitorEntity.setMonitorType(oldHost.getMonitorType());
        hostMonitorEntity.setTemplateId(view.getCvkTemplate());
        hostMonitorEntity.setScrapeInterval(view.getTimeinterval());
        hostMonitorEntity.setScrapeTimeout(view.getTimeout());
        hostMonitorEntity.setHostId(host.getId());
        hostMonitorEntity.setHostpoolId(host.getHostpoolId());
        return hostMonitorEntity;
    }

    @Override
    public ResultMsg updateContainerMonitorRecord(OperationMonitorView view) throws IOException {
        ResultMsg msg = new ResultMsg();
        List<String> containerIds = genk8sconObject(view.getContainerIds());
        K8sMonitorEntity oldK8s = objectMapper.readValue(dao.getMonitorRecordByUuid(view.getUuid(), MonitorEnum.LightTypeEnum.K8S.value()), K8sMonitorEntity.class);
        K8sMonitorEntity operationMonitorEntity = updateK8sCommonField(view, oldK8s);
        boolean updateK8s = dao.insertMonitorRecord(operationMonitorEntity, MonitorEnum.LightTypeEnum.K8S.value());
        if (updateK8s && !oldK8s.getTemplateId().equals(operationMonitorEntity.getTemplateId())) {
            RuleMonitorEntity updateAlertRule = configService.updateMonitorRecordAlertRule(operationMonitorEntity.getUuid(), operationMonitorEntity.getTemplateId(), oldK8s.getTemplateId());
            if (null != updateAlertRule) {
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.K8S.value(), operationMonitorEntity.getTemplateId(), updateAlertRule);
            }
        }
        List<Node> allNode = dao.getNodeListByExporter(view.getIp(), view.getApiPort());
        //node默认添加全部的
        Map<String, String> nodeMap = new HashMap<>();
        List<K8sNodeAndContainerView> k8sNodeAndContainerViewList = dao.getAllNodeAndContainerByK8suuid(view.getUuid());
        List<String> nodeMonitorList = new ArrayList<>();
        List<String> containerMonitorList = new ArrayList<>();
        if (k8sNodeAndContainerViewList.size() > 0) {
            k8sNodeAndContainerViewList.forEach(x -> {
                nodeMonitorList.add(x.getK8snode().getUuid());
                if (null != x.getK8sContainerList() && x.getK8sContainerList().size() > 0) {
                    x.getK8sContainerList().forEach(y -> {
                        containerMonitorList.add(y.getUuid());
                    });
                }
            });
        }

        allNode.forEach(node -> {
            if (node.isBeenAdd()) {
                //修改 node
                int index = nodeMonitorList.indexOf(node.getUuid());
                nodeMonitorList.remove(index);
                try {
                    K8snodeMonitorEntity oldK8sNode = objectMapper.readValue(dao.getMonitorRecordByUuid(node.getUuid(), MonitorEnum.LightTypeEnum.K8SNODE.value()), K8snodeMonitorEntity.class);
                    K8snodeMonitorEntity k8snNode = updateNodeMonitorField(view, node, oldK8sNode);
                    k8snNode.setK8sUuid(operationMonitorEntity.getUuid());
                    boolean updateK8sn = dao.insertMonitorRecord(k8snNode, MonitorEnum.LightTypeEnum.K8SNODE.value());
                    nodeMap.put(node.getNodeIp(), k8snNode.getUuid());
                    if (updateK8sn && !oldK8sNode.getTemplateId().equals(k8snNode.getTemplateId())) {
                        //更新告警模板
                        RuleMonitorEntity updateClusterAlertRule = configService.updateMonitorRecordAlertRule(k8snNode.getUuid(), k8snNode.getTemplateId(), oldK8sNode.getTemplateId());
                        if (null != updateClusterAlertRule) {
                            configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.K8SNODE.value(), k8snNode.getTemplateId(), updateClusterAlertRule);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //添加node
                addK8sNode(view, node, operationMonitorEntity.getUuid(), nodeMap);
            }
        });
        List<DelMonitorRecordView> delNodeMonitorRecordViews = new ArrayList<>();
        if (nodeMonitorList.size() > 0) {
            nodeMonitorList.forEach(x -> {
                DelMonitorRecordView del = new DelMonitorRecordView();
                del.setUuid(x);
                del.setLightType(MonitorEnum.LightTypeEnum.K8SNODE.value());
                delNodeMonitorRecordViews.add(del);
            });

        }

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
                        int index = containerMonitorList.indexOf(container.getUuid());
                        containerMonitorList.remove(index);
                        //修改container
                        K8scontainerMonitorEntity oldContainer = null;
                        try {
                            oldContainer = objectMapper.readValue(dao.getMonitorRecordByUuid(container.getUuid(), MonitorEnum.LightTypeEnum.K8SCONTAINER.value()), K8scontainerMonitorEntity.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateContainer(view, container, nodeMap, oldContainer);
                    } else {
                        //添加container
                        addContainer(view, container, nodeMap);
                    }
                });

                break;
            case MONITOR_3:
                //监控k8s和指定容器
                containerIds.forEach(cId -> {
                    Optional<Container> optCon = myContainerList.stream().filter(x -> x.getContainerId().equals(cId)).findFirst();
                    if (optCon.isPresent()) {
                        if (optCon.get().isBeenAdd()) {
                            int index = containerMonitorList.indexOf(optCon.get().getUuid());
                            containerMonitorList.remove(index);
                            //修改container
                            K8scontainerMonitorEntity oldContainer = null;
                            try {
                                oldContainer = objectMapper.readValue(dao.getMonitorRecordByUuid(optCon.get().getUuid(), MonitorEnum.LightTypeEnum.K8SCONTAINER.value()), K8scontainerMonitorEntity.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            updateContainer(view, optCon.get(), nodeMap, oldContainer);
                        } else {
                            //添加container
                            addContainer(view, optCon.get(), nodeMap);
                        }
                    }
                });
                break;
        }
        List<DelMonitorRecordView> delMonitorRecordViews = new ArrayList<>();
        if (containerMonitorList.size() > 0) {
            containerMonitorList.forEach(x -> {
                DelMonitorRecordView del = new DelMonitorRecordView();
                del.setUuid(x);
                del.setLightType(MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                delMonitorRecordViews.add(del);
            });
        }
        //先删除容器再删除节点，否则会有外键约束
        delNetworkMonitorRecord(delMonitorRecordViews);
        delNetworkMonitorRecord(delNodeMonitorRecordViews);
        msg.setCode(HttpStatus.OK.value());
        msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        return msg;
    }

    private K8sMonitorEntity updateK8sCommonField(OperationMonitorView view, K8sMonitorEntity oldK8s) {
        K8sMonitorEntity k8sMonitorEntity = new K8sMonitorEntity();
        k8sMonitorEntity.setUuid(oldK8s.getUuid());
        k8sMonitorEntity.setName(view.getName());
        k8sMonitorEntity.setIp(view.getIp());
        k8sMonitorEntity.setMonitorType(oldK8s.getMonitorType());
        k8sMonitorEntity.setTemplateId(view.getK8sTemplate());
        k8sMonitorEntity.setScrapeInterval(view.getTimeinterval());
        k8sMonitorEntity.setScrapeTimeout(view.getTimeout());
        k8sMonitorEntity.setApiPort(view.getApiPort());
        k8sMonitorEntity.setCadvisorPort(view.getCAdvisorPort());

        return k8sMonitorEntity;

    }

    @Override
    public int getMonitorCountByTemplateId(String uuid, String lightType) throws IOException {
        String response = dao.getMonitorRecordByTemplateId(uuid, lightType);
        if (lightType.equals(MonitorEnum.LightTypeEnum.SWITCH.value()) || lightType.equals(MonitorEnum.LightTypeEnum.ROUTER.value())
                || lightType.equals(MonitorEnum.LightTypeEnum.LB.value()) || lightType.equals(MonitorEnum.LightTypeEnum.FIREWALL.value())) {
            List<NetworkMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<NetworkMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.MYSQL.value())) {
            List<DBMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<DBMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.TOMCAT.value())) {
            List<TomcatMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<TomcatMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.CAS.value())) {
            List<CasMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<CasMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.CVK.value())) {
            List<HostMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<HostMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value())) {
            List<VmMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<VmMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8S.value())) {
            List<K8sMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<K8sMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8SNODE.value())) {
            List<K8snodeMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<K8snodeMonitorEntity>>() {
            });
            return list.size();
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8SCONTAINER.value())) {
            List<K8scontainerMonitorEntity> list = objectMapper.readValue(response, new TypeReference<List<K8scontainerMonitorEntity>>() {
            });
            return list.size();
        }
        return 1;
    }

    private void updateContainer(OperationMonitorView view, Container container, Map<String, String> nodeMap, K8scontainerMonitorEntity oldContainer) {

        try {
            K8scontainerMonitorEntity monitorContainer = updateContainerMonitorField(view, container, oldContainer);
            monitorContainer.setK8snodeUuid(nodeMap.getOrDefault(container.getNodeIp(), ""));
            boolean updateK8sc = dao.insertMonitorRecord(monitorContainer, MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
            if (updateK8sc && !oldContainer.getTemplateId().equals(monitorContainer.getTemplateId())) {
                RuleMonitorEntity updateContainerAlertRule = configService.updateMonitorRecordAlertRule(monitorContainer.getUuid(), monitorContainer.getTemplateId(), oldContainer.getTemplateId());
                if (null != updateContainerAlertRule) {
                    configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.K8SCONTAINER.value(), monitorContainer.getTemplateId(), updateContainerAlertRule);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private K8scontainerMonitorEntity updateContainerMonitorField(OperationMonitorView view, Container container, K8scontainerMonitorEntity oldContainer) throws JsonProcessingException {
        K8scontainerMonitorEntity k8scMonitorEntity = new K8scontainerMonitorEntity();
        k8scMonitorEntity.setUuid(oldContainer.getUuid());
        k8scMonitorEntity.setName(container.getContainerName());
        k8scMonitorEntity.setMonitorType(oldContainer.getMonitorType());
        k8scMonitorEntity.setTemplateId(view.getK8sContainerTemplate());
        k8scMonitorEntity.setScrapeInterval(view.getTimeinterval());
        k8scMonitorEntity.setScrapeTimeout(view.getTimeout());
        k8scMonitorEntity.setPod_name(container.getPodName());
        k8scMonitorEntity.setPod_namespace(container.getPodNamespace());
        k8scMonitorEntity.setContainer_id(container.getContainerId());
        return k8scMonitorEntity;
    }

    private void addK8sNode(OperationMonitorView view, Node node, String k8sUuid, Map<String, String> nodeMap) {
        try {
            K8snodeMonitorEntity k8snNode = setNodeMonitorField(view, node, k8sUuid);
            nodeMap.put(node.getNodeIp(), k8snNode.getUuid());
            boolean insertK8sn = dao.insertMonitorRecord(k8snNode, MonitorEnum.LightTypeEnum.K8SNODE.value());
            if (insertK8sn) {
                //添加监控对象的告警规则
                RuleMonitorEntity ruleMonitorEntity = configService.addMonitorRecordAlertRule(k8snNode.getUuid(), k8snNode.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.K8SNODE.value(), k8snNode.getTemplateId(), ruleMonitorEntity);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private K8snodeMonitorEntity updateNodeMonitorField(OperationMonitorView view, Node node, K8snodeMonitorEntity oldK8sNode) throws JsonProcessingException {
        K8snodeMonitorEntity k8snMonitorEntity = new K8snodeMonitorEntity();
        k8snMonitorEntity.setUuid(oldK8sNode.getUuid());
        k8snMonitorEntity.setName(node.getNodeName());
        k8snMonitorEntity.setIp(node.getNodeIp());
        k8snMonitorEntity.setMonitorType(oldK8sNode.getMonitorType());
        k8snMonitorEntity.setTemplateId(view.getK8sNodeTemplate());
        k8snMonitorEntity.setScrapeInterval(view.getTimeinterval());
        k8snMonitorEntity.setScrapeTimeout(view.getTimeout());
        return k8snMonitorEntity;
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
            boolean insertK8sc = dao.insertMonitorRecord(monitorContainer, MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
            if (insertK8sc) {
                //添加监控对象的告警规则
                RuleMonitorEntity ruleMonitorEntityC = configService.addMonitorRecordAlertRule(monitorContainer.getUuid(), monitorContainer.getTemplateId());
                //生成并下发etcd中的告警模板
                configService.addAlertTemplateToEtcd(MonitorEnum.LightTypeEnum.K8SCONTAINER.value(), monitorContainer.getTemplateId(), ruleMonitorEntityC);
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
        tomcatMonitor.forEach(x -> {
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            entity.setLightTypeId(MonitorEnum.LightTypeEnum.TOMCAT.value());
            allMonitor.add(entity);
        });
        List<DBMonitorEntity> dbMonitor = dao.getAllDbMonitorEntity();
        dbMonitor.forEach(x -> {
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            entity.setLightTypeId(MonitorEnum.LightTypeEnum.MYSQL.value());
            allMonitor.add(entity);
        });
        List<HostMonitorEntity> hostMonitor = dao.getAllHostMonitorEntity();
        hostMonitor.forEach(x -> {
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            entity.setLightTypeId(MonitorEnum.LightTypeEnum.CVK.value());
            allMonitor.add(entity);
        });
        List<VmMonitorEntity> vmMonitor = dao.getAllVmMonitorEntity();
        vmMonitor.forEach(x -> {
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            entity.setLightTypeId(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
            allMonitor.add(entity);
        });
        List<K8snodeMonitorEntity> k8snodeMonitor = dao.getAllK8snodeMonitorEntity();
        k8snodeMonitor.forEach(x -> {
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8SNODE.value());
            allMonitor.add(entity);
        });
        List<K8scontainerMonitorEntity> k8scontainerMOnitor = dao.getAllK8sContainerMonitorEntity();
        k8scontainerMOnitor.forEach(x -> {
            OperationMonitorEntity entity = new OperationMonitorEntity();
            BeanUtils.copyProperties(x, entity);
            entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
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
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.FIREWALL.value());
        try {
            return objectMapper.readValue(response, NetworkMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TomcatMonitorEntity getTomcatMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.TOMCAT.value());
        try {
            return objectMapper.readValue(response, TomcatMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DBMonitorEntity getDbMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.MYSQL.value());
        try {
            DBMonitorEntity dbmonitor =  objectMapper.readValue(response, DBMonitorEntity.class);
            MonitorTemplate template = configService.getTemplateByLightType(MonitorEnum.LightTypeEnum.MYSQL.value(),"");
            dbmonitor.setMonitorTemplate(template);
            return dbmonitor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CasMonitorEntity getCasMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.CAS.value());
        try {
            return objectMapper.readValue(response, CasMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HostMonitorEntity getHostMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.CVK.value());
        try {
            return objectMapper.readValue(response, HostMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VmMonitorEntity getVmMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
        try {
            return objectMapper.readValue(response, VmMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K8sMonitorEntity getK8sMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.K8S.value());
        try {
            K8sMonitorEntity k8sMonitor = objectMapper.readValue(response, K8sMonitorEntity.class);
            //  获取其下node和container的templateid
            List<K8sNodeAndContainerView> nodeandcontainer = dao.getAllNodeAndContainerByK8suuid(uuid);
            if (nodeandcontainer.get(0).getK8snode()!=null) {
                k8sMonitor.setK8sNTemplateId(nodeandcontainer.get(0).getK8snode().getTemplateId());
            }
            Optional<K8sNodeAndContainerView> cmonitor = nodeandcontainer.stream().filter(x->null!=x.getK8sContainerList() &&
                    x.getK8sContainerList().size()>0).findFirst();
            cmonitor.ifPresent(k8sNodeAndContainerView -> k8sMonitor.setK8scTemplateId(k8sNodeAndContainerView.getK8sContainerList().get(0).getTemplateId()));

            MonitorTemplate template = configService.getTemplateByLightType(MonitorEnum.LightTypeEnum.K8S.value(),"");
            k8sMonitor.setMonitorTemplate(template);
            return k8sMonitor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K8snodeMonitorEntity getK8snodeMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.K8SNODE.value());
        try {
            return objectMapper.readValue(response, K8snodeMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K8scontainerMonitorEntity getK8sContainerMonitorEntity(String uuid) {
        String response = dao.getMonitorRecordByUuid(uuid, MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
        try {
            return objectMapper.readValue(response, K8scontainerMonitorEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isMonitorRecordIpDup(String ip, String lightType) {
        return dao.isMonitorRecordIpDup(ip, lightType);
    }

    @Override
    public List<NetworkMonitorEntity> getAllNetworkMonitorEntity() {
        return dao.getAllNetworkMonitorEntity();
    }

    @Override
    public ResultMsg getBusMonitorListByPage(PageData page) throws JsonProcessingException {
        return ResCommon.getCommonResultMsg(dao.getBusMonitorListByPage(page));
    }

    @Override
    public ResultMsg getMonitorRecordList(String middle) {
        List<OperationMonitorEntity> allMonitor = new ArrayList<>();
        List<AlertRuleTemplateEntity> templateList = configService.getAllTemplateNo();

        if (middle.equals("all")) {
            List<NetworkMonitorEntity> networkMonitor = dao.getAllNetworkMonitorEntity();
            networkMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(x.getLightType());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.NETWORK_DEVICE.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.SNMP_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });

            List<TomcatMonitorEntity> tomcatMonitor = dao.getAllTomcatMonitorEntity();
            tomcatMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.TOMCAT.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.DATABASE.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.TOMCAT_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<DBMonitorEntity> dbMonitor = dao.getAllDbMonitorEntity();
            dbMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.MYSQL.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.DATABASE.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.MYSQL_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<CasMonitorEntity> casMonitor = dao.getAllCasMonitorEntity();
            casMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.CAS.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.CAS_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<HostMonitorEntity> hostMonitor = dao.getAllHostMonitorEntity();
            hostMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.CVK.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.HOST_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<VmMonitorEntity> vmMonitor = dao.getAllVmMonitorEntity();
            vmMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.VM_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<K8sMonitorEntity> k8sMonitor = dao.getAllK8sMonitorEntity();
            k8sMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8S.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.CONTAINER.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.K8S_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<K8snodeMonitorEntity> k8snodeMonitor = dao.getAllK8snodeMonitorEntity();
            k8snodeMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8SNODE.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.CONTAINER.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.K8S_NODE_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<K8scontainerMonitorEntity> k8scontainerMOnitor = dao.getAllK8sContainerMonitorEntity();
            k8scontainerMOnitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.CONTAINER.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.K8S_CONTAINER_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
        } else if (middle.equals(MonitorEnum.MiddleTypeEnum.NETWORK_DEVICE.value())) {
            List<NetworkMonitorEntity> networkMonitor = dao.getAllNetworkMonitorEntity();
            networkMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(x.getLightType());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.NETWORK_DEVICE.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.SNMP_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
        } else if (middle.equals(MonitorEnum.MiddleTypeEnum.DATABASE.value())) {
            List<DBMonitorEntity> dbMonitor = dao.getAllDbMonitorEntity();
            dbMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.MYSQL.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.DATABASE.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.MYSQL_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
        } else if (middle.equals(MonitorEnum.MiddleTypeEnum.MIDDLEWARE.value())) {
            List<TomcatMonitorEntity> tomcatMonitor = dao.getAllTomcatMonitorEntity();
            tomcatMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.TOMCAT.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.MIDDLEWARE.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.TOMCAT_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
        } else if (middle.equals(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value())) {
            List<CasMonitorEntity> casMonitor = dao.getAllCasMonitorEntity();
            casMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.CAS.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.CAS_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<HostMonitorEntity> hostMonitor = dao.getAllHostMonitorEntity();
            hostMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.CVK.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.HOST_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<VmMonitorEntity> vmMonitor = dao.getAllVmMonitorEntity();
            vmMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.VIRTUALIZATION.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.VM_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
        } else if (middle.equals(MonitorEnum.MiddleTypeEnum.CONTAINER.value())) {
            List<K8sMonitorEntity> k8sMonitor = dao.getAllK8sMonitorEntity();
            k8sMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8S.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.CONTAINER.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.K8S_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<K8snodeMonitorEntity> k8snodeMonitor = dao.getAllK8snodeMonitorEntity();
            k8snodeMonitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8SNODE.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.CONTAINER.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.K8S_NODE_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
            List<K8scontainerMonitorEntity> k8scontainerMOnitor = dao.getAllK8sContainerMonitorEntity();
            k8scontainerMOnitor.forEach(x -> {
                OperationMonitorEntity entity = new OperationMonitorEntity();
                BeanUtils.copyProperties(x, entity);
                entity.setLightTypeId(MonitorEnum.LightTypeEnum.K8SCONTAINER.value());
                entity.setMiddleType(MonitorEnum.MiddleTypeEnum.CONTAINER.value());
                Optional<AlertRuleTemplateEntity> temp = templateList.stream().filter(y -> y.getUuid().equals(x.getTemplateId())).findFirst();
                temp.ifPresent(alertRuleTemplateEntity -> entity.setTemplateName(alertRuleTemplateEntity.getTemplateName()));
                entity.setStatus(dao.getQuotaValue(x.getUuid(), MonitorEnum.QuotaMonitorStatusEnum.K8S_CONTAINER_MONITOR_STATUS.value()));
                allMonitor.add(entity);
            });
        }
        return ResCommon.getCommonResultMsg(allMonitor);
    }

}
