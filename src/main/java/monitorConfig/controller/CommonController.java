package monitorConfig.controller;

import monitorConfig.entity.TestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by gy on 2018/6/8.
 */
@Controller
public class CommonController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/getJPAInfo")
    @ResponseBody
    public TestEntity getJPA(HttpServletRequest request, TestEntity t){
        TestEntity entity = new TestEntity();
        entity.setId("sasada");
        entity.setName("gygy");
        return entity;
    }
}
