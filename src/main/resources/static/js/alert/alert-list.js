/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend','topoMain'], function ($, Vue, commonModule, validateExtend,topoMain) {
    var showAlert = function () {
        if ($('#myalert')[0]) {
            var showBus = new Vue({
                    el: '#myalert',
                    data: {
                        moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                        templateName: "cc",
                        businessList: [],
                        path: {
                            getAlertList: "/alert/getAlertInfo",
                            // getRelevatTopo:"/business/getRelevatTopo"
                        },
                        pageNum:1,
                        pageSize:15,
                        pageNumList:[],
                        totalPage:0,
                        alertList:[],
                        transAlertInfo:null,
                        alertLevel:"",
                        alertStatus:"",
                        alertResource:"",
                        alertUuid:"",
                        currenPageInfo:"",
                        totalRecord:0,
                        showAlertList:[],
                        tabSelected: "all",
                        lightType:"all",
                    },
                    filters: {
                        convertDesc: function (desc) {
                            if (desc != null) {
                                return commonModule.i18n("metric.description." + desc);
                            } else {
                                return "";
                            }
                        },
                        convertName: function (name) {
                            if (name != null) {
                                return commonModule.i18n("metric.name." + name);
                            } else {
                                return "";
                            }
                        },
                        convertSeverity:function (severity) {
                            var str="";
                            switch (severity){
                                case 0:
                                    str = commonModule.i18n("alertLevel.critical");
                                    break;
                                case 1:
                                    str = commonModule.i18n("alertLevel.major");
                                    break;
                                case 2:
                                    str = commonModule.i18n("alertLevel.minor");
                                    break;
                                case 3:
                                    str = commonModule.i18n("alertLevel.warning");
                                    break;
                                case 4:
                                    str = commonModule.i18n("alertLevel.notice");
                                    break;
                                default:
                                    break;
                            };
                            return " "+str;
                        },
                        convertStatus:function (status) {
                            if (status==0){
                                //未恢复
                                return commonModule.i18n("alertStatus.none");
                            }else {
                                    //已恢复
                                return commonModule.i18n("alertStatus.already");
                            }
                        },
                        convertAlertLevel:function (severity) {
                            var str="";
                            switch (severity){
                                case 0:
                                    str = commonModule.i18n("alertLevel.critical");
                                    break;
                                case 1:
                                    str = commonModule.i18n("alertLevel.major");
                                    break;
                                case 2:
                                    str = commonModule.i18n("alertLevel.minor");
                                    break;
                                case 3:
                                    str = commonModule.i18n("alertLevel.warning");
                                    break;
                                case 4:
                                    str = commonModule.i18n("alertLevel.notice");
                                    break;
                                default:
                                    break;
                            };
                            return "级别: "+str;
                        },
                        convertAlertStatus:function (status) {
                            if (status==0){
                                //未恢复
                                return "状态: "+commonModule.i18n("alertStatus.none");
                            }else {
                                //已恢复
                                return "状态: "+commonModule.i18n("alertStatus.already");
                            }
                        },
                        convertAlertResource:function (name) {
                            return "资源: "+name;
                        }

                    },
                    mounted: function () {
                        var obj={"severity": 0,"resolve": 0,"ip":"172.25.17.253","uuid":"0b6b3cdf-fbbb-480a-aaa7-41edd38d75a2"};
                        sessionStorage.setItem("transAlert",JSON.stringify(obj));
                        this.transAlertInfo=JSON.parse(sessionStorage.getItem("transAlert"));
                        if (null!=this.transAlertInfo){
                            this.alertLevel=this.transAlertInfo.severity;
                            // this.alertLevel="";

                            this.alertStatus=this.transAlertInfo.resolve;
                            this.alertResource=this.transAlertInfo.ip;
                            this.alertUuid=this.transAlertInfo.uuid;
                        }else {
                            this.alertLevel=-1;
                            // this.alertLevel="";

                            this.alertStatus=-1;
                            this.alertResource="";
                            this.alertUuid="";
                            this.lightType="all";
                        }
                        this.initData();
                    },
                    methods: {
                        initData: function () {
                            var _self = this;
                            _self.businessList=[];
                            _self.pageNumList=[];
                            $.ajax({
                                data: {"severity": _self.alertLevel,"ip":_self.alertResource,"resolve":_self.alertStatus,
                                    "uuid":_self.alertUuid,"lightType":_self.lightType},
                                url: _self.path.getAlertList,
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
                                       _self.alertList=data;
                                        _self.reloadAlertList();

                                    }
                                },
                                error: function () {
                                }
                            })
                        },
                        reloadAlertList:function () {
                            var _self=this;
                            _self.showAlertList=[];
                            var  startIndex = (_self.pageNum-1)*parseInt(_self.pageSize);
                            for(var i=0;i<parseInt(_self.pageSize);i++){
                                if (i+startIndex>=_self.alertList.length){
                                    break;
                                }
                                _self.showAlertList.push(_self.alertList[i+startIndex]);
                            }
                            _self.currenPageInfo="共"+_self.totalRecord+"条记录 , 当前第"+_self.pageNum+"/"+_self.totalPage+"页"
                        },
                        initFontColor: function () {
                            var _self = this;
                            for (var i = 0; i < _self.businessList.length; i++) {
                                var j = i + 1;
                                if (_self.businessList[i].health_score >= 75) {
                                    //绿色
                                    $(".singleBusiness:nth-child(" + j + ") .heartBeat").addClass("notBusy");
                                    $(".singleBusiness:nth-child(" + j + ") .healthStyle label").addClass("notBusy");
                                } else if (_self.businessList[i].health_score >= 26) {
                                    //黄色
                                    $(".singleBusiness:nth-child(" + j + ") .heartBeat").addClass("normalBusy");
                                    $(".singleBusiness:nth-child(" + j + ") .healthStyle label").addClass("normalBusy");
                                } else {
                                    //红色
                                    $(".singleBusiness:nth-child(" + j + ") .heartBeat").addClass("mostBusy");
                                    $(".singleBusiness:nth-child(" + j + ") .healthStyle label").addClass("mostBusy");
                                }

                                if (_self.businessList[i].busy_score >= 75) {
                                    //红色
                                    $(".singleBusiness:nth-child(" + j + ") .busyStyle label").addClass("mostBusy");
                                } else if (_self.businessList[i].busy_score >= 26) {
                                    //黄色
                                    $(".singleBusiness:nth-child(" + j + ") .busyStyle label").addClass("normalBusy");
                                } else {
                                    //绿色
                                    $(".singleBusiness:nth-child(" + j + ") .busyStyle label").addClass("notBusy");
                                }

                                if (_self.businessList[i].available_score >= 75) {
                                    //绿色
                                    $(".singleBusiness:nth-child(" + j + ") .availableStyle label").addClass("notBusy");
                                } else if (_self.businessList[i].available_score >= 26) {
                                    //黄色
                                    $(".singleBusiness:nth-child(" + j + ") .availableStyle label").addClass("normalBusy");
                                } else {
                                    //红色
                                    $(".singleBusiness:nth-child(" + j + ") .availableStyle label").addClass("mostBusy");
                                }
                            }
                        },
                        clickResource: function (event, lighttype) {
                            var e = event.target;
                            $('#leftMenu').find('li').removeClass('active');
                            $(e).closest('li').addClass('active');
                            //其实是middle
                            this.tabSelected = lighttype;
                            this.pageNum = 1;
                            this.pageSize = 10;
                            this.pageNumList = [];
                            this.totalPage = 0;
                            this.currenPageInfo = "";
                            this.totalRecord = 0;
                            this.lightType=lighttype;
                            this.initData();
                        },
                        closeFilterCond:function (e,condition) {
                            var _self = this;
                          switch (condition){
                              case 'severity':
                                  _self.alertLevel=-1;
                                  _self.initData();
                                  break;
                              case 'status':
                                  _self.alertStatus=-1;
                                  _self.initData();
                                  break;
                              case 'monitor':
                                  _self.alertResource="";
                                  _self.initData();
                                  break;
                              case 'all':
                                  _self.alertLevel=-1;
                                  _self.alertStatus=-1;
                                  _self.alertResource="";
                                  _self.alertUuid="";
                                  _self.initData();
                                  break;
                              default:
                                  break;
                          }
                        },
                        seeRelevantTopo: function (e, uuid) {
                            sessionStorage.setItem('relevatBusinessUuid',uuid);
                            window.open("/topo/showWeaveTopo",'_parent');
                            topoMain.startTopo();
                        },
                        //下一步
                        next: function () {
                            var _self = this;
                            if (_self.pageNum<_self.totalPage){
                                _self.pageNum= _self.pageNum+1;
                                _self.reloadAlertList();
                            }
                            // //得到当前选中项的页号
                            // var id = _self.pageNum;
                            // //计算下一页的页号
                            // var nextPage = parseInt(id) + 1;
                            // //得到select的option集合
                            // var list = $("#pageNum").options;
                            // //得到select中，下一页的option
                            // var nextOption = list[nextPage - 1];
                            // //修改select的选中项
                            // nextOption.selected = true;
                            // //调用查询方法
                        },

                        //上一步
                        previous: function () {
                            var _self = this;
                            if (_self.pageNum>1){
                                _self.pageNum= _self.pageNum-1;
                                _self.reloadAlertList();
                            }
                            // //得到当前选中项的页号
                            // var id = $("#pageNum option:selected").val();
                            // //计算上一页的页号
                            // var previousPage = parseInt(id) - 1;
                            // //得到select的option集合
                            // var list = $("#pageNum").options;
                            // //得到select中，上一页的option
                            // var previousOption = list[previousPage - 1];
                            // //修改select的选中项
                            // previousOption.selected = true;
                            // //调用查询方法
                        }
                        ,
                        //修改每页显示条数时，要从第一页开始查起
                        research: function () {
                            // //得到select的option集合
                            // var list = $("#pageNum").options;
                            // //得到select中，第一页的option
                            // var nextOption = list[0];
                            // //修改select的选中项
                            // nextOption.selected = true;
                            // //调用查询方法
                            var _self = this;
                            _self.pageNum =1;
                            _self.reloadAlertList();
                        },
                        search: function () {
                            // //得到每页显示条数
                            // var pageSize = $("#pageSize").val();
                            // //得到显示第几页
                            // var pageNum = $("#pageNum").val();
                            // //todo
                            // //得到总页数
                            // var pageCount;
                            // if (pageNumCount / pageSize == 0) {
                            //     pageCount = pageNumCount / pageSize;
                            // } else {
                            //     pageCount = Math.ceil(pageNumCount / pageSize);
                            // }
                            _self.reloadAlertList();
                        },
                        selectStyle: function (event, j) {
                            this.businessList[j].hoverEnable = true;
                        }
                        ,
                        outStyle: function (event, j) {
                            this.businessList[j].hoverEnable = false;
                        }
                    }

                })
            ;
        }
    }
    showAlert();
    return {showAlert: showAlert}
})