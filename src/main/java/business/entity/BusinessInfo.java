package business.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2019/4/2.
 */
@Getter
@Setter
public class BusinessInfo {
    private String uuid;

    private String name;

    private float busy_score;

    private float health_score;

    private float available_score;

    private String cluster;

    private List<BusinessMonitorEntity> resourceList;
}
