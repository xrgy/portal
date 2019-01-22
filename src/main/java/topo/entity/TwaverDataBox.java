package topo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/22.
 */
@Getter
@Setter
public class TwaverDataBox {
    //box的属性

    @JsonProperty("class")
    private String className;

    @JsonProperty("c")
    private TwaverClient twaverClient;
}
