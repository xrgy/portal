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
                    network:null,
                    nodeList: [],
                    linkList: [],
                    springLayout:null,
                    path: {
                        getWeaveNode: "/topo/getAllWeaveTopoNode",
                        getWeaveLink: "/topo/getAllWeaveTopoLink",
                        getBusinessNode: "/topo/getBusinessNode",

                    },
                    selectname:"",
                    selectHealthscore:"",
                    selectBusyscore:"",
                    selectAvailablescore:"",
                    selectLinkLeftName:"",
                    selectLinkRightName:"",

                },
                mounted: function () {
                    this.initBox();
                    this.initDataBox();
                    this.initListener();
                    this.registerImage('../image/cupcake.jpg','node');
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
                        var autoLayout = new twaver.layout.AutoLayouter(box);
                        _self.springLayout = new twaver.layout.SpringLayouter(network);
                        _self.springLayout.setNodeRepulsionFactor(0.3);
                        _self.springLayout.setLinkRepulsionFactor(0.3);
                        _self.springLayout.setInterval(50);
                        _self.springLayout.setStepCount(10);
                        autoLayout.doLayout('symmetry',function () {
                            _self.springLayout.start();
                        });

                    },
                    initDataBox: function () {
                        twaver.SerializationSettings.setClientType('uuid', 'string');
                        twaver.SerializationSettings.setClientType("fromNode", 'string');
                        twaver.SerializationSettings.setClientType("toNode", 'string');
                    },
                    getData:function () {
                        var _self = this;
                        $.ajax({
                            // data: {"lightType": _self.lightType, "monitorMode": _self.monitorMode},
                            url: _self.path.getWeaveNode,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var node = data.data;
                                    for(var index in node) {
                                        _self.createNode(node[index]);
                                    };

                                    _self.getLink();

                                }
                            },
                            error: function () {

                            }
                        })
                    },
                    getLink:function () {
                        var _self = this;
                        $.ajax({
                            // data: {"lightType": _self.lightType, "monitorMode": _self.monitorMode},
                            url: _self.path.getWeaveLink,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var link = data.data;
                                    for(var i in link) {
                                        var fromNodeId = link[i].fromNodeId,
                                            toNodeId = link[i].toNodeId,
                                            uuid = link[i].uuid;
                                        var node1=null,node2=null;
                                        for (var j in _self.nodeList){
                                            if (_self.nodeList[j].getId() === fromNodeId){
                                                node1 = _self.nodeList[j];
                                            }
                                            if (_self.nodeList[j].getId() === toNodeId){
                                                node2 = _self.nodeList[j];
                                            }
                                        }
                                        if(node1 && node2){
                                            _self.createLink(uuid,node1,node2);
                                        }
                                    }
                                }
                            },
                            error: function () {
                            }
                        })
                    },
                    createNode: function (mynode) {
                        var _self = this;
                        var node = new twaver.Node(mynode.uuid);
                        node.setClient("uuid",mynode.uuid);
                        node.setName(mynode.nodeName);
                        node.setImage("node");
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
                        image.src = url;
                        image.onload = function () {
                            twaver.Util.registerImage(name, image, 30, 30);
                            image.onload = null;
                            _self.network.invalidateElementUIs();
                        };
                    },

                    createLink:function (uuid,node1,node2) {
                        var _self = this;
                        var link = new twaver.Link(uuid,node1,node2);
                        link.setClient("fromNode", node1.getName());
                        link.setClient("toNode", node2.getName());
                        _self.linkList.push(link);
                        _self.box.add(link);
                    },

                    clickLayout:function (event) {
                        var _self = this;
                        var isRunning = _self.springLayout.isRunning();
                        if (isRunning){
                            _self.springLayout.stop();
                        }else {
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