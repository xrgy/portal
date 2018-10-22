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
                        addNetworkMonitorRecord: "/monitor/addNetworkMonitorRecord"
                    },
                    templateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    lightType: "",
                    tabSelected:"",
                },
                mounted: function () {
                    var light = sessionStorage.getItem('addLightType');
                    this.tabSelected = light;
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
                                        if (_self.monitorMode == "snmp_v1"){
                                            data.snmp_v1.forEach(function (x) {
                                                _self.templateList.push(x);
                                            })
                                        }else if(_self.monitorMode == "snmp_v2c"){
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
                        var e = event.target;
                        $('#leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.initForm();
                        this.initData(lighttype);
                    },
                    getTemplateByMode:function (mode) {
                        this.monitorMode = mode;
                        this.initData(this.lightType);
                    },
                    submitForm: function () {
                        var _self = this;
                        var formdata = new FormData($('#add-network')[0]);
                        formdata.append("lightType", _self.lightType);
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url: _self.path.addNetworkMonitorRecord,
                            dataType: 'json',//预期服务器返回数据类型
                            success: function (data) {

                            },
                            error: function () {

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
                        isIP: true
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
                        isIP: commonModule.i18n("validate.pleaseInputIPAddress")
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
            var light = sessionStorage.getItem('addLightType');
            addNetwork.title = commonModule.i18n("modal.title.add" + light);
            addNetwork.tabSelected = light;
            addNetwork.initData(light);
        });
        $("#addnetwork").on("hide.bs.model", function (e) {
            $("#add-network").resetForm();
        });
    }
    addnetwork();
    return {addnetwork: addnetwork}
})