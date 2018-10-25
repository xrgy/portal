package business.controller;

import business.service.BusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


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


}
