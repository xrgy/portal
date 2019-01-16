package business.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/3/31.
 */
@Getter
@Setter
public class BusinessResourceEntity {

    private String uuid;

    private String businessUuid;

    private String monitorId;

    private float busy_score;

    private float health_score;

    private float available_score;

    private String lightType;

}
