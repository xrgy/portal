/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery','bootstrap', 'vue','commonModule'], function ($,bootstrap, Vue,commonModule) {
    var confList = function () {
        if ($('#configList')[0]) {
            var monitorList = new Vue({
                el: '#configList',
                data: {

                },
                mounted: function () {
                },
                methods: {
                    openConfigModal: function () {
                        $("#monitorConfig").modal({backdrop: 'static', keyboard: false, show: true})
                    }
                }
            })
        }


    }
    confList();
    return {confList: confList}
})