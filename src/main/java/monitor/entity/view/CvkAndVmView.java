package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;
import monitor.entity.HostMonitorEntity;
import monitor.entity.VmMonitorEntity;

import java.util.List;

/**
 * Created by gy on 2019/1/15.
 */
@Getter
@Setter
public class CvkAndVmView {

    HostMonitorEntity hostMonitor;

    List<VmMonitorEntity> vmMonitorList;
}
