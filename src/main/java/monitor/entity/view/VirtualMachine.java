package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/19.
 */
@Getter
@Setter
public class VirtualMachine {

    private String uuid;

    //vm id
    private String id;

    //vm name
    private String name;

    //vm status
    private String status;

    //vm os
    private String os;

    //vm ip
    private String ip;

    private boolean beenAdd;

    private String cvkId;

    private String cvkName;

    private String hostpoolId;
}
