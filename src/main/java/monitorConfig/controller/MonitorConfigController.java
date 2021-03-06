package monitorConfig.controller;

import business.entity.PageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResCommon;
import monitor.common.ResultMsg;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.metric.UpTemplateView;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

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

    @RequestMapping("showTemplateList")
    public String showAlert(){
        return "monitorConfig/monitorConfigList";
    }



    @RequestMapping("/getMetricInfo")
    @ResponseBody
    public ResultMsg getMetricInfo(String lightType,String monitorMode){
        return service.getMetricInfo(lightType,monitorMode);
    }
    @RequestMapping("/isTemplateNameDup")
    @ResponseBody
    public boolean isTemplateNameDup(HttpServletRequest request){
        //返回true 未重复
        String name = request.getParameter("name");
        String templateUuid = request.getParameter("templateUUid");
        return service.isTemplateNameDup(name,templateUuid);
    }


    @RequestMapping("/addTemplate")
    @ResponseBody
    public ResultMsg addTemplate(HttpServletRequest request) throws IOException {
        String data = request.getParameter("templateData");
        NewTemplateView view = mapper.readValue(data, NewTemplateView.class);
        return service.addTemplate(view);
    }

    @RequestMapping("/OpenTemplate")
    @ResponseBody
    public ResultMsg openTemplate(String uuid) throws IOException {
        return service.OpenTemplate(uuid);
    }

    @RequestMapping("/updateTemplate")
    @ResponseBody
    public ResultMsg updateTemplate(HttpServletRequest request) throws IOException {
        String data = request.getParameter("templateData");
        NewTemplateView view = mapper.readValue(data, NewTemplateView.class);
        return service.updateTemplate(view);
    }


    @RequestMapping("/getTemplateByLightType")
    @ResponseBody
    public ResultMsg getTemplateByLightType(String lightType,String monitorMode){
        return ResCommon.getCommonResultMsg(service.getTemplateByLightType(lightType,monitorMode));
    }

    @RequestMapping("delTemplate")
    @ResponseBody
    public ResultMsg delTemplate(String templateUuids) throws IOException {
        List<String> templateUuidList = mapper.readValue(templateUuids, new TypeReference<List<String>>() {});
        return service.delTemplate(templateUuidList);
    }


    @RequestMapping("getAllTemplate")
    @ResponseBody
    public ResultMsg getAllTemplate(PageData page,String type) throws JsonProcessingException {
        return service.getAllTemplate(page,type);
    }
}
