/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend'], function ($, Vue, commonModule, validateExtend) {
    var monitorConf = function () {
        if ($('#monitorConfig')[0]) {
            var monitorConfig = new Vue({
                el: '#monitorConfig',
                data: {
                    moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                    templateName: "",
                    monitorConfigTitle:"",
                    dataObj: [{
                        quotaName: 'm',
                        quotaDesc: ''
                    },
                        {
                            quotaName: 'n',
                            quotaDesc: ''
                        }],
                    typeName: [{
                        name: '1',
                        quotaDesc: ''
                    },
                        {
                            name: '2',
                            quotaDesc: ''
                        }],
                    alertLevel: [
                        {text: commonModule.i18n("alertLevel.critical"), value: "0"},//紧急
                        {text: commonModule.i18n("alertLevel.major"), value: "1"},//重要
                        {text: commonModule.i18n("alertLevel.minor"), value: "2"},//次要
                        {text: commonModule.i18n("alertLevel.warning"), value: "3"},//警告
                        {text: commonModule.i18n("alertLevel.notice"), value: "4"}],//通知
                    alertCondition: [
                        {text: ">", value: '0'},
                        {text: "=", value: '1'},
                        {text: "<", value: '2'},
                        {text: ">=", value: '3'},
                        {text: "<=", value: '4'},
                        {text: "!=", value: '5'}],
                    expressionMore: [
                        {text: "and", value: 'and'},
                        {text: "or", value: 'or'}],
                    path: {
                        getMetricInfo: "/monitorConfig/getMetricInfo",
                        addTemplate: "/monitorConfig/addTemplate",
                        openTemplateData: "/monitorConfig/OpenTemplate",
                        updateTemplate: "/monitorConfig/updateTemplate"
                    },
                    monitorMode: "snmp_v1",
                    lightType: "switch",
                    lightClass: "network",
                    availDataObj: null,
                    perfDataObj: null,
                    // resourceUuid:"",
                    myOpe: "",
                    editTemUuid: "",
                    oneshowMoreCondition: "0",
                    usedCount:0,
                },
                filters: {
                    convertType: function (type) {
                        if (type!="" && type != null) {
                            return commonModule.i18n("metric.type." + type);
                        } else {
                            return "";
                        }
                    },
                    convertName: function (name) {
                        if (name!="" && name != null) {
                            return commonModule.i18n("metric.name." + name);
                        } else {
                            return "";
                        }
                    },
                    convertDesc: function (desc) {
                        if (desc!="" && desc != null) {
                            return commonModule.i18n("metric.description." + desc);
                        } else {
                            return "";
                        }
                    }
                },
                mounted: function () {
                    console.log("add monitor display")
                },
                methods: {
                    initEditData: function () {
                        var _self = this;
                        $.ajax({
                            //"MySQL mysql"
                            data: {"uuid": _self.editTemUuid},
                            url: _self.path.openTemplateData,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    _self.templateName = data.template_name;
                                    _self.lightType = data.light_type;
                                    _self.usedCount=data.usedCount;
                                    _self.getEditLightClass(_self.lightType, data.monitor_mode);
                                    var available = data.available;
                                    var performance = data.performance;
                                    var availArray = [], perfArray = [];
                                    for (var i = 0; i < available.length; i++) {
                                        var avail = {};
                                        avail.uuid = available[i].uuid;
                                        avail.type = available[i].quota_info.metric_type;
                                        avail.data = available[i].quota_info;
                                        availArray.push(avail);
                                        // if (_self.resourceUuid === ""){
                                        //     _self.resourceUuid = available[availdata].metric_light_type_id;
                                        // }
                                    }
                                    _self.availDataObj = _self.groupByType(availArray);
                                    for (var i = 0; i < performance.length; i++) {
                                        var perf = {};
                                        perf.type = performance[i].quota_info.metric_type;
                                        perf.data = performance[i].quota_info;
                                        perfArray.push(perf);
                                    }
                                    _self.perfDataObj = _self.groupByType(perfArray);
                                    // console.log(_self.availDataObj);
                                    // console.log(_self.perfDataObj);
                                    _self.initEditTable();
                                }
                            },
                            error: function () {

                            }
                        })
                    },
                    initEditTable: function () {
                        var _self = this;
                        for (var i = 0; i < _self.availDataObj.length; i++) {
                            for (var j = 0; j < _self.availDataObj[i].data.length; j++) {
                                var item = _self.availDataObj[i].data[j];
                                if (null == item.severity) {
                                    item.severity = '0';
                                }
                            }
                        }
                        for (var t = 0; t < _self.perfDataObj.length; t++) {
                            for (var k = 0; k < _self.perfDataObj[t].data.length; k++) {
                                var item1 = _self.perfDataObj[t].data[k];
                                if (null == item1.level_one_severity) {
                                    item1.level_one_severity = '0';
                                }
                                if (null == item1.level_one_alert_first_condition) {
                                    item1.level_one_alert_first_condition = '0';
                                }
                                if (null == item1.level_one_expression_more) {
                                    item1.level_one_expression_more = 'and';
                                }
                                if (null == item1.level_one_alert_second_condition) {
                                    item1.level_one_alert_second_condition = '0';
                                }
                                if (null == item1.level_two_severity) {
                                    item1.level_two_severity = '1';
                                }
                                if (null == item1.level_two_alert_first_condition) {
                                    item1.level_two_alert_first_condition = '0';
                                }
                                if (null == item1.level_two_expression_more) {
                                    item1.level_two_expression_more = 'and';
                                }
                                if (null == item1.level_two_alert_second_condition) {
                                    item1.level_two_alert_second_condition = '0';
                                }

                            }
                        }
                    },
                    initData: function () {
                        var _self = this;
                        console.log(commonModule.i18n("monitorConfig.templateName"));
                        $.ajax({
                            //"MySQL mysql"
                            data: {"lightType": _self.lightType, "monitorMode": _self.monitorMode},
                            url: _self.path.getMetricInfo,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    var available = data.available;
                                    var performance = data.performance;
                                    var availArray = [], perfArray = [];
                                    for (var i = 0; i < available.length;i++) {
                                        var avail = {};
                                        avail.type = available[i].metric_type;
                                        avail.data = available[i];
                                        availArray.push(avail);
                                        // if (_self.resourceUuid === ""){
                                        //     _self.resourceUuid = available[availdata].metric_light_type_id;
                                        // }
                                    }
                                    _self.availDataObj = _self.groupByType(availArray);
                                    for (var i = 0; i < performance.length; i++) {
                                        var perf = {};
                                        perf.type = performance[i].metric_type;
                                        perf.data = performance[i];
                                        perfArray.push(perf);
                                    }
                                    _self.perfDataObj = _self.groupByType(perfArray);
                                    _self.initTable();
                                    // _self.templateName = data.name;
                                }
                            },
                            error: function () {

                            }
                        })
                    },
                    initTable: function () {
                        var _self = this;
                        for (var i in _self.availDataObj) {
                            for (var j in _self.availDataObj[i].data) {
                                var item = _self.availDataObj[i].data[j];
                                item.severity = '0';
                            }
                        }
                        for (var t in _self.perfDataObj) {
                            for (var k in _self.perfDataObj[t].data) {
                                var item1 = _self.perfDataObj[t].data[k];
                                item1.level_one_severity = '0';
                                item1.level_one_alert_first_condition = '0';
                                item1.level_one_expression_more = 'and';
                                item1.level_one_alert_second_condition = '0';
                                item1.level_two_severity = '1';
                                item1.level_two_alert_first_condition = '0';
                                item1.level_two_expression_more = 'and';
                                item1.level_two_alert_second_condition = '0';

                            }
                        }
                    },
                    groupByType: function (array) {
                        var map = {}, ret = [];
                        for (var i = 0; i < array.length; i++) {
                            var line = array[i];
                            if (!map[line.type]) {
                                ret.push({
                                    type: line.type,
                                    data: [line.data]
                                });
                                map[line.type] = ret;
                            } else {
                                for (var j = 0; j < ret.length; j++) {
                                    var typeArr = ret[j];
                                    if (line.type === typeArr.type) {
                                        typeArr.data.push(line.data);
                                        break;
                                    }
                                }
                            }
                        }
                        return ret;
                    },
                    getEditLightClass: function (light, monitor_mode) {
                        var _self = this;
                        _self.monitorMode = monitor_mode;
                        if (light === "switch" || light === "router" || light === "firewall" || light === "LB") {
                            _self.lightClass = "network";
                        } else {
                            _self.lightClass = "";
                        }
                    },
                    getLightClass: function (light) {
                        var _self = this;
                        if (light === "switch" || light === "router" || light === "firewall" || light === "LB") {
                            _self.monitorMode = "snmp_v1";
                            _self.lightClass = "network";
                        } else if (light === "Tomcat") {
                            _self.monitorMode = "jmx";
                            _self.lightClass = "";
                        } else if (light === "CVK" || light === "VirtualMachine") {
                            _self.monitorMode = "cas";
                            _self.lightClass = "";
                        } else if (light === "k8sNode" || light === "k8sContainer") {
                            _self.monitorMode = "k8s";
                            _self.lightClass = "";
                        } else {
                            _self.monitorMode = light.toLowerCase();
                            _self.lightClass = "";
                        }
                    },
                    //显示二级告警
                    showMoreLine: function (event) {
                        var e = event.target;
                        //closet从当前元素开始向上查找，包括当前元素
                        $(e).closest('.span-more-line').addClass("hidden");
                        $(e).closest('.span-more-line').closest('div').next().removeClass('hidden');
                    },
                    //显示更多条件
                    showMoreCondition: function (event) {
                        var e = event.target;
                        $(e).addClass("hidden");
                        $(e).parent().next().removeClass('hidden');
                    },
                    //close按钮
                    closeCondition: function (event) {
                        var e = event.target;
                        if ($(e).parent().prev('div').length !== 0) {
                            $(e).parent().addClass("hidden");
                            $(e).parent().prev('div').children('.span-more-condition').removeClass('hidden');
                        } else {
                            $(e).parent().next().addClass('hidden');
                            $(e).closest('div').children('.span-more-condition').removeClass('hidden');
                            $(e).parent().parent().prev('div').children('.span-more-line').removeClass('hidden');
                            $(e).parent().parent().addClass('hidden');
                        }
                    },
                    toggleType: function (event) {
                        var e = event.target;
                        $(e).toggleClass('fa-angle-down fa-angle-up');
                        var start = $(e).parent().parent().attr('id');
                        $(e).parent().parent().siblings("tr[id^=" + start + "]").toggleClass('hidden')
                        // $($(e).parent().parent().siblings("tr[id^="+start+"]")).slideToggle();

                    },
                    toggleMoitorMode: function (str) {
                        var _self = this;
                        if (_self.myOpe == "edit" && _self.usedCount!=0){
                            //不允许修改
                            return
                        }
                        _self.monitorMode = str;
                        console.log("monitorMode:" + _self.monitorMode);
                        _self.initData();
                    },
                    //新建模板提交
                    submitForm: function () {
                        var _self = this;
                        var data = {};
                        if (_self.myOpe == "edit") {
                            data.uuid = _self.editTemUuid;
                        }
                        data.template_name = _self.templateName;
                        data.monitor_mode = _self.monitorMode;
                        data.template_type = 1;
                        data.light_type = _self.lightType;

                        data.available = _self.availDataObj;
                        data.performance = _self.perfDataObj;

                        $.ajax({
                            type: "post",
                            data: {templateData: JSON.stringify(data)},
                            url: _self.myOpe == "edit" ? _self.path.updateTemplate : _self.path.addTemplate,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    if (_self.myOpe == "edit") {
                                        commonModule.prompt("prompt.updateSuccess", data.msg);
                                    } else {
                                        commonModule.prompt("prompt.insertSuccess", data.msg);
                                    }
                                } else {
                                    //弹出框 新建失败
                                    if (_self.myOpe == "edit") {
                                        commonModule.prompt("prompt.updateError", "alert");
                                    }else {
                                        commonModule.prompt("prompt.insertError", "alert");
                                    }
                                }
                                $("#monitorConfig").modal('hide');

                            },
                            error: function () {
                                //处理异常，请重试
                                $("#monitorConfig").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }

                        })

                    }
                }

            });
            $("#add-template").validate({
                submitHandler: function () {
                    monitorConfig.submitForm();
                },
                //失去焦点
                onfocusout: function (element, event) {
                    $(element).valid();
                },
                ignore: ".ignore",
                //在 keyup 时验证,默认为true
                onkeyup: function (element, event) {
                    // $(element).valid();
                    return false;
                },
                rules: {
                    tempName: {
                        required: true,
                        //remote接受的返回值只要true和false即可
                        remote: {
                            //验证名称是否重复
                            type: "post",
                            url: "/monitorConfig/isTemplateNameDup",
                            timeout: 6000,
                            data: {
                                name: function () {
                                    return $('#tempName').val();
                                },
                                templateUUid: function () {
                                    return monitorConfig.editTemUuid;
                                }
                            }
                        }
                    }
                },
                messages: {
                    tempName: {
                        required: commonModule.i18n("validate.inputNotEmpty"),
                        remote: commonModule.i18n("validate.templateNameDuplicate")
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
        $("#monitorConfig").on('show.bs.modal', function () {
            monitorConfig.myOpe = sessionStorage.getItem('templateOpe');

            if (monitorConfig.myOpe == "edit") {
                monitorConfig.editTemUuid = sessionStorage.getItem('templateOpeObj');
                monitorConfig.monitorConfigTitle=commonModule.i18n("monitorConfig.title.updateTemplateName");
                monitorConfig.initEditData();
            } else {
                var light = sessionStorage.getItem('addConfigLightType');
                monitorConfig.lightType = light;
                monitorConfig.getLightClass(light);
                monitorConfig.monitorConfigTitle=commonModule.i18n("monitorConfig.title.addTemplateName");
                monitorConfig.initData();
            }

        })
    }
    monitorConf();
    return {monitorConf: monitorConf}
})