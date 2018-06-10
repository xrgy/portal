package monitorConfig.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import monitorConfig.common.ResultMsg;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by gy on 2018/3/24.
 */
@Controller
@RequestMapping("monitorConfig")
public class MonitorConfigController {


    @Autowired
    private MonitorConfigService service;

    @Autowired
    private ObjectMapper mapper;


    @RequestMapping("/addMonitorConfig")
    public String monitorConfig(){
        return "monitorConfig/addMonitorConfig";
    }


    @RequestMapping("/getMetricInfo")
    @ResponseBody
    public ResultMsg getMetricInfo(String lightType,String monitorMode){
        return service.getMetricInfo(lightType,monitorMode);
    }
    @RequestMapping("/isTemplateNameDup")
    @ResponseBody
    public boolean isTemplateNameDup(String name){
        //返回true 未重复
        return service.isTemplateNameDup(name);
    }
    @RequestMapping("/addTemplate")
    @ResponseBody
    public ResultMsg addTemplate(HttpServletRequest request) throws IOException {
        String data = request.getParameter("templateData");
        NewTemplateView view = mapper.readValue(data, NewTemplateView.class);
        return service.addTemplate(view);
    }
}
