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
                    radioType:"1",
                    path: {
                        getTemplateByLightType: "/monitorConfig/getTemplateByLightType",
                        addVirtualMonitorRecord: "/monitor/addVirtualMonitorRecord"
                    },
                    casTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    cvkTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    vmTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    lightType: "",
                    tabSelected: "",
                },
                mounted: function () {
                    var light = sessionStorage.getItem('addLightType');
                    this.tabSelected = light;
                },
                methods: {

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
                            this.radioType="1",
                            this.infoCasMonitortemplate = '',
                            this.infoCvkMonitortemplate = '',
                            this.infoVmMonitortemplate = '',
                            this.casTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                            this.cvkTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                            this.vmTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}];
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
                                    data.cas.forEach(function (x) {
                                        _self.casTemplateList.push(x);
                                    });
                                    data.cvk.forEach(function (x) {
                                        _self.cvkTemplateList.push(x);
                                    });
                                    data.virtualMachine.forEach(function (x) {
                                        _self.vmTemplateList.push(x);
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
                        var formdata = new FormData($('#add-cas')[0]);
                        formdata.append("lightType", _self.lightType);
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url: _self.path.addVirtualMonitorRecord,
                            dataType: 'json',//预期服务器返回数据类型
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    commonModule.prompt("prompt.insertSuccess", data.msg);
                                } else {
                                    //弹出框 新建失败
                                    commonModule.prompt("prompt.insertError", "alert");
                                }
                                $("#addcas").modal('hide');
                            },
                            error: function () {
                                //处理异常，请重试
                                $("#addcas").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }
                        })
                    },

                }
            })


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
                        isIP: true
                    },
                    name: {
                        required: true
                    },
                    port: {
                        required: true
                    },
                    userName:{
                        required:true
                    },
                    password:{
                        required:true
                    },
                    timeinterval: {
                        required: true
                    },
                    timeout: {
                        required: true
                    },
                    casTemplate: {
                        required: true,
                    },
                    cvkTemplate: {
                        required: true,
                    },
                    vmTemplate: {
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
                    userName:{
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    password:{
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeinterval: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeout: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    casTemplate: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    cvkTemplate: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    vmTemplate: {
                        required: commonModule.i18n("validate.inputNotEmpty")
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
            var light = sessionStorage.getItem('addLightType');
            addCas.title = commonModule.i18n("modal.title.add" + light);
            addCas.tabSelected = light;
            addCas.initData(light);
        });
        $("#addcas").on("hide.bs.model", function (e) {
            $("#add-cas").resetForm();
        });
    }
    addcas();
    return {addcas: addcas}
})