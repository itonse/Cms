import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
package com.itonse.cms.order.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// (** 여기서는 오버스펙이라 사용X **)테스트용으로 임베디드 Redis 서버를 구성하고 시작/중지하는 기능을 제공

@TestConfiguration    // 테스트에 사용되는 설정 클래스임을 나타냄
public class TestRedisConfig {    // 테스트용 임베디드 Redis 서버 설정

    private RedisServer redisServer;    //  Redis 서버의 상태를 관리(시작, 중지)

    public TestRedisConfig(RedisProperties redisProperties) {   // redisProperties: Redis 서버의 구성 정보(포트번호) 가짐
        this.redisServer = RedisServer.builder()
                .port(redisProperties.getPort())
                .build();
    }

    @PostConstruct  // 객체가 생성된 후 실행되는 초기화 메서드
    public void startRedis(){   // 임베디드 Redis 서버 시작
        redisServer.start();  // 위에서 설정한 서버 시작
    }
    @PreDestroy  // 객체가 소멸되기 전에 실행되는 메서드
    public void stopRedis() {    // 임베디드 Redis 서버 중지
        redisServer.stop();
    }
}

*/
