package com.itonse.cms.order.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itonse.cms.order.domain.redis.Cart;
import com.itonse.cms.order.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.itonse.cms.order.exception.ErrorCode.CART_CHANGE_FAIL;

@Slf4j
@RequiredArgsConstructor   // redisTemplate 을 자동으로 생성
@Service
public class RedisClient {   // 클래스 타입 대로 데이터를 넣고 빼는데 도움을 주기 위해 작성

    // RedisTemplate : 직렬화와 역질렬화(객체를 저장,조회시), 연결관리, 트랜잭션 등을 지원
    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();    // (JSON <-> Java)변환 기능의 보조성 Util (편리함 위해 생성)

    /**
     * 키와 클래스 타입을 받아
     * Redis에서 해당 key에 대응하는 값을 조회한 후 원하는 타입으로 변환하여 반환
     * @param key : 타입 별 키
     * @param classType  : 반환하고자 하는 클래스 타입
     * @return <T> : 저장된 데이터를 classType 으로 반환
     */
    public <T> T get(Long key, Class<T> classType) {     // 편리성을 위해 만든 메소드
        return get(key.toString(), classType);     // 키가 Long 타입일 시, String 타입으로 변환해 아래 메소드 실행
    }

    private <T> T get(String key, Class<T> classType) {    // private
        // redisTemplate.opsForValue() : Redis에서 Value 작업을 수행하는 인터페이스 사용
        // .get(key) : 주어진 key(String)에 대응하는 Value 작업 수행
        // 조회된 Value는 JSON 문자열인데, 이 값을 String 타입으로 redisValue에 할당
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        if (ObjectUtils.isEmpty(redisValue)) {     // redisValue 가 empty 이면, null 반환
            return null;
        } else {    // null이 아닐 시, mapper를 통해 실제 타입으로 변환해서 반환
            try {
                // redisValue 인 JSON 문자열을 원하는 타입인 classType 으로 역직렬화
                return mapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                log.error("Parsing error", e);
                return null;
            }
        }
    }

    /**
     * key와 Cart객체를 받아서,
     * Cart 객체를 JSON 문자열로 변환한 후
     * Redis에 해당 key에 대응하는 값으로 저장
     * @param key : 타입 별 키
     * @param cart : Cart 객체
     */
    public void put(Long key, Cart cart) {    // 키가 Long 타입일 시, String 타입으로 변환하여 아래 메소드 실행
        put(key.toString(), cart);
    }

    private void put(String key, Cart cart) {
        try {
            // redisTemplate.opsForValue() : Redis에서 Value 작업을 수행하는 인터페이스 사용
            // mapper.writeValueAsString(cart) : cart 객체를 JSON 문자열로 직렬화
            redisTemplate.opsForValue().set(key, mapper.writeValueAsString(cart));
        } catch (JsonProcessingException e) {
            throw new CustomException(CART_CHANGE_FAIL);
        }
    }
}
