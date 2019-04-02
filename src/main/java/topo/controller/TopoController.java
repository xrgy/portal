package topo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import topo.entity.TwaverBox;
import topo.service.TopoService;

import java.io.IOException;


/**
 * Created by gy on 2018/3/24.
 */
@Controller
@RequestMapping("topo")
public class TopoController {


    @Autowired
    private TopoService service;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping("showWeaveTopo")
    public String showWeaveTopo(){
        return "topo/topoMain";
    }

    @RequestMapping("showNetTopo")
    public String showNetTopo(){
        return "topo/editTopo";
    }

    @RequestMapping("getAllWeaveTopoData")
    @ResponseBody
    public ResultMsg getAllWeaveTopoData(String relUuid) throws JsonProcessingException {
        return service.getAllWeaveTopoData(relUuid);
    }
    @RequestMapping("getBusinessNode")
    @ResponseBody
    public ResultMsg getBusinessNode(String uuid) throws JsonProcessingException {
        return service.getBusinessNode(uuid);
    }

    @RequestMapping("getAllWeaveTopoLink")
    @ResponseBody
    public ResultMsg getAllWeaveTopoLink(String relUuid) throws JsonProcessingException {
        return service.getAllWeaveTopoLink(relUuid);
    }


    @RequestMapping("getAllNetTopoNode")
    @ResponseBody
    public ResultMsg getTopoNodeByCanvasId(String canvasId) throws JsonProcessingException {
        return service.getTopoNodeByCanvasId(canvasId);
    }

    @RequestMapping("getAllNetTopoLink")
    @ResponseBody
    public ResultMsg getTopoLinkByCanvasId(String canvasId) throws JsonProcessingException {
        return service.getTopoLinkByCanvasId(canvasId);
    }

    @RequestMapping("getTopoCanvasData")
    @ResponseBody
    public ResultMsg getTopoCanvasData(){
        return service.getTopoCanvasData();
    }

    @RequestMapping("getCanvasByType")
    @ResponseBody
    public ResultMsg getCanvasByType(String type) {
        return service.getCustomCanvas(type);
    }

    @RequestMapping("getAllNetTopoPort")
    @ResponseBody
    public ResultMsg getAllPorts(){
        return service.getAllPorts();
    }

    @RequestMapping("getCanvasAlarmInfo")
    @ResponseBody
    public ResultMsg getCanvasAlarmInfo(String canvasId) throws JsonProcessingException {
        return service.getCanvasAlarmInfo(canvasId);
    }

    @RequestMapping("getTopoLinkRate")
    @ResponseBody
    public ResultMsg getTopoLinkRate(String canvasId,String linkRate) throws JsonProcessingException {
        return service.getInterfaceRate(canvasId,linkRate);
    }
    @RequestMapping("saveTopo")
    @ResponseBody
    public ResultMsg saveTopo(String topojson) throws IOException {
        TwaverBox twaverBox = mapper.readValue(topojson,TwaverBox.class);
        return service.saveTopo(twaverBox);
    }
    @RequestMapping("saveBusinessTopo")
    @ResponseBody
    public ResultMsg saveBusinessTopo(String topojson) throws IOException {
        TwaverBox twaverBox = mapper.readValue(topojson,TwaverBox.class);
        return service.saveBusinessTopo(twaverBox);
    }
    @RequestMapping("deleteNetTopoNode")
    @ResponseBody
    public ResultMsg deleteNetTopoNode(String uuid){
        return ResCommon.genSimpleResByBool(service.deleteTopoResourceBymonitoruuid(uuid));
    }

    @RequestMapping("deleteNetTopoLink")
    @ResponseBody
    public ResultMsg deleteNetTopoLink(String uuid){
        return ResCommon.genSimpleResByBool(service.deleteTopoLinkByUuid(uuid));
    }

}
