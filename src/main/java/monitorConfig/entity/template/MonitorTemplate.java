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
    List<RuleTemplate> ssh;

    List<RuleTemplate> wmi;

    List<RuleTemplate> snmp_v2;

    List<RuleTemplate> snmp_v1;

    List<RuleTemplate> k8s;

    List<RuleTemplate> k8sn;

    List<RuleTemplate> k8sc;

    List<RuleTemplate> cas;

    List<RuleTemplate> cascluster;

    List<RuleTemplate> cvk;

    List<RuleTemplate> virtualMachine;

    List<RuleTemplate> other;
}
