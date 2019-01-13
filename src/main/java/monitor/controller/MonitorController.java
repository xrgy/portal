package monitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResultMsg;
import monitor.entity.view.OperationMonitorView;
import monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by gy on 2018/3/24.
 */
@Controller
@RequestMapping("monitor")
public class MonitorController {


    @Autowired
    private MonitorService service;

    @Autowired
    private ObjectMapper mapper;


    @RequestMapping("/addNetworkMonitorRecord")
    @ResponseBody
    public ResultMsg addNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.addNetworkMonitorRecord(view);
    }
    @RequestMapping("/addDbMonitorRecord")
    @ResponseBody
    public ResultMsg addDbMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.addDataBaseMonitorRecord(view);
    }
    @RequestMapping("/addVirtualMonitorRecord")
    @ResponseBody
    public ResultMsg addVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.addVirtualMonitorRecord(view);
    }
    @RequestMapping("/addMiddleWareMonitorRecord")
    @ResponseBody
    public ResultMsg addMiddleWareMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.addMiddleWareMonitorRecord(view);
    }
    @RequestMapping("/addContainerMonitorRecord")
    @ResponseBody
    public ResultMsg addContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
//        return service.addContainerMonitorRecord(view);
        return null;
    }
    @RequestMapping("/delNetworkMonitorRecord")
    @ResponseBody
    public ResultMsg delNetworkMonitorRecord(List<String> uuids) throws JsonProcessingException {
        return service.delNetworkMonitorRecord(uuids);
    }

    @RequestMapping("/getMonitorRecord")
    @ResponseBody
    public ResultMsg getMonitorRecord(String uuid) throws JsonProcessingException {
        return service.getMonitorRecord(uuid);
    }


    @RequestMapping("/updateNetworkMonitorRecord")
    @ResponseBody
    public ResultMsg updateNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.updateNetworkMonitorRecord(view);
    }


    @RequestMapping("/getBusinessMonitorRecord")
    @ResponseBody
    public ResultMsg getBusinessMonitorRecord() throws JsonProcessingException {
        return service.getBusinessMonitorRecord();
    }


    @RequestMapping("/getContainerList")
    @ResponseBody
    public ResultMsg getContainerList(String ip,String apiPort) throws JsonProcessingException {
        return service.getContainerListByExporter(ip,apiPort);
    }





}
