/**
 * Created by gy on 2018/3/24.
 */
import monitorConfig.controller.MonitorConfigController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication  // same as @Configuration @EnableAutoConfiguration @ComponentScan
@ComponentScan(basePackages= {"monitorConfig","monitor"})
//@ComponentScan(basePackageClasses = MonitorConfigController.class,Moni)
public class BingoApplication {
    public static void main(String[] args) throws Exception  {
        SpringApplication.run(BingoApplication.class,args);
    }
}
