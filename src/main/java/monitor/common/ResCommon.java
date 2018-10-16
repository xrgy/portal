package monitor.common;

import monitorConfig.common.CommonEnum;
import org.springframework.http.HttpStatus;

/**
 * Created by gy on 2018/10/16.
 */
public class ResCommon {
    public static ResultMsg genSimpleResByBool(boolean flag){
        ResultMsg msg = new ResultMsg();
        if (flag) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }
}
