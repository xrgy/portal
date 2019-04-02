/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule','validate-extend'], function ($, Vue, commonModule,validateExtend) {
    var businessEdit = function () {
        if ($('#editBusiness')[0]) {
            var editBusiness = new Vue({
                el: '#editBusiness',
                data: {
                    businessId:"",
                    tabSelected:"",
                    busRadio:"1",
                    path:{
                        getBusinessInfo: "/business/getBusinessInfo",
                        delBusinessResource: "/business/delBusinessResource",

                    },
                    businessName:"",
                    resourceList:null,
                },
                filters: {
                    convertLightType: function (type) {
                        if (type != null) {
                            if (type === "VirtualMachine"){
                               return commonModule.i18n("monitor.lightType.VirtualMachine");
                            }else if (type === "k8sContainer"){
                               return "Docker";
                            }else {
                               return type;
                            }
                        } else {
                            return "";
                        }
                    }
                },
                mounted: function () {
                    console.log("edit business display")
                },
                methods: {
                    initForm: function () {
                        $("#edit-business").validate().resetForm();
                        $('.form-group').removeClass("has-error");
                        this.title = "",
                            this.infoIp = '',
                            this.infoName = '',
                            this.monitorMode = 'snmp_v1',
                            this.infoReadcommunity = '',
                            this.infoWritecommunity = '',
                            this.infoPort = '161',
                            this.infoTimeinterval = '180',
                            this.infoTimeout = '175',
                            this.infoMonitortemplate = '',
                            this.templateList = [{
                                uuid: '',
                                templateName: commonModule.i18n("form.select.default")
                            }];
                    },
                    clickBaseInfo: function (event) {
                        var e = event.target;
                        $('#leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.tabSelected="baseInfo";
                        this.busRadio="1";
                    },
                    delResource:function (event,monitorId) {
                      var _self = this;
                      var monitorList = [];
                      monitorList.push(monitorId);
                        $.ajax({
                            //"MySQL mysql"
                            data: {"businessId":_self.businessId,"monitorList":JSON.stringify(monitorList)},
                            url: _self.path.delBusinessResource,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    _self.businessName=data.name;
                                    _self.resourceList=data.resourceList;
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    initBaseInfo:function () {
                        var _self = this;
                        $.ajax({
                            //"MySQL mysql"
                            data: {"businessId":_self.businessId},
                            url: _self.path.getBusinessInfo,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    _self.businessName=data.name;
                                    _self.resourceList=data.resourceList;
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    clickBusResource: function (event) {
                        var e = event.target;
                        $('#leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        editBusiness.tabSelected="resource";
                        this.busRadio="2";
                    },


                    initTable: function () {
                        var _self = this;
                        for(var i in _self.availDataObj){
                            for(var j in _self.availDataObj[i].data){
                                var item = _self.availDataObj[i].data[j];
                                item.severity='0';
                            }
                        }
                        for(var t in _self.perfDataObj){
                            for(var k in _self.perfDataObj[t].data){
                                var item1 = _self.perfDataObj[t].data[k];
                                item1.level_one_severity='0';
                                item1.level_one_alert_first_condition='0';
                                item1.level_one_expression_more='and';
                                item1.level_one_alert_second_condition='0';
                                item1.level_two_severity='1';
                                item1.level_two_alert_first_condition='0';
                                item1.level_two_expression_more='and';
                                item1.level_two_alert_second_condition='0';

                            }
                        }
                    },
                    groupByType: function (array) {
                        var map = {}, ret = [];
                        for (var i in array) {
                            var line = array[i];
                            if (!map[line.type]) {
                                ret.push({
                                    type: line.type,
                                    data: [line.data]
                                });
                                map[line.type] = ret;
                            } else {
                                for (var j in ret) {
                                    var typeArr = ret[j];
                                    if (line.type === typeArr.type) {
                                        typeArr.data.push(line.data);
                                        break;
                                    }
                                }
                            }
                        }
                        return ret;
                    },
                    //显示二级告警
                    showMoreLine: function (event) {
                        var e = event.target;
                        //closet从当前元素开始向上查找，包括当前元素
                        $(e).closest('.span-more-line').addClass("hidden");
                        $(e).closest('.span-more-line').closest('div').next().removeClass('hidden');
                    },
                    //显示更多条件
                    showMoreCondition: function (event) {
                        var e = event.target;
                        $(e).addClass("hidden");
                        $(e).parent().next().removeClass('hidden');
                    },
                    //close按钮
                    closeCondition: function (event) {
                        var e = event.target;
                        if ($(e).parent().prev('div').length !== 0) {
                            $(e).parent().addClass("hidden");
                            $(e).parent().prev('div').children('.span-more-condition').removeClass('hidden');
                        } else {
                            $(e).parent().next().addClass('hidden');
                            $(e).closest('div').children('.span-more-condition').removeClass('hidden');
                            $(e).parent().parent().prev('div').children('.span-more-line').removeClass('hidden');
                            $(e).parent().parent().addClass('hidden');
                        }
                    },
                    toggleType: function (event) {
                        var e = event.target;
                        $(e).toggleClass('fa-angle-down fa-angle-up');
                        var start = $(e).parent().parent().attr('id');
                        $(e).parent().parent().siblings("tr[id^=" + start + "]").toggleClass('hidden')
                        // $($(e).parent().parent().siblings("tr[id^="+start+"]")).slideToggle();

                    },
                    toggleMoitorMode:function (str) {
                        var _self =this;
                        _self.monitorMode=str;
                        console.log("monitorMode:"+_self.monitorMode);
                        _self.initData();
                    },
                    //新建模板提交
                    submitForm:function () {
                        var _self = this;
                        var data ={};
                        data.template_name=_self.templateName;
                        data.monitor_mode= _self.monitorMode;
                        data.template_type= 1;
                        data.light_type=_self.lightType;
                        data.available = _self.availDataObj;
                        data.performance = _self.perfDataObj;

                        $.ajax({
                            type:"post",
                            data:{templateData:JSON.stringify(data)},
                            url:_self.path.addTemplate,
                            success:function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    commonModule.prompt("prompt.deleteSuccess",data.msg);
                                }else {
                                    //弹出框 新建失败
                                    commonModule.prompt("prompt.deleteError","alert");
                                }
                                $("#monitorConfig").modal('hide');
                                
                            },
                            error:function () {
                                //处理异常，请重试
                                $("#monitorConfig").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain","alert");
                            }

                        })

                    }
                }

            });
            $("#add-template").validate({
                submitHandler: function () {
                    monitorConfig.submitForm();
                },
                //失去焦点
                onfocusout: function (element,event) {
                    $(element).valid();
                },
                ignore:".ignore",
                //在 keyup 时验证,默认为true
                onkeyup:function (element,event) {
                    // $(element).valid();
                    return false;
                },
                rules:{
                    tempName:{
                        required:true,
                        //remote接受的返回值只要true和false即可
                        remote:{
                            //验证名称是否重复
                            type:"post",
                            url:"/monitorConfig/isTemplateNameDup",
                            timeout:6000,
                            data:{
                                name:function () {
                                    return $('#tempName').val();
                            }}
                        }
                    }
                },
                messages:{
                    tempName: {
                        required: commonModule.i18n("validate.inputNotEmpty"),
                        remote: commonModule.i18n("validate.templateNameDuplicate")
                    }
                },
                errorElement:"span",
                errorPlacement:function (error,element) {
                    element.parent("div").addClass('has_feedback');
                    if (element.prop("type") === "checkbox"){
                        error.insertAfter(element.parent("label"));
                    }else {
                        error.insertAfter(element);
                    }
                },
                success: function (label,element) {
                    //要验证的元素通过验证后的动作
                },
                highlight: function (element,erroClass,validClass) {
                    $(element).parent("div").addClass("has-error").removeClass("has-success");
                },
                unhighlight: function (element,erroClass,validClass) {
                    $(element).parent("div").addClass("has-success").removeClass("has-error");
                }
            });

        }
        $("#editBusiness").on('show.bs.modal', function () {
            editBusiness.businessId = sessionStorage.getItem('editBusinessId');
            editBusiness.tabSelected="baseInfo";
            editBusiness.initBaseInfo();
        })
    }
    businessEdit();
    return {businessEdit: businessEdit}
})