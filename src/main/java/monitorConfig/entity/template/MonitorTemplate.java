package monitorConfig.entity.template;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.digester.Rule;

import java.util.List;

/**
 * Created by gy on 2018/10/21.
 */
@Getter
@Setter
public class MonitorTemplate {
    List<NewRuleTemplate> ssh;

    List<NewRuleTemplate> wmi;

    List<NewRuleTemplate> snmp_v2;

    List<NewRuleTemplate> snmp_v1;

    List<NewRuleTemplate> k8s;

    List<NewRuleTemplate> k8sn;

    List<NewRuleTemplate> k8sc;

    List<NewRuleTemplate> cas;

    List<NewRuleTemplate> cascluster;

    List<NewRuleTemplate> cvk;

    List<NewRuleTemplate> virtualMachine;

    List<NewRuleTemplate> other;
}
