package business.entity;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/3/31.
 */
@Getter
@Setter
public class BusinessEntity {


    private String uuid;

    private String name;

    private float busy_score;

    private float health_score;

    private float available_score;

    private String cluster;


}
