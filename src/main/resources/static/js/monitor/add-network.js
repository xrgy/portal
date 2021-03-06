/**
 * Created by gy on 2018/10/12.
 */
'use strict';
define(['jquery', 'vue', 'commonModule', 'validate-extend'], function ($, Vue, commonModule, validateExtend) {
    var addnetwork = function () {
        if ($('#addnetwork')[0]) {
            var addNetwork = new Vue({
                el: '#addnetwork',
                data: {
                    title: "",
                    infoIp: '',
                    infoName: '',
                    monitorMode: 'snmp_v1',
                    infoReadcommunity: '',
                    infoWritecommunity: '',
                    infoPort: '161',
                    infoTimeinterval: '180',
                    infoTimeout: '175',
                    infoMonitortemplate: '',
                    path: {
                        getTemplateByLightType: "/monitorConfig/getTemplateByLightType",
                        addNetworkMonitorRecord: "/monitor/addNetworkMonitorRecord",
                        getEditData: "/monitor/getNetworkMonitor",
                        updateNetworkMonitorRecord: "/monitor/updateNetworkMonitorRecord",
                    },
                    templateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    lightType: "",
                    netTabSelected: "",
                    monitorOpe:"",
                    monitorOpeUuid:""
                },
                mounted: function () {
                    var light = sessionStorage.getItem('addLightType');
                    this.netTabSelected = light;
                },
                methods: {

                    initForm: function () {
                        $("#add-network").validate().resetForm();
                        $('.form-group').removeClass("has-error");
                        this.title = "",
                            this.infoIp = '',
                            this.infoName = '',
                            this.monitorMode = 'snmp_v1',
                            this.infoReadcommunity = '',
                            this.infoWritecommunity = '',
                            this.infoPort = '161',
                            this.infoTimeinterval = '180',
                            this.infoTimeout = '175',
                            this.infoMonitortemplate = '',
                            this.templateList = [{
                                uuid: '',
                                templateName: commonModule.i18n("form.select.default")
                            }];
                    },
                    initEditData: function (lightType) {
                        var _self = this;
                        _self.lightType = lightType;
                        this.templateList = [{
                            uuid: '',
                            templateName: commonModule.i18n("form.select.default")
                        }];
                        this.netTabSelected = lightType;
                        $.ajax({
                            data: {uuid: _self.monitorOpeUuid, lightType: _self.lightType},
                            url: _self.path.getEditData,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    _self.infoIp = data.ip;
                                    _self.infoName = data.name;
                                    if (data.snmpVersion==1){
                                        _self.monitorMode="snmp_v1";
                                    }else if (data.snmpVersion==2){
                                        _self.monitorMode="snmp_v2c";
                                    }
                                    _self.infoReadcommunity = data.readCommunity;
                                    _self.infoPort = data.port;
                                    _self.infoTimeinterval = data.scrapeInterval;
                                    _self.infoTimeout = data.scrapeTimeout;
                                    _self.infoMonitortemplate = data.templateId;
                                    if (_self.monitorMode == "snmp_v1") {
                                        data.monitorTemplate.snmp_v1.forEach(function (x) {
                                            _self.templateList.push(x);
                                        })
                                    } else if (_self.monitorMode == "snmp_v2c") {
                                        data.monitorTemplate.snmp_v2.forEach(function (x) {
                                            _self.templateList.push(x);
                                        })
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
                                    if (_self.monitorMode == "snmp_v1") {
                                        data.snmp_v1.forEach(function (x) {
                                            _self.templateList.push(x);
                                        })
                                    } else if (_self.monitorMode == "snmp_v2c") {
                                        data.snmp_v2.forEach(function (x) {
                                            _self.templateList.push(x);
                                        })
                                    }
                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    clickResource: function (event, lighttype) {
                        if (this.monitorOpe == "edit") {
                            return
                        }
                        var e = event.target;
                        $('#addnetwork #leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.initForm();
                        this.title = commonModule.i18n("modal.title.add" + lighttype);
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
                    getTemplateByMode: function (mode) {
                        this.monitorMode = mode;
                        this.initData(this.lightType);
                    },
                    submitForm: function () {
                        var _self = this;
                        var formdata = new FormData($('#add-network')[0]);
                        if (_self.monitorOpe == "edit") {
                            formdata.append("uuid", _self.monitorOpeUuid);
                        }
                        formdata.append("lightType", _self.lightType);
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url: _self.monitorOpe == "edit" ? _self.path.updateNetworkMonitorRecord :_self.path.addNetworkMonitorRecord,
                            dataType: 'json',//预期服务器返回数据类型
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    if (_self.monitorOpe == "edit") {
                                        commonModule.prompt("prompt.updateSuccess", data.msg);
                                    } else {
                                        commonModule.prompt("prompt.insertSuccess", data.msg);
                                    }
                                } else {
                                    //弹出框 新建失败
                                    if (_self.monitorOpe == "edit") {
                                        commonModule.prompt("prompt.updateError", "alert");
                                    } else {
                                        commonModule.prompt("prompt.insertError", "alert");
                                    }
                                }
                                $("#addnetwork").modal('hide');
                            },
                            error: function () {
                                //处理异常，请重试
                                $("#addnetwork").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }
                        })
                    },

                }
            })


            $('#add-network').validate({
                submitHandler: function () {
                    addNetwork.submitForm();
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
                                    return $('#ip').val();
                                },
                                lightType: function () {
                                    return addNetwork.lightType;
                                },
                                uuid: function () {
                                    return addNetwork.monitorOpeUuid;
                                },
                            }
                        },
                    },
                    name: {
                        required: true
                    },
                    readcommunity: {
                        required: true
                    },
                    port: {
                        required: true
                    },
                    timeinterval: {
                        required: true
                    },
                    timeout: {
                        required: true
                    },
                    monitortemplate: {
                        required: true,
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
                    readcommunity: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    port: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeinterval: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeout: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    monitortemplate: {
                        required: commonModule.i18n("validate.inputNotEmpty"),


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

        $("#addnetwork").on('show.bs.modal', function (event) {
            addNetwork.initForm();
            var light = "";
            addNetwork.monitorOpe = sessionStorage.getItem('monitorOpe');
            if (addNetwork.monitorOpe == "edit") {
                addNetwork.monitorOpeUuid = sessionStorage.getItem('monitorOpeObj');
                light = sessionStorage.getItem('editLightType');
                if (light!=addNetwork.netTabSelected){
                    $('#addnetwork #leftMenu').find('li').removeClass('active');
                }
                addNetwork.title = commonModule.i18n("modal.title.update" + light);
                addNetwork.netTabSelected = light;
                addNetwork.initEditData(light);
            } else {
                $('#addnetwork #leftMenu').find('li').removeClass('active');
                light = sessionStorage.getItem('addLightType');
                addNetwork.title = commonModule.i18n("modal.title.add" + light);
                addNetwork.netTabSelected = light;
                addNetwork.initData(light);
            }
        });
        $("#addnetwork").on("hide.bs.model", function (e) {
            $("#add-network").resetForm();
        });
    }
    addnetwork();
    return {addnetwork: addnetwork}
})