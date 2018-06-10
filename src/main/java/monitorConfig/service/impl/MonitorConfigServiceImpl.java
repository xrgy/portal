package monitorConfig.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import monitorConfig.common.CommonEnum;
import monitorConfig.common.ResultMsg;
import monitorConfig.dao.MonitorConfigDao;
import monitorConfig.entity.metric.NewTemplateView;
import monitorConfig.entity.TestEntity;
import monitorConfig.entity.metric.ResMetricInfo;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MonitorConfigDao dao;

    @Override
    public String getJPA() {
//        objectMapper.writeValueAsString();
//        objectMapper.readValue("",TestEntity.class);
        ResponseEntity<TestEntity> response = rest().getForEntity("http://127.0.0.1:8086/monitorConfig/jpa",TestEntity.class);
        return response.getBody().getName();
    }

    @Override
    public ResultMsg getMetricInfo(String lightType, String monitorMode) {
        ResultMsg msg = new ResultMsg();
        ResMetricInfo info = dao.getMetricInfo(lightType,monitorMode);
        if (null != info) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
            msg.setData(dao.getMetricInfo(lightType, monitorMode));
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }

    @Override
    public boolean isTemplateNameDup(String name) {
        return dao.isTemplateNameDup(name);
    }

    @Override
    public ResultMsg addTemplate(NewTemplateView view) {
        ResultMsg msg = new ResultMsg();
        boolean res = dao.addTemplate(view);
        if (res) {
            msg.setCode(HttpStatus.OK.value());
            msg.setMsg(CommonEnum.MSG_SUCCESS.value());
        }else {
            msg.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            msg.setMsg(CommonEnum.MSG_ERROR.value());
        }
        return msg;
    }

    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
}
