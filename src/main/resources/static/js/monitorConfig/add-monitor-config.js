/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue','commonModule'], function ($, Vue,commonModule) {
    var monitorConf = function () {
        if ($('#monitorConfig')[0]) {
            var monitorConfig = new Vue({
                el: '#monitorConfig',
                data: {
                    templateName: "cc",
                    snmpVersion:"snmpv2",
                    dataObj:{
                        quotaName:'',
                        quotaDesc:''
                    },
                    alertLevel:[
                        {text:'',value:'0'},//紧急
                        {text:'',value:'1'},//重要
                        {text:'',value:'2'},//次要
                        {text:'',value:'3'},//警告
                        {text:'',value:'4'}]//通知
                },
                filters: {
                    filterName:function(name) {

                        return "";

                    },
                    filterDesc:function(desc) {

                        return "";
                    }
                },
                mounted: function () {
                    console.log("add monitor display")
                },
                methods: {
                    initData: function () {
                        var _self =this;
                        console.log(commonModule.i18n("monitorConfig.templateName"));
                        $.ajax({
                            data:{"id":"111"},
                            url:"/getJPAInfo",
                            success: function (data) {
                                _self.templateName = data.name;
                            },
                            error: function () {

                            }
                        })
                    },
                    showMoreLine :function (event) {
                        var e = event.target;
                        //closet从当前元素开始向上查找，包括当前元素
                        $(e).closest('.span-more-line').addClass("hidden");
                        $(e).closest('.span-more-line').closest('div').next().removeClass('hidden');
                    },
                    showMoreCondition:function (event) {
                        var e = event.target;
                        $(e).addClass("hidden");
                        $(e).closest('div').next().removeClass('hidden')
                    },
                    //close 按钮没有做
                }

            })
            $("#monitorConfig").on('show.bs.modal',function () {
                monitorConfig.initData();
            })
        }

    }
    monitorConf();
    return {monitorConf: monitorConf}
})