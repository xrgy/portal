/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend', 'topoMain'], function ($, Vue, commonModule, validateExtend, topoMain) {
    var showTempList = function () {
        if ($('#monitorConifgList')[0]) {
            var showBus = new Vue({
                    el: '#monitorConifgList',
                    data: {
                        moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                        templateName: "cc",
                        templateList: [],
                        path: {
                            getTemplateList: "/monitorConfig/getAllTemplate",
                            delTemplate: "/monitorConfig/delTemplate"
                        },
                        pageNum: 1,
                        pageSize: 10,
                        pageNumList: [],
                        totalPage: 0,
                        currenPageInfo: "",
                        totalRecord: 0,
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
                        toggleConfigList: function (event) {
                            var e = event.currentTarget;
                            $(e).toggleClass('fa-angle-down fa-angle-up');
                            $('#config-list').toggleClass('hidden');
                        },
                        addDeviceConfig: function (lighttype) {
                            sessionStorage.setItem('addConfigLightType', lighttype);
                            sessionStorage.setItem('templateOpe', "add");
                            $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
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
                        delResource: function (event, temId) {
                            var e = event.currentTarget;
                            var canNT = $(e).hasClass("canNotTrash");
                            if (!canNT) {
                                var _self = this;
                                var temList = [];
                                temList.push(temId);
                                _self.actionDel(JSON.stringify(temList));
                            }else {
                                commonModule.prompt("prompt.template.notDel","alert");
                            }
                        },
                        editResource: function (event, temId) {
                            var _self = this;
                            sessionStorage.setItem('templateOpe', "edit");
                            sessionStorage.setItem('templateOpeObj', temId);
                            $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
                        },
                        delList: function () {
                            var _self = this;
                            var temList = [];
                            $("input[name='needDel']:checkbox").each(function () {
                                if ($(this).prop("checked")) {
                                    if (!$(this).hasClass("cnTrash")){
                                        temList.push($(this).val());
                                    }
                                }
                            });
                            if (temList.length>0){
                                this.actionDel(JSON.stringify(temList))
                            }else {
                                commonModule.prompt("prompt.template.notDel","alert");
                            }
                        },
                        actionDel: function (templateListStr) {
                            var _self = this;
                            $.ajax({
                                data: {"templateUuids": templateListStr},
                                url: _self.path.delTemplate,
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
                            //其实是
                            this.tabSelected = lighttype;
                            this.pageNum = 1;
                            this.pageSize = 10;
                            this.pageNumList = [];
                            this.totalPage = 0;
                            this.currenPageInfo = "";
                            this.totalRecord = 0;
                            this.initTable(lighttype);
                        },
                        initTable: function (type) {
                            var _self = this;
                            _self.type = type;
                            _self.templateList = [];
                            _self.pageNumList = [];
                            $.ajax({
                                data: {
                                    "type": type,
                                    "pageIndex": parseInt(_self.pageNum),
                                    "pageSize": parseInt(_self.pageSize)
                                },
                                url: _self.path.getTemplateList,
                                success: function (data) {
                                    if (data.msg == "SUCCESS") {
                                        var data = data.data;
                                        _self.totalRecord = data.totalRecord;
                                        _self.totalPage = data.totalPage;
                                        for (var i = 1; i <= _self.totalPage; i++) {
                                            _self.pageNumList.push(i);
                                        }
                                        data.list.forEach(function (x) {
                                            _self.templateList.push(x);
                                        });
                                        _self.currenPageInfo = "共" + _self.totalRecord + "条记录 , 当前第" + _self.pageNum + "/" + _self.totalPage + "页"
                                    }
                                },
                                error: function () {
                                }
                            })
                        }
                        ,

                        //下一步
                        next: function () {
                            var _self = this;
                            if (_self.pageNum < _self.totalPage) {
                                _self.pageNum = _self.pageNum + 1;
                                this.initTable(_self.type);
                            }
                        }
                        ,

                        //上一步
                        previous: function () {
                            var _self = this;
                            if (_self.pageNum > 1) {
                                _self.pageNum = _self.pageNum - 1;
                                this.initTable(_self.type);
                            }
                        }
                        ,
                        //修改每页显示条数时，要从第一页开始查起
                        research: function () {
                            var _self = this;
                            _self.pageNum = 1;
                            this.initTable(_self.type);
                        }
                        ,
                        search: function () {
                            this.initTable(this.type);
                        }
                        ,
                    }

                })
            ;
        }
    }
    showTempList();
    return {showTempList: showTempList}
})