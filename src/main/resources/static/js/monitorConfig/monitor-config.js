/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery','bootstrap', 'vue','commonModule'], function ($,bootstrap, Vue,commonModule) {
    var confList = function () {
        if ($('#configList')[0]) {
            var monitorList = new Vue({
                el: '#configList',
                data: {
                    networkdevice:[{name: commonModule.i18n('monitor.networkdevice.switch'),value:"switch",type:'network_device'},
                        {name: commonModule.i18n('monitor.networkdevice.router'),value:"router",type:'network_device'},
                        {name: commonModule.i18n('monitor.networkdevice.firewall'),value:"firewall",type:'network_device'},
                        {name: commonModule.i18n('monitor.networkdevice.LB'),value:"LB",type:'network_device'}],
                    middleware:[{name: 'tomcat',value:"Tomcat",type:'middleware'}],
                    databasedevice:[{name: 'MySQL',value:"MySQL",type:'database'}],
                    virtualdevice:[{name: 'CAS',value:"CAS",type:'virtualization'},{name: 'vCenter',value:"vCenter",type:'virtualization'}],
                    container:[{name: 'kubernetes',value:"k8s",type:'container'}]
                },
                mounted: function () {
                },
                methods: {
                    openConfigModal: function () {
                        $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
                    },
                    toggleDeviceList:function (event) {
                      var  e = event.currentTarget;
                      $(e).toggleClass('fa-angle-down fa-angle-up');
                      $('#device-list').toggleClass('hidden');
                    },
                    toggleConfigList:function (event) {
                        var  e = event.currentTarget;
                        $(e).toggleClass('fa-angle-down fa-angle-up');
                        $('#config-list').toggleClass('hidden');
                    },
                    addDevice:function (middletype,lighttype) {
                        sessionStorage.setItem('addLightType',lighttype);
                        switch(middletype){
                            case 'network_device':
                                $('#addnetwork').modal({backdrop:'static',keyboard:false,show:true});
                                break;
                            case 'middleware':
                                break;
                            case 'database':
                                $('#adddb').modal({backdrop:'static',keyboard:false,show:true});
                                break;
                            case 'virtualization':
                                break;
                            case 'container':
                                break;
                        }
                    },
                    addDeviceConfig:function (lighttype) {
                        sessionStorage.setItem('addConfigLightType',lighttype);
                        $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
                    }
                }
            })
        }


    }
    confList();
    return {confList: confList}
})