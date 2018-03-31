import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by gy on 2018/3/24.
 */
@Controller
public class ApplicationController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }
}
