/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue'], function ($, Vue) {
    var monitorConf = function () {
        if ($('#monitorConfig')[0]) {
            var monitorConfig = new Vue({
                el: '#monitorConfig',
                data: {
                    templateName: "cc",
                },
                mounted: function () {

                }

            })
        }
    }
    monitorConf();
    return {monitorConf: monitorConf}
})