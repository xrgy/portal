package monitorConfig.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import monitorConfig.entity.TestEntity;
import monitorConfig.service.MonitorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Created by gy on 2018/3/31.
 */
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String getJPA() {
//        objectMapper.writeValueAsString();
//        objectMapper.readValue("",TestEntity.class);
        ResponseEntity<TestEntity> response = rest().getForEntity("http://127.0.0.1:8085/monitorConfig/jpa",TestEntity.class);
        return response.getBody().getName();
    }

    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
}
