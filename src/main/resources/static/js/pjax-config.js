/**
 * Created by gy on 2018/3/26.
 */
'use strict'
define(['jquery','vue','pjax','commonModule'], function ($, Vue, pjax,commonModule) {
    $(document).ready(function () {
        // monitorConfig.monitorConf();
        // configlist.confList();
    })

    $(document).click('a', function () {
        sessionStorage.menuItem = "monitorConfig";
    });
    $('body').pjax('a', '#container', {
        fragment: '#container'
    });
    $('body').on('pjax:complete', function () {
        switch (sessionStorage.menuItem) {
            case "monitorConfig":
                // monitorConfig.monitorConf();
                break;
            default:
                break;
        }
    })
});


