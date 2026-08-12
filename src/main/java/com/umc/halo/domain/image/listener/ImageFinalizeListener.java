package com.umc.halo.domain.image.listener;


import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.image.service.ImageService;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageFinalizeListener {

    private final ImageService imageService;
    private final MemberChapterRepository memberChapterRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ImageFinalizeRequestedEvent event) {
        try {
            // pending -> final 복제
            imageService.copyToFinal(event.pendingKey(), event.finalKey());

            // imageKey가 여전히 pendingKey일 때만 finalKey로 갱신
            int updated = memberChapterRepository.updateImageKeyIfPending(
                    event.memberChapterId(), event.pendingKey(), event.finalKey());

            if (updated == 0) {
                log.warn("이미지 확정 경합 또는 대상 없음, pending 삭제 건너뜀. memberChapterId={}, pendingKey={}",
                        event.memberChapterId(), event.pendingKey());
                return;
            }

            // pending 삭제
            imageService.deleteObject(event.pendingKey());
        } catch (S3Exception e) {
            // KMS 권한/키 설정 오류 로깅
            log.error("이미지 확정 중 S3 오류 (KMS 키/권한 설정 확인 필요). errorCode={}, statusCode={}, memberChapterId={}, pendingKey={}",
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null,
                    e.statusCode(), event.memberChapterId(), event.pendingKey(), e);
        } catch (SdkClientException e) {
            // S3 연결 불가 등 클라이언트 측 오류 로깅
            log.error("이미지 확정 중 S3 클라이언트 오류 (네트워크/엔드포인트 확인 필요). memberChapterId={}, pendingKey={}",
                    event.memberChapterId(), event.pendingKey(), e);
        } catch (Exception e) {
            log.warn("이미지 확정 실패 memberChapterId={}, pendingKey={}", event.memberChapterId(), event.pendingKey(), e);
        }
    }
}