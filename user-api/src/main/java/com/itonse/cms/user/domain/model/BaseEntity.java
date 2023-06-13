package com.itonse.cms.user.domain.model;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass  // 테이블로 매핑X, 자식 클래스들에게 상속 (추상클래스)
@EntityListeners(value = {AuditingEntityListener.class})  // 상속받은 엔티티 클래스들의 생성일자와 수정일자를 자동 관리
public class BaseEntity {

    @CreatedDate
    private LocalDateTime createAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

}
