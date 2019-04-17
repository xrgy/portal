/**
 * Created by gy on 2018/10/12.
 */
'use strict';
define(['jquery', 'vue', 'commonModule', 'validate-extend'], function ($, Vue, commonModule, validateExtend) {
    var addtomcat = function () {
        if ($('#addtomcat')[0]) {
            var addTomcat = new Vue({
                el: '#addtomcat',
                data: {
                    title: "",
                    infoIp: '',
                    infoName: '',
                    infoUsername: '',
                    infoPassword: '',
                    infoPort: '3306',
                    infoTimeinterval: '180',
                    infoTimeout: '175',
                    infoMonitortemplate: '',
                    path: {
                        getTemplateByLightType: "/monitorConfig/getTemplateByLightType",
                        addMiddleWareMonitorRecord: "/monitor/addMiddleWareMonitorRecord",
                        getEditData: "/monitor/getTomcatMonitor",
                        updateMiddleWareMonitorRecord: "/monitor/updateMiddleMonitorRecord",
                        tomcatCanAccess:"/monitor/tomcatCanAccess",


                    },
                    templateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    lightType: "",
                    tabSelected: "",
                    checkedSSL: false,
                    monitorOpe: "",
                    monitorOpeUuid: ""
                },
                mounted: function () {
                    var light = sessionStorage.getItem('addLightType');
                    this.tabSelected = light;
                },
                methods: {

                    initForm: function () {
                        $("#add-tomcat").validate().resetForm();
                        $('.form-group').removeClass("has-error");
                        this.title = "",
                            this.infoIp = '',
                            this.infoName = '',
                            this.infoUsername = '',
                            this.infoPassword = '',
                            this.infoPort = '3306',
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
                        $.ajax({
                            data: {uuid: _self.monitorOpeUuid},
                            url: _self.path.getEditData,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    _self.infoIp = data.ip;
                                    _self.infoName = data.name;
                                    if (data.authentication==1){
                                        _self.checkedSSL=true;
                                        _self.infoUsername = data.username;
                                        _self.infoPassword = data.password;
                                    }else {
                                        _self.checkedSSL=false;
                                        _self.infoUsername = "";
                                        _self.infoPassword = "";
                                    }
                                    _self.infoPort = data.port;
                                    _self.infoTimeinterval = data.scrapeInterval;
                                    _self.infoTimeout = data.scrapeTimeout;
                                    _self.infoMonitortemplate = data.templateId;
                                    data.monitorTemplate.other.forEach(function (x) {
                                        _self.templateList.push(x);
                                    });
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
                                    data.other.forEach(function (x) {
                                        _self.templateList.push(x);
                                    });
                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    clickResource: function (event, lighttype) {
                        var e = event.target;
                        $('#addtomcat #leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.initForm();
                        this.initData(lighttype);
                    },
                    //测试是否连通
                    canReach:function () {
                        var _self = this;
                        $.ajax({
                            data: {"ip": _self.infoIp,"username":_self.infoUsername,"password":_self.infoPassword,"authentication": _self.checkedSSL,"port":_self.infoPort},
                            url: _self.path.tomcatCanAccess,
                            success: function (data) {
                                if (data.accessible === true) {
                                    commonModule.prompt("prompt.accessSuccess","SUCCESS");
                                }else {
                                    commonModule.prompt("prompt.accessError","alert");
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    submitForm: function () {
                        var _self = this;
                        var formdata = new FormData($('#add-tomcat')[0]);
                        if (_self.monitorOpe == "edit") {
                            formdata.append("uuid", _self.monitorOpeUuid);
                        }
                        formdata.append("lightType", _self.lightType);
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url: _self.monitorOpe == "edit" ? _self.path.updateMiddleWareMonitorRecord : _self.path.addMiddleWareMonitorRecord,
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
                                $("#addtomcat").modal('hide');
                            },
                            error: function () {
                                //处理异常，请重试
                                $("#addtomcat").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }
                        })
                    },

                }
            })


            $('#add-tomcat').validate({
                submitHandler: function () {
                    addTomcat.submitForm();
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
                        isIP: true
                    },
                    name: {
                        required: true
                    },
                    userName: {
                        required: true
                    },
                    password: {
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
                        isIP: commonModule.i18n("validate.pleaseInputIPAddress")
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

        $("#addtomcat").on('show.bs.modal', function (event) {
            addTomcat.initForm();
            var light = "";
            addTomcat.monitorOpe = sessionStorage.getItem('monitorOpe');
            if (addTomcat.monitorOpe == "edit") {
                addTomcat.monitorOpeUuid = sessionStorage.getItem('monitorOpeObj');
                light = sessionStorage.getItem('editLightType');
                addTomcat.title = commonModule.i18n("modal.title.update" + light);
                addTomcat.initEditData(light);
            } else {
                light = sessionStorage.getItem('addLightType');
                addTomcat.title = commonModule.i18n("modal.title.add" + light);
                addTomcat.initData(light);
            }
            addTomcat.tabSelected = light;
        });
        $("#addtomcat").on("hide.bs.model", function (e) {
            $("#add-tomcat").resetForm();
        });
    }
    addtomcat();
    return {addtomcat: addtomcat}
})