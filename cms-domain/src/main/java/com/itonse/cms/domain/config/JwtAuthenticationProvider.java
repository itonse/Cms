package com.itonse.cms.domain.config;

import com.itonse.cms.domain.domain.common.UserVo;
import com.itonse.cms.domain.domain.common.UserType;
import com.itonse.cms.domain.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Objects;

public class JwtAuthenticationProvider {  // 시크릿키를 HS256으로 알고리즘을 해서 JWT 의 'VERIFY SIGNATURE' 에 넣음
    private String secretKey = "secretKey";

    private long tokenValidTime = 1000L * 60 * 60 * 24;   // JWT 토큰 유효시간: 1일 (로그인 하고 하루 지나면 토큰이 만료되어 다시 로그인 필요)

    public String createToken(String userPk, Long id, UserType userType) {   // 토큰 생성

        Claims claims = Jwts.claims()  // JWT 의 클레임 객체 생성
                .setSubject(Aes256Util.encrypt(userPk))  // Subject 클레임에 userPk 암호화 값,
                .setId(Aes256Util.encrypt(id.toString()));  // ID 클레임에 id.toString() 암호화 값 설정

        claims.put("roles", userType);  // 클레임에 사용자 유형 추가
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)  // 발급시간
                .setExpiration(new Date(now.getTime() + tokenValidTime))  // 토큰 만료시간 (현재 +1일)
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 서명 알고리즘, 시크릿키로 토큰에 서명
                .compact();  // 문자열 형태로
    }

    public boolean validateToken(String jwtToken) {     // 토큰의 Validation 체크
        try {
            Jws<Claims> claimsJws = Jwts.parser()   // 토큰 검증 파서 생성
                    .setSigningKey(secretKey)   // 시크릿키 설정 (서명 확인)
                    .parseClaimsJws(jwtToken);   // 토큰의 클레임을 반환하는 JWT 객체 생성

            return !claimsJws.getBody().getExpiration().before(new Date());  // 토큰 유효기간이 남았는지 여부 반환
        } catch (Exception e) {
            return false;
        }
    }

    public UserVo getUserVo(String token) {   // JWT 토큰으로 유저 Value Object 추출
        Claims c = Jwts.parser()   // JWT 토큰을 파싱하여 토큰의 클레임 객체 반환
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();

        return new UserVo(   // 아래 값들로 유저정보 객체 생성
                Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(c.getId()))),  // ID 값 복호화
                Aes256Util.decrypt(c.getSubject())  // Subject 값 복호화
        );
    }
}
