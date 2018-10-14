package monitor.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/6/8.
 */
@Getter
@Setter
public class ResultMsg {
    private int code;
    private String msg;
    private Object data;
}
