package business.service;


/**
 * Created by gy on 2018/3/31.
 */
public interface BusinessService {

    /**
     * 设备是否已经被加入了业务监控
     * @param monitorUuid
     * @return
     */
    boolean isJoinBusinessMonitor(String monitorUuid);
}
