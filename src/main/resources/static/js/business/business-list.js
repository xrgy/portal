/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend','topoMain'], function ($, Vue, commonModule, validateExtend,topoMain) {
    var showBusiness = function () {
        if ($('#myBusiness')[0]) {
            var showBus = new Vue({
                    el: '#myBusiness',
                    data: {
                        moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                        templateName: "cc",
                        businessList: [],
                        path: {
                            getBusinessList: "/business/getBusinessListByPage",
                            // getRelevatTopo:"/business/getRelevatTopo"
                        },
                        pageNum:1,
                        pageSize:15,
                        pageNumList:[],
                        totalPage:0,

                    },
                    filters: {
                        convertDesc: function (desc) {
                            if (desc != null) {
                                return commonModule.i18n("metric.description." + desc);
                            } else {
                                return "";
                            }
                        }
                    },
                    mounted: function () {
                        this.initData();
                    },
                    methods: {
                        initData: function () {
                            var _self = this;
                            _self.businessList=[];
                            _self.pageNumList=[];
                            $.ajax({
                                data: {"pageIndex": parseInt(_self.pageNum), "pageSize": parseInt(_self.pageSize)},
                                url: _self.path.getBusinessList,
                                success: function (data) {
                                    if (data.msg == "SUCCESS") {
                                        var data = data.data;
                                        _self.totalPage= data.totalPage;
                                        // if(_self.totalPage<_self.pageNum){
                                        //     _self.pageNum=1;
                                        // }
                                            for(var i=1;i<=_self.totalPage;i++){
                                            _self.pageNumList.push(i);
                                        }

                                        data.list.forEach(function (x) {
                                            x.hoverEnable = false;
                                            _self.businessList.push(x);
                                        });
                                        setTimeout(function () {
                                            _self.initFontColor();
                                        }, 1000)
                                    }
                                },
                                error: function () {
                                }
                            })
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
                                this.initData();
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
                                this.initData();
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
                            this.initData();
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
                            this.initData();
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
    showBusiness();
    return {showBusiness: showBusiness}
})