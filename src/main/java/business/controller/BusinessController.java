package business.controller;

import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * Created by gy on 2018/3/24.
 */
@Controller
@RequestMapping("business")
public class BusinessController {


    @Autowired
    private BusinessService service;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping("showBusinessList")
    public String showBusinessList(){
        return "business/businessList";
    }

    @RequestMapping("/getBusinessList")
    @ResponseBody
    public ResultMsg getBusinessList() throws JsonProcessingException {
        return service.getBusinessList();
    }

    @RequestMapping("/addBusinessResource")
    @ResponseBody
    public ResultMsg addBusinessResource(String businessId,List<DelMonitorRecordView> view) throws JsonProcessingException {
        return service.addBusinessResource(businessId,view);
    }


}
