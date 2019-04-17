package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/4/17.
 */
@Getter
@Setter
public class TomcatAccessView {

    String ip;

    String username;

    String password;

    boolean authentication;

    String port;
}
