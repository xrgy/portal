/**
 * Created by gy on 2018/6/4.
 */
'use strict';
define(['jquery', 'jquery-i18n'], function ($, jqueryi18n) {
    var i18n = function (key, param0, param1, param2, param3, param4, param5, param6, param7, param8, param9) {
        return $.i18n.prop(key, param0, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    };
    $(document).ready(function () {
        loadProperties("message", "././i18n/", sessionStorage.language);
        require(['validate-extend', 'addMonitorConfig', 'monitorConfig']);
        $("#prompt-close").click(function () {
            $(this).parent().addClass("hidden");
        })
    });
    function loadProperties(name, path, lang) {
        //中文
        lang = 'zh_CN';
        $.i18n.properties({
            name: name,
            path: path,
            mode: 'map',
            language: lang,
            callback: function () {
                $("[data-i18n]").each(function () {
                    $(this).html($.i18n.prop($(this).data("i18n")));
                });
                $("[data-i18n-placeholder]").each(function () {
                    $(this).attr('placeholder', $.i18n.prop($(this).data("i18n-placeholder")));
                });
                $("[data-i18n-value]").each(function () {
                    $(this).attr('value', $.i18n.prop($(this).data("i18n-value")));
                });
                $("[data-i18n-title]").each(function () {
                    $(this).attr('title', $.i18n.prop($(this).data("i18n-title")));
                });
                $("[data-i18n-text]").each(function () {
                    $(this).text($.i18n.prop($(this).data('i18n-text')));
                })
                $("[data-i18n-btn]").each(function () {
                    $(this).append($.i18n.prop($(this).data("i18n-btn")));
                });
            }
        })
    };

    var prompt = function (info, level) {
        $('#prompt-modal #prompt-info').text(i18n(info));
        if (level === "SUCCESS") {
            $('#prompt-modal #prompt-title').text(i18n("prompt.titleSuccess"));
            $('#prompt-modal').removeClass("prompt-alert");
            $('#info-ident').removeClass("fa-close").addClass("fa-check");
        } else if (level === "alert") {
            $('#prompt-modal #prompt-title').text(i18n("prompt.titleAlert"));
            $('#prompt-modal').addClass("prompt-alert");
            $('#info-ident').removeClass("fa-check").addClass("fa-close");
        }
        $('#prompt-modal').removeClass('hidden');
        setTimeout(function () {
            $('#prompt-modal').addClass('hidden');
        }, 4000);

    };

    // commonModule.i18n=i18n;
    return {
        i18n: i18n,
        prompt:prompt

    };

});