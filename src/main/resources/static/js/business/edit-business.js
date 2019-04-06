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
                        getBusMonitorList: "/monitor/getBusinessMonitorRecord",
                        updateBusiness:"/business/updateBusiness",
                    },
                    businessName:"",
                    resourceList:null,
                    pageNum:1,
                    pageSize:10,
                    pageNumList:[],
                    currenPageInfo:"",
                    totalPage:0,
                    monitorEntityList:[],
                    showMonitorList:[],
                    totalRecord:0,
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
                    initData: function () {
                        var _self = this;
                        _self.monitorEntityList=[];
                        _self.pageNumList=[];
                        _self.showMonitorList=[];
                        $.ajax({
                            url: _self.path.getBusMonitorList,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    _self.totalRecord = data.length;
                                    if (_self.totalRecord % _self.pageSize == 0) {
                                        _self.totalPage = Math.floor(_self.totalRecord / _self.pageSize);
                                    } else {
                                        _self.totalPage = Math.floor(_self.totalRecord / _self.pageSize) + 1;
                                    }
                                    for(var i=1;i<=_self.totalPage;i++){
                                        _self.pageNumList.push(i);
                                    }
                                    var i=0;
                                    data.forEach(function (x) {
                                        _self.monitorEntityList.push(x);
                                    });
                                    _self.reloadMonitorList();
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    reloadMonitorList:function () {
                        var _self=this;
                        _self.showMonitorList=[];
                        var  startIndex = (_self.pageNum-1)*parseInt(_self.pageSize);
                        for(var i=0;i<parseInt(_self.pageSize);i++){
                            if (i+startIndex>=_self.monitorEntityList.length){
                                break;
                            }
                            _self.showMonitorList.push(_self.monitorEntityList[i+startIndex]);
                        }
                        _self.currenPageInfo="共"+_self.totalRecord+"条记录 , 当前第"+_self.pageNum+"/"+_self.totalPage+"页"
                    },
                    closeAddResource:function (e) {
                        $("#addResource").modal('hide');
                    },
                    research: function () {
                        var _self = this;
                        _self.pageNum =1;
                        _self.reloadMonitorList();
                    },
                    previous: function () {
                        var _self = this;
                        if (_self.pageNum > 1) {
                            _self.pageNum = _self.pageNum - 1;
                            _self.reloadMonitorList();
                        }
                    },
                    search: function () {
                        this.reloadMonitorList();

                    },
                    //下一步
                    next: function () {
                        var _self = this;
                        if (_self.pageNum < _self.totalPage) {
                            _self.pageNum = _self.pageNum + 1;
                            _self.reloadMonitorList();
                        }
                    },
                    clickBaseInfo: function (event) {
                        var e = event.target;
                        $('#leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.tabSelected="baseInfo";
                        this.busRadio="1";
                    },
                    checkAllDel:function (event) {
                        var _self = this;
                        if ($("#allDel").prop("checked")){
                            $("input[name='needDel']:checkbox").each(function(){
                                $(this).prop("checked",true);
                            });
                        }else {
                            $("input[name='needDel']:checkbox").each(function(){
                                $(this).prop("checked",false);
                            });
                        }

                    },
                    checkAllAdd:function (event) {
                        var _self = this;
                        if ($("#allAdd").prop("checked")){
                            $("input[name='needAdd']:checkbox").each(function(){
                                $(this).prop("checked",true);
                            });
                        }else {
                            $("input[name='needAdd']:checkbox").each(function(){
                                $(this).prop("checked",false);
                            });
                        }

                    },

                    delList:function () {
                        var _self = this;
                        var monitorList = [];
                        $("input[name='needDel']:checkbox").each(function(){
                            if($(this).prop("checked")){
                                monitorList.push($(this).val());
                            }
                        });
                      //todo 只是从_self.resourceList中移除
//                        this.actionDel(JSON.stringify(monitorList))
                        monitorList.forEach(function (item) {
                            _self.resourceList.remove(item);
                        })
                    },
                    delResource:function (event,monitorId) {
                      var _self = this;
                      var monitorList = [];
                      monitorList.push(monitorId);
                      //todo 只是从_self.resourceList中移除
                      //_self.actionDel(JSON.stringify(monitorList))
                        _self.resourceList.remove(monitorId);
                    },
                    addResource:function () {
                        //显示modal，请求
                        $("#addResource").modal({backdrop: 'static', keyboard: false, show: true})
                        this.initData();
                    },
                    // actionDel:function (monitorListStr) {
                    //     var _self=this;
                    //     $.ajax({
                    //         //"MySQL mysql"
                    //         data: {"businessId":_self.businessId,"monitorList":monitorListStr},
                    //         url: _self.path.delBusinessResource,
                    //         success: function (data) {
                    //             if (data.msg === "SUCCESS") {
                    //                 _self.refreshResourceList();
                    //             }
                    //         },
                    //         error: function () {
                    //         }
                    //     })
                    // },
                    refreshResourceList:function () {
                        var _self=this;
                        _self.resourceList=[];
                        $.ajax({
                            //"MySQL mysql"
                            data: {"businessId":_self.businessId},
                            url: _self.path.getBusinessInfo,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
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
                        this.tabSelected="resource";
                        this.busRadio="2";
                    },

                    hasK8sNodeType:function(element, index, array) {
                        if (element.lightType=='k8sNode'){
                            return true;
                        }
                    },
                    hasK8scType:function(element, index, array) {
                        if (element.lightType=='k8sContainer'){
                            return true;
                        }
                    },
                    hasCvkType:function(element, index, array) {
                        if (element.lightType=='CVK'){
                            return true;
                        }
                    },
                    hasVmType:function(element, index, array) {
                        if (element.lightType=='VirtualMachine'){
                            return true;
                        }
                    },
                    //新建模板提交
                    submitForm:function () {
                        var _self = this;
                        // _self.resourceList中必须包含cvk和vm，k8snode和k8sc
                        var hasK8sn = _self.resourceList.some(_self.hasK8sNodeType);
                        var hasK8sc = _self.resourceList.some(_self.hasK8scType);
                        var hasCvk = _self.resourceList.some(_self.hasCvkType);
                        var hasVm = _self.resourceList.some(_self.hasVmType);
                        if (hasK8sn && hasK8sc && hasCvk && hasVm){
                            //提交
                            $.ajax({
                                type:"post",
                                data:{businessId:_self.businessId,busname:_self.businessName,data:JSON.stringify(_self.resourceList)},
                                url:_self.path.updateBusiness,
                                success:function (data) {
                                    if (data.msg === "SUCCESS") {
                                        //弹出框 新建成功
                                        commonModule.prompt("prompt.updateSuccess",data.msg);
                                    }else {
                                        //弹出框 新建失败
                                        commonModule.prompt("prompt.updateError","alert");
                                    }
                                    $("#editBusiness").modal('hide');

                                },
                                error:function () {
                                    //处理异常，请重试
                                    $("#editBusiness").modal('hide');
                                    commonModule.prompt("prompt.exceptionPleaseTryAgain","alert");
                                }

                            })

                        }else {
                            commonModule.prompt("prompt.confirmError", "alert");
                        }
                    },
                    submitAddResourceForm:function () {
                        //不提交到服务器，只是提交到_self.resourceList中
                        var _self = this;
                        $("input[name='needAdd']:checkbox").each(function(){
                            if($(this).prop("checked")){
                                //monitorList.push($(this).val());
                                //
                                var monitorId = $(this).val();
                                var name = $(this).closest("tr").find('span').eq(0).attr("value");
                                var ip = $(this).closest("tr").find('span').eq(1).attr("value");
                                var light = $(this).closest("tr").find('span').eq(2).attr("value")
                                _self.resourceList.push({monitorId:monitorId,name:name,ip:ip,lightType:light})
                            }
                        });
                        $("#addResource").modal('hide');
                    }
                }

            });
            $("#edit-business").validate({
                submitHandler: function () {
                    editBusiness.submitForm();
                },
                rules:{
                },
                messages:{
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

            $("#add-resource").validate({
                submitHandler: function () {
                    editBusiness.submitAddResourceForm();
                },
                rules:{
                },
                messages:{
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
            editBusiness.busRadio="1";
            editBusiness.initBaseInfo();
        })
        Array.prototype.indexOf = function(val) {
            for (var i = 0; i < this.length; i++) {
                if (this[i].monitorId == val) return i;
            }
            return -1;
        }
        Array.prototype.remove = function(val) {
            var index = this.indexOf(val);
            if (index > -1) {
                this.splice(index, 1);
            }
        };
    }
    businessEdit();
    return {businessEdit: businessEdit}
})