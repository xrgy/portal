package monitor.common;

/**
 * Created by gy on 2018/6/8.
 */
public interface MonitorEnum {

    enum MonitorRecordEnum {
        SNMP_VERSION_V1("snmp_v1"),
        SNMP_VERSION_V2("snmp_v2c"),
        SNMP("snmp");

        private String value;

        MonitorRecordEnum(String msg) {
            this.value = msg;
        }

        public String value() {
            return this.value;
        }
    }
}
