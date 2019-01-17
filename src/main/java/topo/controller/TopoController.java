package topo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import topo.service.TopoService;


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

    @RequestMapping("getAllWeaveTopoNode")
    @ResponseBody
    public ResultMsg getAllWeaveTopoNode() throws JsonProcessingException {
        return service.getAllWeaveTopoNode();
    }

    @RequestMapping("getAllWeaveTopoLink")
    @ResponseBody
    public ResultMsg getAllWeaveTopoLink() throws JsonProcessingException {
        return service.getAllWeaveTopoLink();
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

    @RequestMapping("getCustomCanvas")
    @ResponseBody
    public ResultMsg getCustomCanvas() {
        return service.getCustomCanvas();
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
}
