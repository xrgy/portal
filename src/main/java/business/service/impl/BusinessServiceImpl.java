package business.service.impl;

import business.dao.BusinessDao;
import business.service.BusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    private static final String RULE_ANME_START="rule";
    private static final String AVL_RULE_NAME="_avl";
    private static final String ONE="one";
    private static final String TWO="two";
    private static final String ONE_LEVEL_PERF="_one_level_perf";
    private static final String TWO_LEVEL_PERF="_two_level_perf";


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BusinessDao dao;

    @Override
    public boolean isJoinBusinessMonitor(String monitorUuid) {
        return false;
    }
}
