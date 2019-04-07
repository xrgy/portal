/**
 * Created by gy on 2018/10/12.
 */
'use strict';
define(['jquery', 'vue', 'commonModule', 'validate-extend'], function ($, Vue, commonModule, validateExtend) {
    var adddb = function () {
        if ($('#adddb')[0]) {
            var addDb = new Vue({
                el: '#adddb',
                data: {
                    title: "",
                    infoIp: '',
                    infoName: '',
                    infoUsername: '',
                    infoPassword: '',
                    infoPort: '3306',
                    infoTimeinterval: '180',
                    infoTimeout: '175',
                    infoBbname: '',
                    infoMonitortemplate: '',
                    path: {
                        getTemplateByLightType: "/monitorConfig/getTemplateByLightType",
                        addDBMonitorRecord: "/monitor/addDbMonitorRecord",
                        getEditData:"/monitor/getDbMonitor",
                        updateDBMonitorRecord:"/monitor/updateDbMonitorRecord"
                    },
                    templateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
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

                    initForm: function () {
                        $("#add-db").validate().resetForm();
                        $('.form-group').removeClass("has-error");
                        this.title = "",
                            this.infoIp = '',
                            this.infoName = '',
                            this.infoUsername = '',
                            this.infoPassword = '',
                            this.infoPort = '3306',
                            this.infoTimeinterval = '180',
                            this.infoTimeout = '175',
                            this.infoBbname = '',
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
                                    _self.infoUsername = data.username;
                                    _self.infoPassword = data.password;
                                    _self.infoPort = data.port;
                                    _self.infoTimeinterval = data.scrapeInterval;
                                    _self.infoTimeout = data.scrapeTimeout;
                                    _self.infoBbname = data.databasename;
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
                        $('#leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.initForm();
                        this.initData(lighttype);
                    },
                    submitForm: function () {
                        var _self = this;
                        var formdata = new FormData($('#add-db')[0]);
                        if (_self.monitorOpe == "edit"){
                            formdata.append("uuid", _self.monitorOpeUuid);
                        }
                        formdata.append("lightType", _self.lightType);
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url:  _self.monitorOpe == "edit" ? _self.path.updateDBMonitorRecord :_self.path.addDBMonitorRecord,
                            dataType: 'json',//预期服务器返回数据类型
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    if (_self.monitorOpe == "edit"){
                                        commonModule.prompt("prompt.updateSuccess",data.msg);
                                    }else {
                                        commonModule.prompt("prompt.insertSuccess",data.msg);
                                    }
                                }else {
                                    //弹出框 新建失败
                                    if (_self.monitorOpe == "edit") {
                                        commonModule.prompt("prompt.updateError","alert");
                                    }else {
                                        commonModule.prompt("prompt.insertError","alert");
                                    }
                                }
                                $("#adddb").modal('hide');
                            },
                            error: function () {
                                //处理异常，请重试
                                $("#adddb").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain","alert");
                            }
                        })
                    },

                }
            })


            $('#add-db').validate({
                submitHandler: function () {
                    addDb.submitForm();
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
                    userName:{
                        required:true
                    },
                    password:{
                      required:true
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
                    databaseName:{
                        required:true
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
                    userName:{
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    password:{
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

        $("#adddb").on('show.bs.modal', function (event) {
            addDb.initForm();
            var light="";
            addDb.monitorOpe=sessionStorage.getItem('monitorOpe');
            if (addDb.monitorOpe=="edit"){
                addDb.monitorOpeUuid=sessionStorage.getItem('monitorOpeObj');
                light=sessionStorage.getItem('editLightType');
                addDb.title = commonModule.i18n("modal.title.update" + light);
                addDb.initEditData(light);
            }else {
                light = sessionStorage.getItem('addLightType');
                addDb.title = commonModule.i18n("modal.title.add" + light);
                addDb.initData(light);
            }
            addDb.tabSelected = light;
        });
        $("#adddb").on("hide.bs.model", function (e) {
            $("#add-db").resetForm();
        });
    }
    adddb();
    return {adddb: adddb}
})