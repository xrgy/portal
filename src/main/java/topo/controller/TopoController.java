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
}
