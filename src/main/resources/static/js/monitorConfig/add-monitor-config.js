/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule'], function ($, Vue, commonModule) {
    var monitorConf = function () {
        if ($('#monitorConfig')[0]) {
            var monitorConfig = new Vue({
                el: '#monitorConfig',
                data: {
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
                        {text: '', value: '0'},//紧急
                        {text: '', value: '1'},//重要
                        {text: '', value: '2'},//次要
                        {text: '', value: '3'},//警告
                        {text: '', value: '4'}]//通知
                },
                filters: {
                    filterName: function (name) {

                        return name;

                    },
                    filterDesc: function (desc) {

                        return name;
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
                            data: {"id": "111"},
                            url: "/getJPAInfo",
                            success: function (data) {
                                _self.templateName = data.name;
                            },
                            error: function () {

                            }
                        })
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
                    toggleType:function (event) {
                        var e = event.target;
                        $(e).toggleClass('fa-angle-down fa-angle-up');
                        var start = $(e).parent().parent().attr('id');
                        $(e).parent().parent().siblings("tr[id^="+start+"]").toggleClass('hidden')
                        // $($(e).parent().parent().siblings("tr[id^="+start+"]")).slideToggle();

                    }
                }

            })
            $("#monitorConfig").on('show.bs.modal', function () {
                monitorConfig.initData();
            })
        }

    }
    monitorConf();
    return {monitorConf: monitorConf}
})