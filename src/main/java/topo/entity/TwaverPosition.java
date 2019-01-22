package topo.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by gy on 2019/1/22.
 */
@Getter
@Setter
public class TwaverPosition {

    private Map<String,Integer> location;

    private int width;

    private int height;

}
