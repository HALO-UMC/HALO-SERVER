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

            // DB 저장
            memberChapterRepository.findById(event.memberChapterId())
                    .ifPresent(memberChapter -> {
                        memberChapter.updateImageKey(event.finalKey());
                        memberChapterRepository.save(memberChapter);
                    });

            // pending 삭제
            imageService.deleteObject(event.pendingKey());
        } catch (Exception e) {
            log.warn("이미지 확정 실패 memberChapterId={}, pendingKey={}", event.memberChapterId(), event.pendingKey(), e);
        }
    }
}