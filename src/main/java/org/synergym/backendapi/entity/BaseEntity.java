package org.synergym.backendapi.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "use_yn", length = 1)
    private Character useYn;

    // softDelete 메서드 - userYn을 N으로 변경
    public void softDelete(){
        this.useYn = 'N';
    }

    // 사용자 재가입 시 useYn을 Y로 활성화
    public void reactivate() {
        this.useYn = 'Y';
    }

    // useYn 디폴트값 Y
    @PrePersist
    public void prePersist() {
        if (this.useYn == null) {
            this.useYn = 'Y';
        }
    }


}
