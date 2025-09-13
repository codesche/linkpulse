package com.linkpulse.link.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * 로컬/개발 환경에서 토픽 자동 생성.
 * 운영 환경에서는 IaC(배포 스크립트)로 생성하고, 이 빈을 비활성화할 수 있음.
 */

@Configuration
public class KafkaTopicConfig {

    @Value("${link.kafka.topic.click-raw:link-click-raw}")
    private String clickRawTopic;

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties props) {
        // buildAdminProperties()에 의존하지 않고, 버전 무관하게 직접 구성
        Map<String, Object> cfg = new HashMap<>();

        List<String> servers = props.getBootstrapServers();
        if (servers == null || servers.isEmpty()) {
            // spring.kafka.bootstrap-servers 가 비어있으면 즉시 알림
            throw new IllegalStateException("spring.kafka.bootstrap-servers is not set");
        }

        cfg.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", servers));
        // 선택: 타임아웃 등 기본값 보강
        // cfg.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);

        KafkaAdmin admin = new KafkaAdmin(cfg);
        admin.setFatalIfBrokerNotAvailable(false);      // dev에서 브로커 없어도 앱 기동 허용
        return admin;
    }

    @Bean
    public NewTopic clickRawTopic() {
        // partitions/replication 은 환경에 맞게 조정
        return new NewTopic(clickRawTopic, 3, (short) 1);
    }

}
