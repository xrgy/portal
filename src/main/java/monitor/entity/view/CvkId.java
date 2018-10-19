package monitor.entity.view;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/10/19.
 */
@Getter
@Setter
public class CvkId {

    private String cvkId;

    private List<String> vmIds;
}
