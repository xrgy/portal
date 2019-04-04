package business.controller;

import business.entity.BusinessMonitorEntity;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResultMsg;
import monitor.entity.DelMonitorRecordView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import business.entity.PageData;

import java.io.IOException;
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

    @RequestMapping("/getBusinessListByPage")
    @ResponseBody
    public ResultMsg getBusinessListByPage(PageData page) throws JsonProcessingException {
        return service.getBusinessListByPage(page);
    }

    @RequestMapping("/addBusinessResource")
    @ResponseBody
    public ResultMsg addBusinessResource(String businessId,List<DelMonitorRecordView> view) throws JsonProcessingException {
        return service.addBusinessResource(businessId,view);
    }
    @RequestMapping("/updateBusiness")
    @ResponseBody
    public ResultMsg updateBusiness(String businessId,String busname,String data) throws IOException {
        List<BusinessMonitorEntity> monitorEntityList = mapper.readValue(data, new TypeReference<List<BusinessMonitorEntity>>() {});
        return service.updateBusiness(businessId,busname,monitorEntityList);
    }

    //删除业务
    @RequestMapping("/delBusiness")
    @ResponseBody
    public ResultMsg delBusiness(String businessId) {
        return service.delBusiness(businessId);
    }

//    @RequestMapping("/getRelevatTopo")
//    @ResponseBody
//    public ResultMsg getRelevatTopo(String uuid) throws JsonProcessingException {
//        return service.getRelevatTopo(uuid);

    @RequestMapping("/getBusinessInfo")
    @ResponseBody
    public ResultMsg getBusinessInfo(String businessId) {
        return service.getBusinessInfo(businessId);
    }
    @RequestMapping("/delBusinessResource")
    @ResponseBody
    public ResultMsg delBusinessResource(String businessId,String monitorList) throws IOException {
        List<String> monitorIds = mapper.readValue(monitorList, new TypeReference<List<String>>() {});
        return service.delBusinessResource(businessId,monitorIds);
    }
}
