package monitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResultMsg;
import monitor.entity.CasTransExporterModel;
import monitor.entity.DelMonitorRecordView;
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
        return service.addContainerMonitorRecord(view);
    }

    //全部类型的
    @RequestMapping("/delNetworkMonitorRecord")
    @ResponseBody
    public ResultMsg delNetworkMonitorRecord(List<DelMonitorRecordView> view) throws JsonProcessingException {
        return service.delNetworkMonitorRecord(view);
    }


//    @RequestMapping("/getMonitorRecord")
//    @ResponseBody
//    public ResultMsg getMonitorRecord(String uuid) throws JsonProcessingException {
//        return service.getMonitorRecord(uuid);
//    }


    @RequestMapping("/updateNetworkMonitorRecord")
    @ResponseBody
    public ResultMsg updateNetworkMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.updateNetworkMonitorRecord(view);
    }
    @RequestMapping("/updateMiddleMonitorRecord")
    @ResponseBody
    public ResultMsg updateMiddleMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.updateMiddleMonitorRecord(view);
    }
    @RequestMapping("/updateDbMonitorRecord")
    @ResponseBody
    public ResultMsg updateDbMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.updateDbMonitorRecord(view);
    }
    @RequestMapping("/updateVirtualMonitorRecord")
    @ResponseBody
    public ResultMsg updateVirtualMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.updateVirtualMonitorRecord(view);
    }

    //k8s
    @RequestMapping("/updateContainerMonitorRecord")
    @ResponseBody
    public ResultMsg updateContainerMonitorRecord(OperationMonitorView view) throws JsonProcessingException {
        return service.updateContainerMonitorRecord(view);
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

    @RequestMapping("/getCvkAndVmList")
    @ResponseBody
    ResultMsg getCvkAndVmListByExporter(CasTransExporterModel casTransExporterModel) throws JsonProcessingException {
        return service.getCvkAndVmListByExporter(casTransExporterModel);
    }




}
