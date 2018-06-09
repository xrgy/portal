package monitorConfig.controller;

import monitorConfig.common.ResultMsg;
import monitorConfig.entity.TestEntity;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by gy on 2018/3/24.
 */
@Controller
@RequestMapping("monitorConfig")
public class MonitorConfigController {


    @Autowired
    private MonitorConfigService service;


    @RequestMapping("/addMonitorConfig")
    public String monitorConfig(){
        return "monitorConfig/addMonitorConfig";
    }


    @RequestMapping("/getMetricInfo")
    @ResponseBody
    public ResultMsg getMetricInfo(String lightType,String monitorMode){
        return service.getMetricInfo(lightType,monitorMode);
    }
}
