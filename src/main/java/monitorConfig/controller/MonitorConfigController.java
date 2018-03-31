package monitorConfig.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by gy on 2018/3/24.
 */
@Controller
public class MonitorConfigController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/monitorConfig/addMonitorConfig")
    public String monitorConfig(){
        return "monitorConfig/addMonitorConfig";
    }
}
