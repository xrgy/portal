/**
 * Created by gy on 2018/3/24.
 */
'use strict';
require.config({
    // baseUrl: '/static/js/lib',
    //默认目录与main.js在同一目录
    paths:{
        'jquery': './lib/jquery/jquery-3.3.1.min',
        'vue': './lib/vue/vue',
        'vueRouter': './lib/vue/vue-router',
        'pjax':'./lib/jquery/jquery.pjax',
        'bootstrap':'./lib/bootstrap/bootstrap.min',
        'bootstrap-table':'./lib/bootstrap/bootstrap-table.min',
        'bootstrap-table-zh-CN':'./lib/bootstrap/bootstrap-table-zh-CN.min',
        'jquery-i18n': './lib/jquery/jquery.i18n.properties',
        'jquery-validate':'./lib/jquery/jquery-validate',
        'twaver':'./lib/twaver/twaver',

        /**自定义js**/
        'addMonitorConfig':'./monitorConfig/add-monitor-config',
        'monitorConfig':'./monitorConfig/monitor-config',
        'pjaxConfig':'./pjax-config',
        'commonModule':'./common-module',
        'validate-extend':'./validate-extend',
        'addNetwork':'./monitor/add-network',
        'addDb':'./monitor/add-db',
        'addTomcat':'./monitor/add-tomcat',
        'addCas':'./monitor/add-cas',
        'addK8s':'./monitor/add-k8s',
        'topoMain':'./topo/topo-main',
        'editTopo':'./topo/edit-topo',
        'showBusiness':'./business/business-list',
        'editBusiness':'./business/edit-business',
        'showAlert':'./alert/alert-list',
        'monitorConfigList':'./monitorConfig/monitor-config-list',



    },
    shim:{
        'jquery-i18n':['jquery'],
        'jquery-validate':['jquery'],
        'vueRouter':['jquery','vue'],
        'pjax':['jquery'],
        'bootstrap':['jquery'],
        'bootstrap-table':['bootstrap'],
        'bootstrap-table-zh-CN':['bootstrap'],
    },
    waitSeconds: 6000

});