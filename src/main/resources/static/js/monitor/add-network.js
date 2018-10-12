/**
 * Created by gy on 2018/10/12.
 */
'use strict';
define(['jquery','vue','commonModule'],function ($, Vue,commonModule) {
    var addnetwork = function () {
        if ($('#addnetwork')[0]) {
            var addNetwork = new Vue({
                el: '#addnetwork',
                data: {
                    title:"",
                    infoIp:'',
                    infoName:'',
                },
                mounted: function () {
                },
                methods: {
                    initData:function () {
                        console.log("init add net...")
                    },
                    clickResource:function (event,lighttype) {
                        var e = event.target;
                        $('#leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                    }
                }
            })
        }

        $("#addnetwork").on('show.bs.modal', function (event) {
            var light = sessionStorage.getItem('addLightType');
            addNetwork.title = commonModule.i18n("modal.title.add"+light);
            addNetwork.initData();
        })
    }
    addnetwork();
    return {addnetwork: addnetwork}
})