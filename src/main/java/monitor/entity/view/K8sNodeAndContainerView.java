package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;
import monitor.entity.K8scontainerMonitorEntity;
import monitor.entity.K8snodeMonitorEntity;

import java.util.List;

/**
 * Created by gy on 2019/1/15.
 */
@Getter
@Setter
public class K8sNodeAndContainerView {

    K8snodeMonitorEntity k8snode;

    List<K8scontainerMonitorEntity> k8sContainerList;
}
