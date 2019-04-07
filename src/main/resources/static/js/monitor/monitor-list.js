/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend', 'topoMain'], function ($, Vue, commonModule, validateExtend, topoMain) {
    var showMonitorList = function () {
        if ($('#monitorList')[0]) {
            var showBus = new Vue({
                    el: '#monitorList',
                    data: {
                        moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                        templateName: "cc",
                        templateList: [],
                        path: {
                            getMonitorList: "/monitor/getMonitorRecordList",
                            delMonitorRecord: "/monitor/delNetworkMonitorRecord"
                        },
                        pageNum: 1,
                        pageSize: 10,
                        pageNumList: [],
                        totalPage: 0,
                        currenPageInfo: "",
                        totalRecord: 0,
                        monitorEntityList: [],
                        showMonitorList: [],
                        type: "all",
                        tabSelected: "all",
                        networkdevice: [{
                            name: commonModule.i18n('monitor.networkdevice.switch'),
                            value: "switch",
                            type: 'network_device'
                        },
                            {
                                name: commonModule.i18n('monitor.networkdevice.router'),
                                value: "router",
                                type: 'network_device'
                            },
                            {
                                name: commonModule.i18n('monitor.networkdevice.firewall'),
                                value: "firewall",
                                type: 'network_device'
                            },
                            {name: commonModule.i18n('monitor.networkdevice.LB'), value: "LB", type: 'network_device'}],
                        middleware: [{name: 'tomcat', value: "Tomcat", type: 'middleware'}],
                        databasedevice: [{name: 'MySQL', value: "MySQL", type: 'database'}],
                        virtualdevice: [{name: 'CAS', value: "CAS", type: 'virtualization'},
                            {name: 'CVK', value: "CVK", type: 'virtualization'},
                            {name: 'VirtualMachine', value: "VirtualMachine", type: 'virtualization'}],
                        addvirtualdevice: [{name: 'CAS', value: "CAS", type: 'virtualization'}],
                        container: [{name: 'kubernetes', value: "k8s", type: 'container'},
                            {
                                name: commonModule.i18n('monitor.containerdevice.k8snode'),
                                value: "k8sNode",
                                type: 'container'
                            },
                            {
                                name: commonModule.i18n('monitor.containerdevice.k8scontainer'),
                                value: "k8sContainer",
                                type: 'container'
                            }],
                        addcontainer: [{name: 'kubernetes', value: "k8s", type: 'container'}]
                    },
                    filters: {
                        converMonitorStatus: function (middle) {
                            if (middle == "1") {
                                return commonModule.i18n("monitor.baseStatus.canReach");
                            } else {
                                return commonModule.i18n("monitor.baseStatus.notReach");
                            }
                        },
                        convertLightType: function (type) {
                            if (type != null) {
                                if (type === "VirtualMachine") {
                                    return commonModule.i18n("monitor.lightType.VirtualMachine");
                                } else if (type === "k8sContainer") {
                                    return "Docker";
                                } else if (type === "router" || type === "switch" || type === "LB" || type === "firewall") {
                                    return commonModule.i18n('monitor.networkdevice.' + type);
                                } else {
                                    return type;
                                }
                            } else {
                                return "";
                            }
                        }
                    },
                    mounted: function () {

                        this.initTable(this.type);
                    },
                    methods: {
                        toggleDeviceList: function (event) {
                            var e = event.currentTarget;
                            $(e).toggleClass('fa-angle-down fa-angle-up');
                            $('#device-list').toggleClass('hidden');
                        },

                        addDevice: function (middletype, lighttype) {
                            sessionStorage.setItem('addLightType', lighttype);
                            sessionStorage.setItem('monitorOpe', 'add');
                            switch (middletype) {
                                case 'network_device':
                                    $('#addnetwork').modal({backdrop: 'static', keyboard: false, show: true});
                                    break;
                                case 'middleware':
                                    $('#addtomcat').modal({backdrop: 'static', keyboard: false, show: true});
                                    break;
                                case 'database':
                                    $('#adddb').modal({backdrop: 'static', keyboard: false, show: true});
                                    break;
                                case 'virtualization':
                                    $('#addcas').modal({backdrop: 'static', keyboard: false, show: true});
                                    break;
                                case 'container':
                                    $('#addk8s').modal({backdrop: 'static', keyboard: false, show: true});
                                    break;
                            }
                        },
                        checkAllDel: function (event) {
                            var _self = this;
                            if ($("#allDel").prop("checked")) {
                                $("input[name='needDel']:checkbox").each(function () {
                                    $(this).prop("checked", true);
                                });
                            } else {
                                $("input[name='needDel']:checkbox").each(function () {
                                    $(this).prop("checked", false);
                                });
                            }

                        },
                        delResource: function (event, tem) {
                            var e = event.currentTarget;
                            var _self = this;
                            var temList = [];
                            temList.push({uuid: tem.uuid, lightType: tem.lightTypeId});
                            _self.actionDel(JSON.stringify(temList));
                        },
                        editResource: function (event, tem) {
                            var _self = this;
                            var e = event.currentTarget;
                            var canNT = $(e).hasClass("canNotTrash");
                            if (!canNT) {
                                //为true表示不能编辑
                                sessionStorage.setItem('monitorOpe', "edit");
                                sessionStorage.setItem('monitorOpeObj', tem.uuid);
                                sessionStorage.setItem('editLightType', tem.lightTypeId);
                                switch (tem.middleType) {
                                    case 'network_device':
                                        $('#addnetwork').modal({backdrop: 'static', keyboard: false, show: true});
                                        break;
                                    case 'middleware':
                                        $('#addtomcat').modal({backdrop: 'static', keyboard: false, show: true});
                                        break;
                                    case 'database':
                                        $('#adddb').modal({backdrop: 'static', keyboard: false, show: true});
                                        break;
                                    case 'virtualization':
                                        $('#addcas').modal({backdrop: 'static', keyboard: false, show: true});
                                        break;
                                    case 'container':
                                        $('#addk8s').modal({backdrop: 'static', keyboard: false, show: true});
                                        break;
                                }
                            }
                        },
                        delList: function () {
                            var _self = this;
                            var temList = [];
                            $("input[name='needDel']:checkbox").each(function () {
                                if ($(this).prop("checked")) {
                                    var id = $(this).val();
                                    var light = $(this).closest("tr").find('span').eq(3).attr("value");
                                    temList.push({uuid: id, lightType: light});
                                }
                            });
                            this.actionDel(JSON.stringify(temList));
                        },
                        actionDel: function (delStr) {
                            var _self = this;
                            $.ajax({
                                data: {"delInfo": delStr},
                                url: _self.path.delMonitorRecord,
                                success: function (data) {
                                    if (data.msg === "SUCCESS") {
                                        _self.initTable(_self.type);
                                    }
                                },
                                error: function () {
                                }
                            })
                        },
                        clickResource: function (event, lighttype) {
                            var e = event.target;
                            $('#leftMenu').find('li').removeClass('active');
                            $(e).closest('li').addClass('active');
                            //其实是middle
                            this.tabSelected = lighttype;
                            this.initTable(lighttype);
                        },
                        initTable: function (type) {
                            var _self = this;
                            _self.type = type;
                            _self.pageNumList = [];
                            _self.monitorEntityList = [];
                            $.ajax({
                                data: {
                                    "middle": type,
                                },
                                url: _self.path.getMonitorList,
                                success: function (data) {
                                    if (data.msg == "SUCCESS") {
                                        var data = data.data;
                                        _self.totalRecord = data.length;
                                        if (_self.totalRecord % _self.pageSize == 0) {
                                            _self.totalPage = Math.floor(_self.totalRecord / _self.pageSize);
                                        } else {
                                            _self.totalPage = Math.floor(_self.totalRecord / _self.pageSize) + 1;
                                        }
                                        for (var i = 1; i <= _self.totalPage; i++) {
                                            _self.pageNumList.push(i);
                                        }
                                        var i = 0;
                                        data.forEach(function (x) {
                                            _self.monitorEntityList.push(x);
                                        });
                                        _self.reloadMonitorList();
                                    }
                                },
                                error: function () {
                                }
                            })
                        },
                        showTemplateInfo: function (event, temId) {
                            var _self = this;
                            sessionStorage.setItem('templateOpe', "edit");
                            sessionStorage.setItem('templateOpeObj', temId);
                            $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
                        },
                        reloadMonitorList: function () {
                            var _self = this;
                            _self.showMonitorList = [];
                            var startIndex = (_self.pageNum - 1) * parseInt(_self.pageSize);
                            for (var i = 0; i < parseInt(_self.pageSize); i++) {
                                if (i + startIndex >= _self.monitorEntityList.length) {
                                    break;
                                }
                                _self.showMonitorList.push(_self.monitorEntityList[i + startIndex]);
                            }
                            _self.currenPageInfo = "共" + _self.totalRecord + "条记录 , 当前第" + _self.pageNum + "/" + _self.totalPage + "页"
                        },
                        //下一步
                        next: function () {
                            var _self = this;
                            if (_self.pageNum < _self.totalPage) {
                                _self.pageNum = _self.pageNum + 1;
                                _self.reloadMonitorList();
                            }
                        },

                        //上一步
                        previous: function () {
                            var _self = this;
                            if (_self.pageNum > 1) {
                                _self.pageNum = _self.pageNum - 1;
                                _self.reloadMonitorList();
                            }
                        },
                        //修改每页显示条数时，要从第一页开始查起
                        research: function () {
                            var _self = this;
                            _self.pageNum = 1;
                            _self.reloadMonitorList();
                        },
                        search: function () {
                            this.reloadMonitorList();
                        },
                        refresh: function () {
                            this.initTable(this.type);
                        }
                    }

                })
            ;
        }
    }
    showMonitorList();
    return {showMonitorList: showMonitorList}
})