package monitorConfig.controller;

import monitorConfig.entity.TestEntity;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by gy on 2018/3/24.
 */
@Controller
public class MonitorConfigController {


    @Autowired
    private MonitorConfigService service;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/monitorConfig/addMonitorConfig")
    public String monitorConfig(){
        return "monitorConfig/addMonitorConfig";
    }

    @RequestMapping("/getJPAInfo")
    @ResponseBody
    public TestEntity getJPA(HttpServletRequest request,TestEntity t){

        TestEntity entity = new TestEntity();
        entity.setId("sasada");
        entity.setName("gygy");
        return entity;
    }
}
