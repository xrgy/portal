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
                    }
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