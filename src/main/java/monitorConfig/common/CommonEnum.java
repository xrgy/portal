package monitorConfig.common;

/**
 * Created by gy on 2018/6/8.
 */
public enum CommonEnum {

    MSG_SUCCESS("SUCCESS"),
    MSG_ERROR("ERROR");

    private String value;

    CommonEnum(String msg) {
        this.value = msg;
    }

    public String value() {
        return this.value;
    }
}
