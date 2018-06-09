/**
 * Created by gy on 2018/3/24.
 */
'use strict'
require.config({
    // baseUrl: '/static/js/lib',
    //默认目录与main.js在同一目录
    paths:{
        'jquery': './lib/jquery/jquery-3.3.1.min',
        'vue': './lib/vue/vue',
        'vueRouter': './lib/vue/vue-router',
        'pjax':'./lib/jquery/jquery.pjax',
        'bootstrap':'./lib/bootstrap/bootstrap.min',
        'jqueryi18n': './lib/jquery/jquery.i18n.properties',
        'jquery-validate':'./lib/jquery/jquery-validate',


        /**自定义js**/
        'addMonitorConfig':'./monitorConfig/add-monitor-config',
        'monitorConfig':'./monitorConfig/monitor-config',
        'pjaxConfig':'./pjax-config',
        'commonModule':'./common-module'
    },
    shim:{
        'jqueryi18n':['jquery'],
        'jquery-validate':['jquery'],
        'vueRouter':['jquery','vue'],
        'pjax':['jquery'],
        'bootstrap':['jquery']
    }

})