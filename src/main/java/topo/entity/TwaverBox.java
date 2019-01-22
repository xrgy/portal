package topo.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/22.
 */
@Getter
@Setter
public class TwaverBox {

    private String version;

    private String platform;

    private TwaverDataBox dataBox;

    private TwaverDatas[] datas;
}
