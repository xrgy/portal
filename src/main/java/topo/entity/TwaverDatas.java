package topo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/22.
 */
@Getter
@Setter
public class TwaverDatas {

    @JsonProperty("class")
    private String className;

    private int ref;

    @JsonProperty("p")
    private TwaverPosition twaverPosition;

    @JsonProperty("c")
    private TwaverClient twaverClient;
}
