/**
 * Created by gy on 2018/10/12.
 */
'use strict';
define(['jquery', 'vue', 'commonModule', 'validate-extend'], function ($, Vue, commonModule, validateExtend) {
    var addcas = function () {
        if ($('#addcas')[0]) {
            var addCas = new Vue({
                el: '#addcas',
                data: {
                    title: "",
                    infoIp: '',
                    infoName: '',
                    infoUsername: '',
                    infoPassword: '',
                    infoPort: '8080',
                    infoTimeinterval: '180',
                    infoTimeout: '175',
                    infoBbname: '',
                    infoCasMonitortemplate: '',
                    infoCvkMonitortemplate: '',
                    infoVmMonitortemplate: '',
                    radioType: "1",
                    cvkList: [],
                    cvkvmMap: {},
                    chosecvkList: {},
                    vmList: [],
                    path: {
                        getTemplateByLightType: "/monitorConfig/getTemplateByLightType",
                        addVirtualMonitorRecord: "/monitor/addVirtualMonitorRecord",
                        getCvkAndVmList: "/monitor/getCvkAndVmList",
                        getEditData: "/monitor/getCasMonitor",
                        updateVirtualMonitorRecord: "/monitor/updateVirtualMonitorRecord"
                    },
                    casTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    cvkTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    vmTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    lightType: "",
                    tabSelected: "",
                    monitorOpe:"",
                    monitorOpeUuid:""
                },
                mounted: function () {
                    var light = sessionStorage.getItem('addLightType');
                    this.tabSelected = light;
                },
                methods: {
                    clearCvkAndVm: function () {
                        this.cvkList = [];
                        this.cvkvmMap = {};
                        this.chosecvkList = {};
                        this.vmList = [];
                    },
                    getCvkAndVm: function () {
                        var _self = this;
                        $.ajax({
                            data: {
                                ip: _self.infoIp,
                                port: _self.infoPort,
                                username: _self.infoUsername,
                                password: _self.infoPassword
                            },
                            url: _self.path.getCvkAndVmList,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    data.forEach(function (x) {
                                        _self.cvkList.push(x);
                                    });
                                    for (var i = 0; i < data.length; i++) {
                                        _self.cvkvmMap[data[i].id] = data[i].vm;
                                    }

                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    addVm: function (event, vm) {
                        var _self = this;
                        var e = event.target;
                        //closet从当前元素开始向上查找，包括当前元素
                        var tr = $(e).closest('tr');
                        var cvkid = vm.cvkId;
                        if ($(tr).hasClass("tr-background")) {
                            //已选中 则删除选中 并从准备提交的vm列表中删除vmid
                            var l = _self.chosecvkList[cvkid];
                            for (var i = 0; i < l.length; i++) {
                                if (vm.id === l[i]) {
                                    _self.vmList.splice(i, 1);
                                    break;
                                }
                            }
                            $(tr).removeClass("tr-background");
                        } else {
                            $(tr).addClass("tr-background");
                            _self.chosecvkList[cvkid].push(vm.id);
                        }
                    },
                    choseHost: function (event, hostid) {
                        var _self = this;
                        var e = event.target;
                        //closet从当前元素开始向上查找，包括当前元素
                        var tr = $(e).closest('tr');
                        var tmpVm = _self.cvkvmMap[hostid];
                        if ($(tr).hasClass("tr-background")) {
                            //如果有某个class 行背景颜色 则是取消选中
                            //从vmList中去除这个map  hostId的vm
                            //并remove这个class
                            $(tr).removeClass("tr-background");
                            tmpVm.forEach(function (x) {
                                for (var i = 0; i < _self.vmList.length; i++) {
                                    if (x.id === _self.vmList[i].id) {
                                        _self.vmList.splice(i, 1);
                                        break;
                                    }
                                }

                            });
                            delete _self.chosecvkList[hostid];
                        } else {
                            //如果没有这个class 则添加这个host的vm 到vmList中
                            //并且添加这个class
                            tmpVm.forEach(function (x) {
                                _self.vmList.push(x);
                            });
                            $(tr).addClass("tr-background");
                            _self.chosecvkList[hostid] = [];
                        }
                    },

                    initForm: function () {
                        $("#add-cas").validate().resetForm();
                        $('.form-group').removeClass("has-error");
                        this.title = "",
                            this.infoIp = '',
                            this.infoName = '',
                            this.infoUsername = '',
                            this.infoPassword = '',
                            this.infoPort = '8080',
                            this.infoTimeinterval = '180',
                            this.infoTimeout = '175',
                            this.radioType = "1",
                            this.cvkList = [],
                            this.cvkvmMap = {},
                            this.chosecvkList = {},
                            this.vmList = [],
                            this.infoCasMonitortemplate = '',
                            this.infoCvkMonitortemplate = '',
                            this.infoVmMonitortemplate = '',
                            this.casTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                            this.cvkTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                            this.vmTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}];
                    },
                    initEditData: function (lightType) {
                        var _self = this;
                        _self.lightType = lightType;
                        $.ajax({
                            data: {uuid: _self.monitorOpeUuid},
                            url: _self.path.getEditData,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    _self.infoIp = data.ip;
                                    _self.infoName = data.name;
                                    _self.infoUsername = data.username;
                                    _self.infoPassword = data.password;
                                    _self.infoPort = data.port;
                                    _self.infoTimeinterval = data.scrapeInterval;
                                    _self.infoTimeout = data.scrapeTimeout;
                                    _self.infoCasMonitortemplate = data.templateId;
                                    _self.infoCvkMonitortemplate = data.hostTemplateId;
                                    if (data.vmTemplateId == null) {
                                        _self.infoVmMonitortemplate = '';
                                    } else {
                                        _self.infoVmMonitortemplate = data.vmTemplateId;
                                    }
                                    if (data.monitorTemplate.cas !== null) {
                                        data.monitorTemplate.cas.forEach(function (x) {
                                            _self.casTemplateList.push(x);
                                        });
                                    }
                                    if (data.monitorTemplate.cvk !== null) {
                                        data.monitorTemplate.cvk.forEach(function (x) {
                                            _self.cvkTemplateList.push(x);
                                        });
                                    }
                                    if (data.monitorTemplate.virtualMachine !== null) {
                                        data.monitorTemplate.virtualMachine.forEach(function (x) {
                                            _self.vmTemplateList.push(x);
                                        });
                                    }
                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    initData: function (lightType) {
                        var _self = this;
                        _self.lightType = lightType;
                        this.templateList = [{
                            uuid: '',
                            templateName: commonModule.i18n("form.select.default")
                        }];
                        $.ajax({
                            data: {lightType: lightType},
                            url: _self.path.getTemplateByLightType,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    if (data.cas !== null) {
                                        data.cas.forEach(function (x) {
                                            _self.casTemplateList.push(x);
                                        });
                                    }
                                    if (data.cvk !== null) {
                                        data.cvk.forEach(function (x) {
                                            _self.cvkTemplateList.push(x);
                                        });
                                    }
                                    if (data.virtualMachine !== null) {
                                        data.virtualMachine.forEach(function (x) {
                                            _self.vmTemplateList.push(x);
                                        });
                                    }

                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    clickResource: function (event, lighttype) {
                        var e = event.target;
                        $('#addcas #leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.initForm();
                        this.initData(lighttype);
                    },
                    //测试是否连通
                    canReach:function () {
                        // var _self = this;
                        // $.ajax({
                        //     data: {"ip": _self.infoIp,"username":_self.infoUsername,"password":_self.infoPassword,"databasename": _self.infoBbname,"port":_self.infoPort},
                        //     url: _self.path.dbCanAccess,
                        //     success: function (data) {
                        //         if (data.accessible === true) {
                        //             commonModule.prompt("prompt.accessSuccess","SUCCESS");
                        //         }else {
                                    commonModule.prompt("prompt.accessError","alert");
                                // }
                            // },
                            // error: function () {
                            // }
                        // })
                    },
                    submitForm: function () {
                        var _self = this;
                        var formdata = new FormData($('#add-cas')[0]);
                        formdata.append("lightType", _self.lightType);
                        formdata.append("cvkIds", JSON.stringify(_self.chosecvkList));
                        if (_self.monitorOpe == "edit"){
                            formdata.append("uuid", _self.monitorOpeUuid);
                        }
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url: _self.monitorOpe == "edit" ? _self.path.updateVirtualMonitorRecord : _self.path.addVirtualMonitorRecord,
                            dataType: 'json',//预期服务器返回数据类型
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    if (_self.monitorOpe == "edit"){
                                        commonModule.prompt("prompt.updateSuccess",data.msg);
                                    }else {
                                        commonModule.prompt("prompt.insertSuccess", data.msg);
                                    }
                                } else {
                                    //弹出框 新建失败
                                    if (_self.monitorOpe == "edit") {
                                        commonModule.prompt("prompt.updateError","alert");
                                    }else {
                                        commonModule.prompt("prompt.insertError", "alert");
                                    }
                                }
                                $("#addcas").modal('hide');
                            },
                            error: function () {
                                //处理异常，请重试
                                $("#addcas").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }
                        })
                    }
                }
            });
            $('#add-cas').validate({
                submitHandler: function () {
                    addCas.submitForm();
                },
                //失去焦点
                onfocusout: function (element, event) {
                    $(element).valid();
                },
                //在 keyup 时验证,默认为true
                onkeyup: function (element, event) {
                    // $(element).valid();
                    return false;
                },
                rules: {
                    ip: {
                        required: true,
                        isIP: true,
                        //remote接受的返回值只要true和false即可
                        remote: {
                            //验证ip是否重复
                            type: "get",
                            url: "/monitor/isMonitorRecordIpDup",
                            timeout: 6000,
                            data: {
                                ip: function () {
                                    return $('#casip').val();
                                },
                                lightType: function () {
                                    return addCas.lightType;
                                },
                                uuid: function () {
                                    return addCas.monitorOpeUuid;
                                },
                            }
                        },
                    },
                    name: {
                        required: true
                    },
                    port: {
                        required: true
                    },
                    userName: {
                        required: true
                    },
                    password: {
                        required: true
                    },
                    timeinterval: {
                        required: true
                    },
                    timeout: {
                        required: true
                    },
                    casTemplate: {
                        // required: true,
                    },
                    cvkTemplate: {
                        // required: true,
                    },
                    vmTemplate: {
                        // required: true,
                    }
                },
                messages: {
                    ip: {
                        required: commonModule.i18n("validate.inputNotEmpty"),
                        isIP: commonModule.i18n("validate.pleaseInputIPAddress"),
                        remote: commonModule.i18n("validate.ipDuplicate")
                    },
                    name: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    port: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    userName: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    password: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeinterval: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeout: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    casTemplate: {
                        // required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    cvkTemplate: {
                        // required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    vmTemplate: {
                        // required: commonModule.i18n("validate.inputNotEmpty")
                    }

                },
                errorElement: "span",
                errorPlacement: function (error, element) {
                    element.parent("div").addClass('has_feedback');
                    if (element.prop("type") === "checkbox") {
                        error.insertAfter(element.parent("label"));
                    } else {
                        error.insertAfter(element);
                    }
                },
                success: function (label, element) {
                    //要验证的元素通过验证后的动作
                },
                highlight: function (element, erroClass, validClass) {
                    $(element).parent("div").addClass("has-error").removeClass("has-success");
                },
                unhighlight: function (element, erroClass, validClass) {
                    $(element).parent("div").addClass("has-success").removeClass("has-error");
                }
            });

        }

        $("#addcas").on('show.bs.modal', function (event) {
            addCas.initForm();
            var light = "";
            addCas.monitorOpe = sessionStorage.getItem('monitorOpe');
            if (addCas.monitorOpe == "edit") {
                addCas.monitorOpeUuid = sessionStorage.getItem('monitorOpeObj');
                light = sessionStorage.getItem('editLightType');
                addCas.title = commonModule.i18n("modal.title.update" + light);
                addCas.initEditData(light);
            } else {
                var light = sessionStorage.getItem('addLightType');
                addCas.title = commonModule.i18n("modal.title.add" + light);
                addCas.initData(light);
            }
            addCas.tabSelected = light;
        });
        $("#addcas").on("hide.bs.model", function (e) {
            $("#add-cas").resetForm();
        });
    }
    addcas();
    return {addcas: addcas}
})