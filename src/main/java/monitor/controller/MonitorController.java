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


}
