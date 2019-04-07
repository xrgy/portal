/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery','bootstrap', 'vue','commonModule','topoMain','editTopo','showBusiness','showAlert','monitorConfigList','monitorList'],
    function ($,bootstrap, Vue,commonModule,topoMain,editTopo,showBusiness,showAlert,monitorConfigList,monitorList) {
    var confList = function () {
        if ($('#configList')[0]) {
            var monitorList = new Vue({
                el: '#configList',
                data: {//value是三级规格

                    middleware:[{name: 'tomcat',value:"Tomcat",type:'middleware'}],
                    databasedevice:[{name: 'MySQL',value:"MySQL",type:'database'}],
                    virtualdevice:[{name: 'CAS',value:"CAS",type:'virtualization'},
                        {name: 'CVK',value:"CVK",type:'virtualization'},
                        {name: 'VirtualMachine',value:"VirtualMachine",type:'virtualization'}],
                    addvirtualdevice:[{name: 'CAS',value:"CAS",type:'virtualization'}],
                    container:[{name: 'kubernetes',value:"k8s",type:'container'},
                        {name: commonModule.i18n('monitor.containerdevice.k8snode'),value:"k8sNode",type:'container'},
                        {name: commonModule.i18n('monitor.containerdevice.k8scontainer'),value:"k8sContainer",type:'container'}],
                    addcontainer:[{name: 'kubernetes',value:"k8s",type:'container'}]
                },
                mounted: function () {
                },
                methods: {
                    openConfigModal: function () {
                        $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
                    },
                    loadMonitorList:function () {
                        window.open("/monitor/showMonitorList",'_parent');
                        monitorList.showMonitorList();
                    },

                    loadMonitorTemplate:function () {
                        window.open("/monitorConfig/showTemplateList",'_parent');
                        monitorConfigList.showTempList();
                    },
                    loadTopo:function () {
                        window.open("/topo/showWeaveTopo",'_parent');
                        topoMain.startTopo();

                    },
                    loadNetTopo:function () {
                        window.open("/topo/showNetTopo",'_parent');
                        editTopo.netTopo();

                    },
                    loadBusinessList:function () {
                        window.open("/business/showBusinessList",'_parent');
                        showBusiness.showBusiness();
                    },
                    loadAlert:function () {
                        window.open("/alert/showAlert",'_parent');
                        showAlert.showAlert();
                    }

                }
            })
        }


    }
    confList();
    return {confList: confList}
})