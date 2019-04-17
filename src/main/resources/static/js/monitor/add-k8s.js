/**
 * Created by gy on 2018/10/12.
 */
'use strict';
define(['jquery', 'vue', 'commonModule', 'validate-extend', 'bootstrap-table'], function ($, Vue, commonModule, validateExtend, bootstrapTable) {
    var addk8s = function () {
        if ($('#addk8s')[0]) {
            var addK8s = new Vue({
                el: '#addk8s',
                data: {
                    title: "",
                    infoIp: '',
                    infoName: '',
                    infoAPIPort: '8080',
                    infoCAdvisorPort: '4194',
                    infoTimeinterval: '180',
                    infoTimeout: '175',
                    infoK8sMonitortemplate: '',
                    infoK8snMonitortemplate: '',
                    infoK8scMonitortemplate: '',
                    radioType: "1",
                    path: {
                        getTemplateByLightType: "/monitorConfig/getTemplateByLightType",
                        addContainerMonitorRecord: "/monitor/addContainerMonitorRecord",
                        getContainerList: "/monitor/getContainerList",
                        getEditData:"/monitor/getK8sMonitor",
                        updateContainerMonitorRecord:"/monitor/updateContainerMonitorRecord",
                        k8sCanAccess:"/monitor/k8sCanAccess",

                    },
                    k8sTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    k8snTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    k8scTemplateList: [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                    lightType: "",
                    tabSelected: "",
                    monitorOpe:"",
                    monitorOpeUuid:"",
                    pageNum: 1,
                    pageSize: 10,
                    pageNumList: [],
                    totalPage: 0,
                    currenPageInfo: "",
                    totalRecord: 0,
                    containerEntityList: [],
                    showContainerList: [],
                },
                filters: {
                    convertContainerStatus: function (value) {
                        if (value === "0") {
                            return commonModule.i18n("monitor.baseinfo.status.stopped")
                        } else if (value === "1") {
                            return commonModule.i18n("monitor.baseinfo.status.running")
                        }
                    },
                },
                mounted: function () {
                    var light = sessionStorage.getItem('addLightType');
                    this.tabSelected = light;
                },
                methods: {

                    // clearTable: function () {
                    //     $('#tabdiv')[0].innerHTML = "<table id='mycontainertab' class='table table-hover'></table>";
                    // },
                    initForm: function () {
                        $("#add-k8s").validate().resetForm();
                        $('.form-group').removeClass("has-error");
                        this.title = "",
                            this.infoIp = '',
                            this.infoName = '',
                            this.infoAPIPort = '8080',
                            this.infoCAdvisorPort = '4194',
                            this.infoTimeinterval = '180',
                            this.infoTimeout = '175',
                            this.radioType = "1",
                            this.infoK8sMonitortemplate = '',
                            this.infoK8snMonitortemplate = '',
                            this.infoK8scMonitortemplate = '',
                            this.k8sTemplateList = [{uuid: '', templateName: commonModule.i18n("form.select.default")}],
                            this.k8snTemplateList = [{
                                uuid: '',
                                templateName: commonModule.i18n("form.select.default")
                            }],
                            this.k8scTemplateList = [{
                                uuid: '',
                                templateName: commonModule.i18n("form.select.default")
                            }];
                    },
                    initEditData: function (lightType) {
                        var _self = this;
                        _self.lightType = lightType;
                        $.ajax({
                            data: {uuid: _self.monitorOpeUuid},
                            url: _self.path.getEditData,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    _self.infoIp = data.ip;
                                    _self.infoName = data.name;
                                    _self.infoAPIPort = data.apiPort;
                                    _self.infoCAdvisorPort = data.cadvisorPort,
                                    _self.infoTimeinterval = data.scrapeInterval;
                                    _self.infoTimeout = data.scrapeTimeout;
                                    _self.radioType = "1";
                                    _self.infoBbname = data.databasename;
                                    _self.infoK8sMonitortemplate = data.templateId;
                                    _self.infoK8snMonitortemplate = data.k8sNTemplateId;
                                    if (data.k8scTemplateId==null){
                                        _self.infoK8scMonitortemplate='';
                                    }else {
                                        _self.infoK8scMonitortemplate = data.k8scTemplateId;
                                    }
                                    if (data.monitorTemplate.k8s !== null) {
                                        data.monitorTemplate.k8s.forEach(function (x) {
                                            _self.k8sTemplateList.push(x);
                                        });
                                    }
                                    if (data.monitorTemplate.k8sn !== null) {
                                        data.monitorTemplate.k8sn.forEach(function (x) {
                                            _self.k8snTemplateList.push(x);
                                        });
                                    }
                                    if (data.monitorTemplate.k8sc !== null) {
                                        data.monitorTemplate.k8sc.forEach(function (x) {
                                            _self.k8scTemplateList.push(x);
                                        });
                                    }
                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    initData: function (lightType) {
                        var _self = this;
                        _self.lightType = lightType;
                        $.ajax({
                            data: {lightType: lightType},
                            url: _self.path.getTemplateByLightType,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    if (data.k8s !== null) {
                                        data.k8s.forEach(function (x) {
                                            _self.k8sTemplateList.push(x);
                                        });
                                    }
                                    if (data.k8sn !== null) {
                                        data.k8sn.forEach(function (x) {
                                            _self.k8snTemplateList.push(x);
                                        });
                                    }
                                    if (data.k8sc !== null) {
                                        data.k8sc.forEach(function (x) {
                                            _self.k8scTemplateList.push(x);
                                        });
                                    }
                                }
                            },
                            error: function () {

                            }

                        })
                    },
                    checkAllDel: function (event) {
                        var _self = this;
                        if ($("#containerList #allDel").prop("checked")) {
                            $("#containerList input[name='needDel']:checkbox").each(function () {
                                $(this).prop("checked", true);
                            });
                        } else {
                            $("#containerList input[name='needDel']:checkbox").each(function () {
                                $(this).prop("checked", false);
                            });
                        }

                    },
                    // initTable: function () {
                    //             var _self = this;
                    //             $('#mycontainertab').bootstrapTable({
                    //                 method: 'get',
                    //                 cache: false,
                    //                 contentType: "application/x-www-form-urlencoded",
                    //                 dataType: "json",
                    //                 url: _self.path.getContainerList + "?ip=" + _self.infoIp + "&apiPort=" + _self.infoAPIPort,
                    //                 height: 500,//高度调整
                    //                 striped: true, //是否显示行间隔色
                    //                 dataField: 'data',//请求回来的list字段名
                    //                 // dataField: "res",//获取数据的别名，先省略，则为你返回的
                    //                 pageNumber: 1, //初始化加载第一页，默认第一页
                    //                 pagination: true,//是否分页
                    //                 // queryParamsType:'limit',
                    //                 // queryParams:queryParams,
                    //                 sidePagination: 'client',//：client客户端分页，server服务端分页
                    //                 pageSize: 10,//单页记录数
                    //                 // pageList:[5,10,20,30],//分页步进值
                    //                 showRefresh: false,//刷新按钮
                    //                 showColumns: false,
                    //                 // clickToSelect: true,//是否启用点击选中行
                    //                 // toolbarAlign:'right',
                    //                 // buttonsAlign:'right',//按钮对齐方式
                    //                 // toolbar:'#toolbar',//指定工作栏
                    //                 columns: [
                    //                     {
                    //                         title: '',
                    //                         field: '',
                    //                         checkbox: true,
                    //                         // checked:true,没用选不选中根据filed有没有值
                    //                         // width: 25,
                    //                         class: 'mycheck',
                    //                         align: 'center',
                    //                         valign: 'middle',
                    //                     },
                    //                     {
                    //                         title: '容器名称',
                    //                         field: 'name',
                    //                         sortable: true
                    //                     },
                    //                     {
                    //                         title: 'pod',
                    //                         field: 'podName',
                    //                         sortable: true
                    //                     },
                    //                     {
                    //                         title: '运行状态',
                    //                         field: 'status',
                    //                         sortable: true,
                    //                         formatter: function (value, row, index) {
                    //                             //通过formatter可以自定义列显示的内容
                    //                             //value：当前field的值，即id
                    //                             //row：当前行的数据
                    //                             if (value === "0") {
                    //                                 return commonModule.i18n("monitor.baseinfo.status.stopped")
                    //                             } else if (value === "1") {
                    //                                 return commonModule.i18n("monitor.baseinfo.status.running")
                    //                             }
                    //                         }
                    //                     }
                    //
                    //                 ],
                    //         onLoadSuccess: function(res){  //加载成功时执行
                    //             console.info("加载成功");
                    //             console.log(res);
                    //         },
                    //         onLoadError: function(){  //加载失败时执行
                    //             console.info("加载数据失败");
                    //         },
                    //         locale: 'zh-CN',//中文支持,
                    //     })
                    // },
                    reloadMonitorList: function () {
                        var _self = this;
                        _self.showContainerList = [];
                        var startIndex = (_self.pageNum - 1) * parseInt(_self.pageSize);
                        for (var i = 0; i < parseInt(_self.pageSize); i++) {
                            if (i + startIndex >= _self.containerEntityList.length) {
                                break;
                            }
                            _self.showContainerList.push(_self.containerEntityList[i + startIndex]);
                        }
                        _self.currenPageInfo = "共" + _self.totalRecord + "条记录 , 当前第" + _self.pageNum + "/" + _self.totalPage + "页"
                    },
                    initContainerTable: function () {
                        var _self = this;
                        _self.pageNumList = [];
                        _self.containerEntityList = [];
                        $.ajax({
                            url: _self.path.getContainerList + "?ip=" + _self.infoIp + "&apiPort=" + _self.infoAPIPort,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    _self.totalRecord = data.length;
                                    if (_self.totalRecord % _self.pageSize == 0) {
                                        _self.totalPage = Math.floor(_self.totalRecord / _self.pageSize);
                                    } else {
                                        _self.totalPage = Math.floor(_self.totalRecord / _self.pageSize) + 1;
                                    }
                                    for (var i = 1; i <= _self.totalPage; i++) {
                                        _self.pageNumList.push(i);
                                    }
                                    var i = 0;
                                    data.forEach(function (x) {
                                        _self.containerEntityList.push(x);
                                    });
                                    _self.reloadMonitorList();
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    //下一步
                    next: function () {
                        var _self = this;
                        if (_self.pageNum < _self.totalPage) {
                            _self.pageNum = _self.pageNum + 1;
                            _self.reloadMonitorList();
                        }
                    },

                    //上一步
                    previous: function () {
                        var _self = this;
                        if (_self.pageNum > 1) {
                            _self.pageNum = _self.pageNum - 1;
                            _self.reloadMonitorList();
                        }
                    },
                    //修改每页显示条数时，要从第一页开始查起
                    research: function () {
                        var _self = this;
                        _self.pageNum = 1;
                        _self.reloadMonitorList();
                    },
                    search: function () {
                        this.reloadMonitorList();
                    },
                    clickResource: function (event, lighttype) {
                        var e = event.target;
                        $('#addk8s #leftMenu').find('li').removeClass('active');
                        $(e).closest('li').addClass('active');
                        this.initForm();
                        this.initData(lighttype);
                    },
                    //测试是否连通
                    canReach:function () {
                        var _self = this;
                        $.ajax({
                            data: {"masterIp": _self.infoIp,"apiPort": _self.infoAPIPort},
                            url: _self.path.k8sCanAccess,
                            success: function (data) {
                                if (data.accessible === true) {
                                    commonModule.prompt("prompt.accessSuccess","SUCCESS");
                                }else {
                                    commonModule.prompt("prompt.accessError","alert");
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    submitForm: function () {
                        var _self = this;
                        var ids = [];//得到用户选择的数据的ID
                        // var rows = $("#mycontainertab").bootstrapTable('getSelections');
                        $("input[name='needDel']:checkbox").each(function () {
                            if ($(this).prop("checked")) {
                                var id = $(this).val();
                                ids.push(id);
                            }
                        });
                        // for (var i = 0; i < rows.length; i++) {
                        //     ids.push(rows[i].id);
                        // }
                        var formdata = new FormData($('#add-k8s')[0]);
                        formdata.append("lightType", _self.lightType);
                        formdata.append("containerIds", JSON.stringify(ids));
                        if (_self.monitorOpe == "edit"){
                            formdata.append("uuid", _self.monitorOpeUuid);
                        }
                        $.ajax({
                            type: "post",
                            data: formdata,
                            contentType: false,
                            processData: false,
                            url: _self.monitorOpe == "edit" ? _self.path.updateContainerMonitorRecord :_self.path.addContainerMonitorRecord,
                            dataType: 'json',//预期服务器返回数据类型
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    //弹出框 新建成功
                                    if (_self.monitorOpe == "edit"){
                                        commonModule.prompt("prompt.updateSuccess",data.msg);
                                    }else {
                                        commonModule.prompt("prompt.insertSuccess", data.msg);
                                    }
                                } else {
                                    //弹出框 新建失败
                                    if (_self.monitorOpe == "edit") {
                                        commonModule.prompt("prompt.updateError","alert");
                                    }else {
                                        commonModule.prompt("prompt.insertError", "alert");
                                    }
                                }
                                $("#addk8s").modal('hide');
                            },
                            error: function () {
                                //处理异常，请重试
                                $("#addk8s").modal('hide');
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }
                        })
                    },

                }
            })


            $('#add-k8s').validate({
                submitHandler: function () {
                    addK8s.submitForm();
                },
                //失去焦点
                onfocusout: function (element, event) {
                    $(element).valid();
                },
                //在 keyup 时验证,默认为true
                onkeyup: function (element, event) {
                    // $(element).valid();
                    return false;
                },
                rules: {
                    ip: {
                        required: true,
                        isIP: true,
                        //remote接受的返回值只要true和false即可
                        remote: {
                            //验证ip是否重复
                            type: "get",
                            url: "/monitor/isMonitorRecordIpDup",
                            timeout: 6000,
                            data: {
                                ip: function () {
                                    return $('#k8sip').val();
                                },
                                lightType: function () {
                                    return addK8s.lightType;
                                },
                                uuid: function () {
                                    return addK8s.monitorOpeUuid;
                                },
                            }
                        },
                    },
                    name: {
                        required: true
                    },
                    apiport: {
                        required: true
                    },
                    cadvisorport: {
                        required: true
                    },
                    timeinterval: {
                        required: true
                    },
                    timeout: {
                        required: true
                    },
                    k8sTemplate: {
                        // required: true,
                    },
                    k8sNodeTemplate: {
                        // required: true,
                    },
                    k8sContainerTemplate: {
                        // required: true,
                    }
                },
                messages: {
                    ip: {
                        required: commonModule.i18n("validate.inputNotEmpty"),
                        isIP: commonModule.i18n("validate.pleaseInputIPAddress"),
                        remote: commonModule.i18n("validate.ipDuplicate")
                    },
                    name: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    apiport: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    cadvisorport: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeinterval: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    timeout: {
                        required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    k8sTemplate: {
                        // required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    k8sNodeTemplate: {
                        // required: commonModule.i18n("validate.inputNotEmpty")
                    },
                    k8sContainerTemplate: {
                        // required: commonModule.i18n("validate.inputNotEmpty")
                    }
                },
                errorElement: "span",
                errorPlacement: function (error, element) {
                    element.parent("div").addClass('has_feedback');
                    if (element.prop("type") === "checkbox") {
                        error.insertAfter(element.parent("label"));
                    } else {
                        error.insertAfter(element);
                    }
                },
                success: function (label, element) {
                    //要验证的元素通过验证后的动作
                },
                highlight: function (element, erroClass, validClass) {
                    $(element).parent("div").addClass("has-error").removeClass("has-success");
                },
                unhighlight: function (element, erroClass, validClass) {
                    $(element).parent("div").addClass("has-success").removeClass("has-error");
                }
            });

        }

        $("#addk8s").on('show.bs.modal', function (event) {
            addK8s.initForm();
            // $('#tabdiv')[0].innerHTML = "<table id='mycontainertab' class='table table-hover'></table>";
            var light="";
            addK8s.monitorOpe=sessionStorage.getItem('monitorOpe');
            if (addK8s.monitorOpe=="edit"){
                addK8s.monitorOpeUuid=sessionStorage.getItem('monitorOpeObj');
                light=sessionStorage.getItem('editLightType');
                addK8s.title = commonModule.i18n("modal.title.update" + light);
                addK8s.initEditData(light);
            }else {
                light = sessionStorage.getItem('addLightType');
                addK8s.title = commonModule.i18n("modal.title.add" + light);
                addK8s.initData(light);
            }
            addK8s.tabSelected = light;
            // setTimeout(function () {
            //     $('#tabdiv')[0].innerHTML = "<table id='mycontainertab' class='table table-hover'></table>";
            // }, 1000);
        });
        $("#addk8s").on("hide.bs.model", function (e) {
            $("#add-k8s").resetForm();
        });

    }
    addk8s();
    return {addk8s: addk8s}
})