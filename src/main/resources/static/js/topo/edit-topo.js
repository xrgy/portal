/**
 * Created by gy on 2018/12/18.
 */
'use strict'
define(['jquery', 'vue', 'commonModule', 'validate-extend', 'twaver'], function ($, Vue, commonModule, validateExtend, twaver) {
    var netTopo = function () {
        if ($('#mynetcanvas')[0]) {
            var netTopo = new Vue({
                el: '#mynetcanvas',
                data: {

                    box: null,
                    network: null,
                    nodeList: [],
                    linkList: [],
                    portList: [],
                    portToNode: {},
                    springLayout: null,
                    path: {
                        getTopoCanvasData: "/topo/getTopoCanvasData",
                        getNodeAlarmInfo: "/topo/getCanvasAlarmInfo",
                        getLinkRate: "/topo/getTopoLinkRate",
                        saveTopo: "/topo/saveTopo",
                    },
                    deviceIcon: {
                        router: "../image/cupcake.svg",
                        switch: "../image/cupcake.svg",
                        LB: "../image/cupcake.svg",
                        firewall: "../image/cupcake.svg",
                    },
                    alarmColor: ["red", "orange", "yellow", "blue"],
                    paramObj: {
                        inCrease: false,
                        fillAlpha: 0.9
                    },
                    canvasId: "",
                    selectType: "",
                    selectname: "",
                    selectIp: "",
                    selectcricital: "",
                    selectmajor: "",
                    selectminor: "",
                    selectwarning: "",
                    selectnotice: "",
                    selectLinkStatus: "",
                    selectLinkLeftName: "",
                    selectLinkRightName: "",
                    selectLinkLeftPort: "",
                    selectLinkRightPort: "",
                    selectLinkLeftRate: "",
                    selectLinkRightRate: "",
                },
                mounted: function () {
                    this.initBox();
                    this.initDataBox();
                    this.initListener();
                    // this.registerImage('../image/cupcake.svg', 'node');
                    this.getData();
                },
                filters: {
                    convertType: function (type) {
                        if (type != null && type != "") {
                            return commonModule.i18n("monitor.networkdevice." + type);
                        } else {
                            return "";
                        }
                    },
                    convertAlertNum: function (num) {
                        if (num == undefined) {
                            num = 0;
                        }
                        return "(" + num + ")"

                    },
                    convertLinkStatus: function (status) {
                        if (status == "1") {
                            return commonModule.i18n("topo.linkstatus.normal");

                        } else {
                            return commonModule.i18n("topo.linkstatus.unnormal");

                        }
                    },
                },
                methods: {
                    initDataBox: function () {
                        //label显示最高级别告警的count
                        var _self = this;
                        _self.network.getAlarmLabel = function (element) {
                            if (!element.getAlarmState().isEmpty()) {
                                return element.getAlarmState().getAlarmCount(element.getAlarmState().getHighestNewAlarmSeverity());
                            }
                            return null;
                        };
                        //告警不改变网元颜色
                        _self.network.getInnerColor = function (element) {
                            return element.getStyle('inner.color');
                        };
                        twaver.AlarmSeverity.Emergency = twaver.AlarmSeverity.add(900, "Emergency", 'E', '#c11212');
                        twaver.AlarmSeverity.FIRST_IMPORTANT = twaver.AlarmSeverity.add(800, "FirstImportant", 'F', '#ec670b');
                        twaver.AlarmSeverity.SECOND_IMPORTANT = twaver.AlarmSeverity.add(700, "SecondImportant", 'S', '#ffc267');
                        twaver.AlarmSeverity.WARN = twaver.AlarmSeverity.add(600, "Warn", 'W', '#22a5df');
                        _self.network.getToolTip = function (element) {
                            if (element._icon == 'link_icon') {
                                return '';
                            } else {
                                return element.getName();
                            }
                        }

                        twaver.SerializationSettings.setClientType('uuid', 'string');
                        twaver.SerializationSettings.setClientType('monitorUuid', 'string');
                        twaver.SerializationSettings.setClientType('canvasId', 'string');
                        twaver.SerializationSettings.setClientType('fromNodeId', 'string');
                        twaver.SerializationSettings.setClientType('fromPortId', 'string');
                        twaver.SerializationSettings.setClientType('toNodeId', 'string');
                        twaver.SerializationSettings.setClientType('toPortId', 'string');
                        twaver.SerializationSettings.setClientType('timeInterval', 'string');
                        twaver.SerializationSettings.setClientType("nodeType", 'string');
                        twaver.SerializationSettings.setClientType("ip", 'string');
                        twaver.SerializationSettings.setClientType("alarmNotice", 'string');
                        twaver.SerializationSettings.setClientType("linkStatus", 'string');
                        twaver.SerializationSettings.setClientType("fromNode", 'string');
                        twaver.SerializationSettings.setClientType("toNode", 'string');
                        twaver.SerializationSettings.setClientType("fromPort", 'string');
                        twaver.SerializationSettings.setClientType("toPort", 'string');


                    },
                    initBox: function () {
                        var _self = this;
                        var box = new twaver.ElementBox();
                        var network = new twaver.vector.Network(box);
                        var view = network.getView();
                        $("#netTopo").append(view);
                        network.adjustBounds({
                            x: 0,
                            y: 0,
                            width: document.documentElement.clientWidth - 350,
                            height: document.documentElement.clientHeight
                        });
                        _self.box = box;
                        _self.network = network;
                        twaver.Styles.setStyle('link.color', '#1db163');
                        // var autoLayout = new twaver.layout.AutoLayouter(box);
                        // _self.springLayout = new twaver.layout.SpringLayouter(network);
                        // _self.springLayout.setNodeRepulsionFactor(0.3);
                        // _self.springLayout.setLinkRepulsionFactor(0.3);
                        // _self.springLayout.setInterval(50);
                        // _self.springLayout.setStepCount(10);
                        // autoLayout.doLayout('symmetry', function () {
                        //     _self.springLayout.start();
                        // });

                    },

                    getData: function () {
                        var _self = this;
                        $.ajax({
                            // data: {"lightType": _self.lightType, "monitorMode": _self.monitorMode},
                            url: _self.path.getTopoCanvasData,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var data = data.data;
                                    if (data) {
                                        var canvas = data.canvas;
                                        if (canvas) {
                                            _self.canvasId = canvas.uuid;
                                            _self.box.setClient("uuid", _self.canvasId);
                                        }
                                        var nodelist = data.nodes;
                                        if (nodelist) {
                                            for (var index=0;index<nodelist.length;index++) {
                                                _self.createNode(nodelist[index]);
                                            }
                                        }
                                        var portlist = data.ports;
                                        if (portlist) {
                                            for (var index=0;index< portlist.length;index++) {
                                                _self.portList.push(portlist[index]);
                                                _self.portToNode[portlist[index].uuid] = portlist[index];
                                            }
                                        }
                                        var linklist = data.links;
                                        if (linklist) {
                                            for (var i=0;i<linklist.length;i++) {
                                                var fromPortId = linklist[i].fromPortId,
                                                    toPortId = linklist[i].toPortId,
                                                    canvasId = linklist[i].canvasId,
                                                    uuid = linklist[i].uuid;
                                                var fromNodeId = _self.portToNode[fromPortId].nodeUuid;
                                                var toNodeId = _self.portToNode[toPortId].nodeUuid;
                                                if (fromNodeId && toNodeId) {
                                                    var node1 = null, node2 = null;
                                                    for (var j=0;j<_self.nodeList.length;j++) {
                                                        if (_self.nodeList[j].getId() === fromNodeId) {
                                                            node1 = _self.nodeList[j];
                                                        }
                                                        if (_self.nodeList[j].getId() === toNodeId) {
                                                            node2 = _self.nodeList[j];
                                                        }
                                                        if (node1 && node2) {
                                                            break;
                                                        }
                                                    }
                                                    if (node1 && node2) {
                                                        _self.createLink(uuid, node1, node2, _self.portToNode[fromPortId], _self.portToNode[toPortId]);
                                                    }
                                                }
                                            }
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
                        node.setName(mynode.nodeName);
                        node.setLocation(mynode.xpoint, mynode.ypoint);
                        node.setClient("uuid", mynode.uuid);
                        node.setClient("monitorUuid", mynode.monitorUuid);
                        node.setClient("canvasId", _self.canvasId);
                        node.setClient("nodeType", mynode.nodeType);
                        node.setClient("ip", mynode.ip);
                        node.setSize(40, 40);
                        // var imageUrl = _self.setImageSrc(mynode.nodeType);
                        // node.setImageUrl(imageUrl);
                        _self.nodeList.push(node);
                        _self.box.add(node);
                    },
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
                        _self.selectType = el.getClient("nodeType");
                        _self.selectname = el.getName();
                        _self.selectIp = el.getClient("ip");
                        _self.selectcricital = el.getAlarmState().getNewAlarmCount(twaver.AlarmSeverity.Emergency);
                        _self.selectmajor = el.getAlarmState().getNewAlarmCount(twaver.AlarmSeverity.FIRST_IMPORTANT);
                        _self.selectminor = el.getAlarmState().getNewAlarmCount(twaver.AlarmSeverity.SECOND_IMPORTANT);
                        _self.selectwarning = el.getAlarmState().getNewAlarmCount(twaver.AlarmSeverity.WARN);
                        _self.selectnotice = el.getClient('alarmNotice');
                    },
                    initSelectLink: function (el) {
                        var _self = this;
                        _self.selectLinkStatus = el.getClient('linkStatus');
                        _self.selectLinkLeftName = el.getClient('fromNode');
                        _self.selectLinkRightName = el.getClient('toNode');
                        _self.selectLinkLeftPort = el.getClient('fromPort');
                        _self.selectLinkRightPort = el.getClient('toPort');
                        // _self.selectLinkLeftRate=el.getName();
                        // _self.selectLinkRightRate=el.getName2();
                        _self.selectLinkLeftRate = "11.34Kbps";
                        _self.selectLinkRightRate = "32.87Kbps";
                    },
                    setImageSrc: function (nodeType) {
                        var imgSrc = '';
                        var _self = this;
                        switch (nodeType) {
                            case 'router':
                                imgSrc = _self.deviceIcon['router'];
                                break;
                            case 'switch':
                                imgSrc = _self.deviceIcon['switch'];
                                break;
                            case 'LB':
                                imgSrc = _self.deviceIcon['LB'];
                                break;
                            case 'firewall':
                                imgSrc = _self.deviceIcon['firewall'];
                                break;
                            default:
                                break;
                        }
                        return imgSrc;
                    },
                    clearAlarm: function () {
                        var _self = this;
                        var existNode = _self.box.getDatas()._as.filter(function (item) {
                            if (item._icon == 'node_icon') {
                                return item;
                            }
                        });
                        $.each(existNode, function (index, item) {
                            var interval = item.getClient('timeInterval');
                            window.clearInterval(interval);
                            item.getAlarmState().clear();
                            item.setStyle('vector.fill', false);
                        })
                    },
                    setLinkRateInterval: function () {
                        var _self = this;
                        sessionStorage.linkRateInterval = window.setInterval(function () {
                            _self.setLinkRate();
                        }, 6000);
                    },
                    setLinkRate: function () {
                        var _self = this;
                        var linkRate = "in";
                        $.ajax({
                            data: {"canvasId": _self.canvasId, "linkRate": linkRate},
                            url: _self.path.getLinkRate,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var recordList = data.data;
                                    var box = _self.box;
                                    recordList.forEach(function (item) {
                                        var link = box._dataMap[item.uuid];
                                        if (link) {
                                            if (item.linkStatus == "1") {
                                                link.setClient('linkStatus', item.linkStatus);
                                                link.setName(item.formNodeRate);
                                                link.setStyle('label.position', "from");
                                                link.setStyle('label.xoffset', 80);
                                                link.setName2(item.toNodeRate);
                                                link.setStyle('label2.position', "to");
                                                link.setStyle('label2.xoffset', 80);
                                            } else {
                                                link.setName("");
                                                link.setName2("");
                                            }
                                        }
                                    })
                                }
                            },
                            error: function () {
                            }
                        })

                    },

                    getAllNodeAlarm: function () {
                        var _self = this;
                        $.ajax({
                            data: {"canvasId": _self.canvasId},
                            url: _self.path.getNodeAlarmInfo,
                            success: function (data) {
                                if (data.msg === "SUCCESS") {
                                    var result = data.data;
                                    if (result) {
                                        var nodeList = _self.nodeList;
                                        for (var i in result) {
                                            var record = result[i];
                                            var monitorUuid = record.monitorUuid;
                                            for (var j in nodeList) {
                                                var node = nodeList[j];
                                                if (node.getClient("monitorUuid") == monitorUuid) {
                                                    node.getAlarmState().clear();
                                                    node.setStyle('alarm.color', '#ffffff');
                                                    node.setStyle('alarm.pointer.length', 0);
                                                    node.setStyle('alarm.position', 'topright');
                                                    node.setStyle('alarm.xoffset', -5);
                                                    node.getAlarmState().setNewAlarmCount(twaver.AlarmSeverity.Emergency, record.alarm[0]);
                                                    node.getAlarmState().setNewAlarmCount(twaver.AlarmSeverity.FIRST_IMPORTANT, record.alarm[1]);
                                                    node.getAlarmState().setNewAlarmCount(twaver.AlarmSeverity.SECOND_IMPORTANT, record.alarm[2]);
                                                    node.getAlarmState().setNewAlarmCount(twaver.AlarmSeverity.WARN, record.alarm[3]);
                                                    node.setClient('alarmNotice', record.alarm[4]);
                                                    for (var k = 0; k < 4; k++) {
                                                        if (record.alarm[k] > 0) {
                                                            _self.setTick(_self.paramObj, node, _self.alarmColor[k]);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            },
                            error: function () {
                            }
                        })

                    },
                    createLink: function (uuid, node1, node2, port1, port2) {
                        var _self = this;
                        var link = new twaver.Link(uuid, node1, node2);
                        link.setStyle('link.color', '#1db163');
                        link.setClient('uuid', uuid);
                        link.setClient('canvasId', _self.canvasId);
                        link.setClient("fromNode", node1.getName());
                        link.setClient("toNode", node2.getName());
                        link.setClient('fromNodeId', node1.getClient("uuid"));
                        link.setClient('toNodeId', node2.getClient("uuid"));
                        link.setClient('fromPortId', port1.uuid);
                        link.setClient('toPortId', port2.uuid);
                        link.setClient('fromPort', port1.port);
                        link.setClient('toPort', port2.port);
                        _self.linkList.push(link);
                        _self.box.add(link);

                        //链路流量
                        // _self.setLinkRate();
                        // _self.setLinkRateInterval();

                        //设备告警
                        // _self.getAllNodeAlarm();
                    },

                    //设置透明度来实现闪烁效果
                    tickModel: function (paramObj, node, nodeSelectColor) {
                        if (paramObj.inCrease) {
                            paramObj.fillAlpha += 0.13;
                            if (paramObj.fillAlpha >= 0.9) {
                                paramObj.fillAlpha = 0.9;
                                paramObj.inCrease = false;
                            }
                        } else {
                            paramObj.fillAlpha -= 0.13;
                            if (paramObj.fillAlpha <= 0.1) {
                                paramObj.fillAlpha = 0.1;
                                paramObj.inCrease = true;
                            }
                        }
                        var color = this.createColor(nodeSelectColor, paramObj.fillAlpha);
                        node.setStyle('body.type', 'default.vector');
                        node.setStyle('image.padding', -5);
                        node.setStyle('select.style', 'none');
                        node.setStyle('vector.shape', 'circle');
                        // node.setStyle('vector.gradient', 'radial.center');
                        node.setStyle('vector.fill', true);
                        node.setStyle('vector.fill.color', color);
                    },

                    setTick: function (paramObj, node, nodeSelectColor) {
                        var _self = this;
                        var nodeInterval = setInterval(function () {
                            _self.tickModel(paramObj, node, nodeSelectColor);
                        }, 1200);
                        console.log(nodeInterval);
                        node.setClient("timeInterval", nodeInterval);
                    },

                    createColor: function (rgb, alpha) {
                        if (typeof rgb == "string" && rgb == "red") {
                            //紧急
                            rgb = '#c11212';
                        }
                        if (typeof rgb == "string" && rgb == "orange") {
                            //重要
                            rgb = '#ec670b';
                        }
                        if (typeof rgb == "string" && rgb == "yellow") {
                            //次要
                            rgb = '#ffc267';
                        }
                        if (typeof rgb == "string" && rgb == "blue") {
                            //警告
                            rgb = '#22a5df';
                        }
                        if (typeof rgb == "string" && rgb.indexOf('#') == 0) {
                            //
                            rgb = parseInt(rgb.substring(1, rgb.length), 16);
                        }
                        var r = (rgb >> 16) & 0xFF;
                        var g = (rgb >> 8) & 0xFF;
                        var b = rgb & 0xFF;
                        return 'rgba(' + r + ',' + g + ',' + b + ',' + alpha.toFixed(3) + ')';
                    },
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

            window.onbeforeunload = function () {
                window.clearInterval(sessionStorage.linkRateInterval)
            }
        }
    }
    netTopo();
    return {netTopo: netTopo}
})