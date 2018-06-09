/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule','validate-extend'], function ($, Vue, commonModule,validateExtend) {
    var monitorConf = function () {
        if ($('#monitorConfig')[0]) {
            var monitorConfig = new Vue({
                el: '#monitorConfig',
                data: {
                    moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                    templateName: "cc",
                    snmpVersion: "snmpv2",
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
                        {text: commonModule.i18n("alertLevel.critical"), value: '0'},//紧急
                        {text: commonModule.i18n("alertLevel.major"), value: '1'},//重要
                        {text: commonModule.i18n("alertLevel.minor"), value: '2'},//次要
                        {text: commonModule.i18n("alertLevel.warning"), value: '3'},//警告
                        {text: commonModule.i18n("alertLevel.notice"), value: '4'}],//通知
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
                        getMetricInfo: "/monitorConfig/getMetricInfo"
                    },
                    monitorMode: "snmp_v1",
                    lightType: "switch",
                    availDataObj: null,
                    perfDataObj: null
                },
                filters: {
                    convertType: function (type) {
                        if (type != null) {
                            return commonModule.i18n("metric.type." + type);
                        } else {
                            return "";
                        }
                    },
                    convertName: function (name) {
                        if (name != null) {
                            return commonModule.i18n("metric.name." + name);
                        } else {
                            return "";
                        }
                    },
                    convertDesc: function (desc) {
                        if (desc != null) {
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
                    initData: function () {
                        var _self = this;
                        console.log(commonModule.i18n("monitorConfig.templateName"));
                        $.ajax({
                            data: {"lightType": _self.lightType, "monitorMode": _self.monitorMode},
                            url: _self.path.getMetricInfo,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    var available = data.available;
                                    var performance = data.performance;
                                    var availArray = [], perfArray = [];
                                    for (var availdata in available) {
                                        var avail = {};
                                        avail.type = available[availdata].type_name;
                                        avail.data = available[availdata];
                                        availArray.push(avail);
                                    }
                                    _self.availDataObj = _self.groupByType(availArray);
                                    for (var perfdata in performance) {
                                        var perf = {};
                                        perf.type = performance[perfdata].type_name;
                                        perf.data = performance[perfdata];
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
                        for(var i in _self.availDataObj){
                            for(var j in _self.availDataObj[i].data){
                                var item = _self.availDataObj[i].data[j];
                                item.severity='0';
                            }
                        }
                        for(var t in _self.perfDataObj){
                            for(var k in _self.perfDataObj[t].data){
                                var item1 = _self.perfDataObj[t].data[k];
                                item1.level_one_severity='0';
                                item1.level_one_alert_first_condition='0';
                                item1.level_one_expression_more='and';
                                item1.level_one_alert_second_condition='0';
                                item1.level_two_severity='1';
                                item1.level_two_alert_first_condition='0';
                                item1.level_two_expression_more='and';
                                item1.level_two_alert_second_condition='0';

                            }
                        }
                    },
                    groupByType: function (array) {
                        var map = {}, ret = [];
                        for (var i in array) {
                            var line = array[i];
                            if (!map[line.type]) {
                                ret.push({
                                    type: line.type,
                                    data: [line.data]
                                });
                                map[line.type] = ret;
                            } else {
                                for (var j in ret) {
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
                    //新建模板提交
                    submitForm:function () {


                    }
                }
            });
            $("#add-template").validate({
                submitHandler: function () {
                    monitorConfig.submitForm();
                },
                //失去焦点
                onfocusout: function (element,event) {
                    $(element).valid();
                },
                ignore:".ignore",
                //在 keyup 时验证,默认为true
                onkeyup:function (element,event) {
                    // $(element).valid();
                    return false;
                },
                rules:{
                    tempName:{
                        required:true,
                        //remote接受的返回值只要true和false即可
                        remote:{
                            //验证名称是否重复
                            type:"post",
                            url:"/monitorConfig/isTemplateNameDup",
                            timeout:6000,
                            data:{
                                name:function () {
                                    return $('#tempName').val();
                            }}
                        }
                    }
                },
                messages:{
                    tempName: {
                        required: commonModule.i18n("validate.inputNotEmpty"),
                        remote: commonModule.i18n("validate.templateNameDuplicate")
                    }
                },
                errorElement:"span",
                errorPlacement:function (error,element) {
                    element.parent("div").addClass('has_feedback');
                    if (element.prop("type") === "checkbox"){
                        error.insertAfter(element.parent("label"));
                    }else {
                        error.insertAfter(element);
                    }
                },
                success: function (label,element) {
                    //要验证的元素通过验证后的动作
                },
                highlight: function (element,erroClass,validClass) {
                    $(element).parent("div").addClass("has-error").removeClass("has-success");
                },
                unhighlight: function (element,erroClass,validClass) {
                    $(element).parent("div").addClass("has-success").removeClass("has-error");
                }
            });

        }
        $("#monitorConfig").on('show.bs.modal', function () {
            monitorConfig.initData();
        })
    }
    monitorConf();
    return {monitorConf: monitorConf}
})