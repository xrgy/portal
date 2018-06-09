/**
 * Created by gy on 2018/6/9.
 */
"use strict";
define(["jquery","jquery-validate","common-module"],function ($,jqueryValidate,commonModule) {
    //校验字符长度最大限制
    jQuery.validator.addMethod("maxLength",function (value,element,param) {
        return this.optional(element) || (value.length <= 100000);
    },commonModule.i18n("validate.maxLength"));
    //校验url
    jQuery.validator.addMethod("isUrl",function (value,element,param) {
        var pattern = /[a-zA-Z]+:(?:\/\/)[^\s]/;
        return this.optional(element) || (pattern.test(value));
    },commonModule.i18n("validate.urlIllegal"));
    //限制输入值1到100
    jQuery.validator.addMethod("int1To100",function (value,element,param) {
        var pattern = /^([1-9][0-9]?|100)$/;
        return this.optional(element) || (pattern.test(value));
    },commonModule.i18n("validate.num1To100"));
    //限制输入值大于指定输入框数值
    jQuery.validator.addMethod("largerThanOther",function (value,element,param) {
        return parseInt(value) > parseInt(param);
    },commonModule.i18n("validate.largerthan"));
    //限制输入值小于指定输入框数值
    jQuery.validator.addMethod("smallerThanOther",function (value,element,param) {
        return parseInt(value) < parseInt(param);
    },commonModule.i18n("validate.smallerthan"));
    //验证字符串不全为空
    jQuery.validator.addMethod("isEmpty",function (value,element,param) {
        return $.trim(value)!="";
    },commonModule.i18n("validate.notEmpty"));
    //验证是否为整数
    jQuery.validator.addMethod("isNumber",function (value,element,param) {
        var pattern = /^\d{1,}$/;
        return this.optional(element) || (pattern.test(value));
    },commonModule.i18n("validate.isNumber"));
    //验证IP地址是否合法 iPv4的ip地址都是（1~255）.（0~255）.（0~255）.（0~255）的格式
    jQuery.validator.addMethod("isIP",function (value,element,param) {
        var pattern = /^(([1-9])|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
        return this.optional(element) || (pattern.test(value));
    },commonModule.i18n("validate.ipIllegal"));
});