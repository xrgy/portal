/**
 * Created by gy on 2018/3/24.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend'], function ($, Vue, commonModule, validateExtend) {
    var showBusiness = function () {
        if ($('#myBusiness')[0]) {
            var showBus = new Vue({
                el: '#myBusiness',
                data: {
                    moreCondition: commonModule.i18n("monitorConfig.moreCondition"),
                    templateName: "cc",
                    businessList:[],
                    path: {
                        getBusinessList: "/business/getBusinessList",
                    }

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
                    initData:function () {
                        var _self = this;
                        $.ajax({
                            // data: {"canvasId": _self.canvasId, "linkRate": linkRate},
                            url: _self.path.getBusinessList,
                            success: function (data) {
                                if (data.msg == "SUCCESS") {
                                    var data = data.data;
                                    data.forEach(function (x) {
                                        _self.businessList.push(x);
                                    });
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    //下一步
                    next: function () {
                        //得到当前选中项的页号
                        var id = $("#pageNum option:selected").val();
                        //计算下一页的页号
                        var nextPage = parseInt(id) + 1;
                        //得到select的option集合
                        var list = $("#pageNum").options;
                        //得到select中，下一页的option
                        var nextOption = list[nextPage - 1];
                        //修改select的选中项
                        nextOption.selected = true;
                        //调用查询方法
                        this.search();
                    },

                    //上一步
                    previous: function () {

                        //得到当前选中项的页号
                        var id = $("#pageNum option:selected").val();
                        //计算上一页的页号
                        var previousPage = parseInt(id) - 1;
                        //得到select的option集合
                        var list = $("#pageNum").options;
                        //得到select中，上一页的option
                        var previousOption = list[previousPage - 1];
                        //修改select的选中项
                        previousOption.selected = true;
                        //调用查询方法
                        this.search();
                    },
                    //修改每页显示条数时，要从第一页开始查起
                    research: function () {
                        //得到select的option集合
                        var list = $("#pageNum").options;
                        //得到select中，第一页的option
                        var nextOption = list[0];
                        //修改select的选中项
                        nextOption.selected = true;
                        //调用查询方法
                        this.search();
                    },
                    search:function () {
                        //得到每页显示条数
                        var pageSize=$("#pageSize").val();
                        //得到显示第几页
                        var pageNum=$("#pageNum").val();
                        //todo
                        //得到总页数
                        var pageCount;
                        if(pageNumCount/pageSize==0){
                            pageCount=pageNumCount/pageSize;
                        }else{
                            pageCount=Math.ceil(pageNumCount/pageSize);
                        }
                    }
                }

            });
        }
    }
    showBusiness();
    return {showBusiness: showBusiness}
})