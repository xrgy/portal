package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/14.
 */
@Getter
@Setter
public class OperationMonitorView {

    private String ip;

    private String name;

    private String lightType;

    private String monitorMode;

    private String readcommunity;

    private String writecommunity;

    private String port;

    private String timeinterval;

    private String timeout;

    private String monitortemplate;

    //数据库
    private String userName;

    private String password;

    private String databaseName;

    //tomcat是否开启登录认证
    private boolean authentication;

    //模板
    private String casTemplate;

    private String casClusterTemplate;

    private String cvkTemplate;

    private String vmTemplate;

    private String k8sTemplate;

    private String k8sNodeTemplate;

    private String k8sContainerTemplate;

    /**
     * cas 1 2 3 仅监控cvk主机 监控所有cvk和vm 监控指定的cvk和vm
     * k8s 1 2 3 仅监控k8s 监控k8s和所有容器 监控k8s和指定容器
     */
    private String radioType;

}
