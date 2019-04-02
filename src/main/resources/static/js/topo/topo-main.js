/**
 * Created by gy on 2018/12/18.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend', 'twaver'], function ($, Vue, commonModule, validateExtend, twaver) {
    var startTopo = function () {
        if ($('#mycanvas')[0]) {
            var startTopo = new Vue({
                el: '#mycanvas',
                data: {

                    box: null,
                    network: null,
                    nodeList: [],
                    linkList: [],
                    springLayout: null,
                    path: {
                        getWeaveDta: "/topo/getAllWeaveTopoData",
                        // getWeaveLink: "/topo/getAllWeaveTopoLink",
                        getBusinessNode: "/topo/getBusinessNode",
                        saveTopo: "/topo/saveBusinessTopo",

                    },
                    selectname: "",
                    selectHealthscore: "",
                    selectBusyscore: "",
                    selectAvailablescore: "",
                    selectLinkLeftName: "",
                    selectLinkRightName: "",
                    busUuid: "",
                    canvasId: "",
                    location: "self",
                    autoLayout:null

                },
                mounted: function () {
                    this.busUuid = sessionStorage.getItem('relevatBusinessUuid');
                    this.initBox();
                    this.initDataBox();
                    this.initListener();
                    // this.registerImage('../image/cupcake.jpg','node');
                    // this.registerIcon('<i class="fa fa-cloud"></i>','node');
                    // this.registerImage('<i class="fa fa-cloud"></i>','node');
                    twaver.Util.registerImage('node', {
                        "w": 64,
                        "h": 44,
                        "origin": {
                            "x": 0,
                            "y": 0
                        },
                        "v": [
                            {
                                "shape": "path",
                                "data": "M57.477,13.271C55.93,7.851,50.947,3.88,45.037,3.88c-1.912,0-3.727,0.418-5.361,1.165   C36.873,1.946,32.824,0,28.321,0C21.957,0,16.5,3.89,14.188,9.424c-0.183-0.006-0.364-0.014-0.549-0.014   C6.106,9.41,0,15.527,0,23.073c0,7.545,6.106,13.661,13.639,13.661c1.8,0,3.516-0.353,5.089-0.986   C21.06,40.619,26.167,44,32.097,44c4.835,0,9.124-2.246,11.808-5.717c1.666,0.63,3.477,0.98,5.373,0.98   C57.408,39.264,64,32.907,64,25.066C64,20.152,61.41,15.821,57.477,13.271z",
                                "gradient": {
                                    "id": "SVGID_1_",
                                    "type": "linear",
                                    "x1": 19.6367,
                                    "y1": 2.646,
                                    "x2": 43.9219,
                                    "y2": 44.7091,
                                    "stop": [
                                        {
                                            "offset": "0",
                                            "color": "#FFFFFF"
                                        },
                                        {
                                            "offset": "0.3353",
                                            "color": "#FFFFFF"
                                        },
                                        {
                                            "offset": "0.5355",
                                            "color": "#FFFFFF"
                                        },
                                        {
                                            "offset": "0.7003",
                                            "color": "#FFFFFF"
                                        },
                                        {
                                            "offset": "0.8458",
                                            "color": "#FFFFFF"
                                        },
                                        {
                                            "offset": "0.9773",
                                            "color": "#FFFFFF"
                                        },
                                        {
                                            "offset": "1",
                                            "color": "#FFFFFF"
                                        }
                                    ]
                                }
                            },
                            {
                                "shape": "path",
                                "data": "M28.321,2c3.752,0,7.35,1.599,9.871,4.387l0.982,1.086l1.332-0.609   c1.429-0.653,2.953-0.984,4.53-0.984c4.857,0,9.183,3.265,10.517,7.94l0.205,0.721l0.63,0.408C59.902,17.228,62,21.009,62,25.066   c0,6.726-5.707,12.197-12.723,12.197c-1.602,0-3.172-0.286-4.666-0.852l-1.384-0.523l-0.905,1.171   C39.895,40.199,36.168,42,32.097,42c-4.956,0-9.496-2.793-11.565-7.115l-0.81-1.693l-1.741,0.701   c-1.386,0.559-2.847,0.842-4.341,0.842C7.221,34.734,2,29.503,2,23.073C2,16.642,7.221,11.41,13.639,11.41   c0.125,0,0.247,0.004,0.37,0.009l0.115,0.004l1.378,0.044l0.531-1.272C18.113,5.217,22.937,2,28.321,2 M28.321,0   C21.957,0,16.5,3.89,14.188,9.424c-0.183-0.006-0.364-0.014-0.549-0.014C6.106,9.41,0,15.527,0,23.073   c0,7.545,6.106,13.661,13.639,13.661c1.8,0,3.516-0.353,5.089-0.986C21.06,40.619,26.167,44,32.097,44   c4.835,0,9.124-2.246,11.808-5.717c1.666,0.63,3.477,0.98,5.373,0.98C57.408,39.264,64,32.907,64,25.066   c0-4.914-2.59-9.245-6.523-11.795C55.93,7.851,50.947,3.88,45.037,3.88c-1.912,0-3.727,0.418-5.361,1.165   C36.873,1.946,32.824,0,28.321,0L28.321,0z",
                                "gradient": {
                                    "id": "SVGID_2_",
                                    "type": "linear",
                                    "x1": 19.6367,
                                    "y1": 2.646,
                                    "x2": 43.9219,
                                    "y2": 44.7091,
                                    "stop": [
                                        {
                                            "offset": "0",
                                            "color": "#5384BE"
                                        },
                                        {
                                            "offset": "1",
                                            // "color": "#727171"
                                            "color": "#5384BE"
                                        }
                                    ]
                                }
                            }
                        ]
                    });
                    this.getData();
                },
                methods: {

                    initBox: function () {
                        var _self = this;
                        var box = new twaver.ElementBox();
                        var network = new twaver.vector.Network(box);
                        var view = network.getView();
                        $("#weaveTopo").append(view);
                        network.adjustBounds({
                            x: 0,
                            y: 0,
                            width: document.documentElement.clientWidth - 350,
                            height: document.documentElement.clientHeight
                        });
                        _self.box = box;
                        _self.network = network;
                        twaver.Styles.setStyle('link.color', '#1db163');
                        twaver.Styles.setStyle('arrow.to', true);
                        twaver.Styles.setStyle('arrow.to.color', '#1db163');
                        _self.autoLayout = new twaver.layout.AutoLayouter(box);
                        _self.springLayout = new twaver.layout.SpringLayouter(network);
                        _self.springLayout.setNodeRepulsionFactor(0.6);
                        _self.springLayout.setLinkRepulsionFactor(0.6);
                        _self.springLayout.setInterval(50);
                        _self.springLayout.setStepCount(10);


                    },
                    initDataBox: function () {
                        twaver.SerializationSettings.setClientType('uuid', 'string');
                        twaver.SerializationSettings.setClientType("fromNode", 'string');
                        twaver.SerializationSettings.setClientType("toNode", 'string');
                    },
                    getData: function () {
                        var _self = this;
                        _self.location = "self";
                        $.ajax({
                            data: {"relUuid": _self.busUuid},
                            url: _self.path.getWeaveDta,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    var canvas = data.canvas;
                                    if (canvas) {
                                        _self.canvasId = canvas.uuid;
                                        _self.box.setClient("uuid", _self.canvasId);
                                    }
                                    var nodelist = data.nodes;
                                    for (var i = 0; i < nodelist.length; i++) {
                                        if (nodelist[i].xpoint == 0 && nodelist[i].ypoint == 0) {
                                            _self.autoLayout.doLayout('symmetry', function () {
                                                _self.springLayout.start();
                                            });
                                            _self.location = "auto";
                                            break;
                                        }
                                    }
                                    if (nodelist) {
                                        for (var index in nodelist) {
                                            _self.createNode(nodelist[index]);
                                        }
                                    }

                                    var linklist = data.links;
                                    if (linklist) {
                                        for (var i in linklist) {
                                            var fromNodeId = linklist[i].fromNodeId,
                                                toNodeId = linklist[i].toNodeId,
                                                uuid = linklist[i].uuid;
                                            var node1 = null, node2 = null;
                                            for (var j in _self.nodeList) {
                                                if (_self.nodeList[j].getId() === fromNodeId) {
                                                    node1 = _self.nodeList[j];
                                                }
                                                if (_self.nodeList[j].getId() === toNodeId) {
                                                    node2 = _self.nodeList[j];
                                                }
                                            }
                                            if (node1 && node2) {
                                                _self.createLink(uuid, node1, node2);
                                            }
                                        }
                                    }
                                }
                            },
                            error: function () {

                            }
                        })
                    },
                    // getLink:function () {
                    //     var _self = this;
                    //     $.ajax({
                    //         data: {"relUuid": _self.busUuid},
                    //         url: _self.path.getWeaveLink,
                    //         success: function (data) {
                    //             if (data.msg === "SUCCESS") {
                    //                 var link = data.data;
                    //                 for(var i in link) {
                    //                     var fromNodeId = link[i].fromNodeId,
                    //                         toNodeId = link[i].toNodeId,
                    //                         uuid = link[i].uuid;
                    //                     var node1=null,node2=null;
                    //                     for (var j in _self.nodeList){
                    //                         if (_self.nodeList[j].getId() === fromNodeId){
                    //                             node1 = _self.nodeList[j];
                    //                         }
                    //                         if (_self.nodeList[j].getId() === toNodeId){
                    //                             node2 = _self.nodeList[j];
                    //                         }
                    //                     }
                    //                     if(node1 && node2){
                    //                         _self.createLink(uuid,node1,node2);
                    //                     }
                    //                 }
                    //             }
                    //         },
                    //         error: function () {
                    //         }
                    //     })
                    // },
                    saveTopo: function (event) {
                        var _self = this;
                        var boxvalue = new twaver.JsonSerializer(_self.box).serialize();
                        var _self = this;
                        $.ajax({
                            type: "POST",
                            // contentType: false,
                            // contentType: "application/json;charset=utf-8",
                            data: {"topojson": boxvalue},
                            url: _self.path.saveTopo,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    commonModule.prompt("prompt.saveSuccess", data.msg);
                                } else {
                                    //弹出框 新建失败
                                    commonModule.prompt("prompt.saveError", "alert");
                                }
                            },
                            error: function () {
                                commonModule.prompt("prompt.exceptionPleaseTryAgain", "alert");
                            }
                        })

                    },
                    createNode: function (mynode) {
                        var _self = this;
                        var node = new twaver.Node(mynode.uuid);
                        node.setClient("uuid", mynode.uuid);
                        node.setName(mynode.nodeName);
                        node.setImage("node");
                        if (_self.location == "self") {
                            node.setLocation(mynode.xpoint, mynode.ypoint)
                        }
                        _self.nodeList.push(node);
                        _self.box.add(node);

                    },
                    initListener: function () {
                        var _self = this;
                        var selectionModel = _self.box.getSelectionModel();
                        selectionModel.setSelectionMode('singleSelection');
                        selectionModel.addSelectionChangeListener(function (e) {
                            console.log(e);
                            console.log('kind:' + e.kind + ',datas:' + e.datas.toString());

                        });
                        _self.network.addInteractionListener(function (e) {
                            var el = e.element;
                            window.onmouseup = function () {
                                if (e.kind == 'clickElement' && el) {
                                    if (el.getClassName() == 'twaver.Node') {
                                        $("#tipNodeMsg").removeClass('hidden');
                                        $("#tipNodeMsg").css('left', e.event.clientX + 10);
                                        $("#tipNodeMsg").css('top', e.event.clientY + 10);
                                        _self.initSelectNode(el);
                                    } else if (el.getClassName() == 'twaver.Link') {
                                        $("#tipLinkMsg").removeClass('hidden');
                                        $("#tipLinkMsg").css('left', e.event.clientX + 10);
                                        $("#tipLinkMsg").css('top', e.event.clientY + 10);
                                        _self.initSelectLink(el);
                                    }
                                } else if (e.kind == 'clickBackground') {
                                    $("#tipNodeMsg").addClass('hidden');
                                    $("#tipLinkMsg").addClass('hidden');
                                }
                            }
                        })
                    },
                    initSelectNode: function (el) {
                        var _self = this;
                        _self.selectname = el.getName();
                        var uuid = el.getClient("uuid");
                        var _self = this;
                        $.ajax({
                            data: {"uuid": uuid},
                            url: _self.path.getBusinessNode,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var node = data.data;
                                    _self.selectHealthscore = node.health_score;
                                    _self.selectBusyscore = node.busy_score;
                                    _self.selectAvailablescore = node.available_score;
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    initSelectLink: function (el) {
                        var _self = this;
                        _self.selectLinkLeftName = el.getClient('fromNode');
                        _self.selectLinkRightName = el.getClient('toNode');
                    },
                    // registerImage:function (){
                    // this.registerNormalImage('../images/bg.png','bg');
                    // },
                    registerImage: function (url, name) {
                        var _self = this;
                        var image = new Image();
                        // image.src = url;
                        image.onload = function () {
                            // twaver.Util.registerImage(name, image, 30, 30);

                            image.onload = null;
                            _self.network.invalidateElementUIs();
                        };
                    },

                    createLink: function (uuid, node1, node2) {
                        var _self = this;
                        var link = new twaver.Link(uuid, node1, node2);
                        link.setClient("fromNode", node1.getName());
                        link.setClient("toNode", node2.getName());
                        _self.linkList.push(link);
                        _self.box.add(link);
                    },

                    clickLayout: function (event) {
                        var _self = this;
                        var isRunning = _self.springLayout.isRunning();
                        if (isRunning) {
                            _self.springLayout.stop();
                        } else {
                            _self.springLayout.start();
                        }
                    }
                }

            });
        }
    }
    startTopo();
    return {startTopo: startTopo}
})