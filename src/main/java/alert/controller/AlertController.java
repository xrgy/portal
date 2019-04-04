package alert.controller;

import alert.entity.AlertView;
import alert.service.AlertService;
import business.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.common.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by gy on 2018/3/24.
 */
@Controller
@RequestMapping("alert")
public class AlertController {


    @Autowired
    private AlertService service;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping("showAlert")
    public String showAlert(){
        return "alert/alertList";
    }


    @RequestMapping("getAlertInfo")
    @ResponseBody
    public ResultMsg getAlertInfo(AlertView view) throws JsonProcessingException {
        return service.getAlertInfo(view);
    }
}
