package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/10/19.
 */
@Getter
@Setter
public class VirtualMachine {

    private String id;

    private String name;

    private String status;

    private String os;

    private String ip;

    private boolean beenAdd;
}
