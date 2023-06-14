package com.itonse.cms.domain.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Aes256UtilTest {


    @Test
    void encrypt() {   // 암,복호화 동시 테스트
        String encrypt = Aes256Util.encrypt("Hello world");
        assertEquals(Aes256Util.decrypt(encrypt), "Hello world");
    }

}