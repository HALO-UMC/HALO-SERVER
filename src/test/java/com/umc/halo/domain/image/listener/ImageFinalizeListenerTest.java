package com.umc.halo.domain.image.listener;

import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.image.service.ImageService;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 확정 리스너는 copy -> (imageKey==pendingKey 인 경우 갱신) -> pending 삭제 순서를 지켜야 함.
 * 갱신이 0건이면 delete를 건너뛰어야 함
 * 실패해도 재시도 없이 로그만 남기고 조용히 종료되어야 함
 */
@ExtendWith(MockitoExtension.class)
class ImageFinalizeListenerTest {

    @Mock
    private ImageService imageService;
    @Mock
    private MemberChapterRepository memberChapterRepository;

    @InjectMocks
    private ImageFinalizeListener imageFinalizeListener;

    private final ImageFinalizeRequestedEvent event =
            new ImageFinalizeRequestedEvent(1L, "pending/images/1/a.png", "images/1/a.png");

    @Test
    void 성공하면_copy_원자적갱신_delete_순서로_실행된다() {
        given(memberChapterRepository.updateImageKeyIfPending(1L, "pending/images/1/a.png", "images/1/a.png"))
                .willReturn(1);

        imageFinalizeListener.handle(event);

        InOrder inOrder = inOrder(imageService, memberChapterRepository);
        inOrder.verify(imageService).copyToFinal("pending/images/1/a.png", "images/1/a.png");
        inOrder.verify(memberChapterRepository).updateImageKeyIfPending(1L, "pending/images/1/a.png", "images/1/a.png");
        inOrder.verify(imageService).deleteObject("pending/images/1/a.png");
    }

    @Test
    void 갱신이_0건이면_경합으로_보고_delete를_건너뛴다() {
        given(memberChapterRepository.updateImageKeyIfPending(eq(1L), anyString(), anyString()))
                .willReturn(0);

        assertThatCode(() -> imageFinalizeListener.handle(event)).doesNotThrowAnyException();

        verify(imageService, never()).deleteObject(anyString());
    }

    @Test
    void copy가_실패하면_갱신과_delete를_건너뛰고_예외를_삼킨다() {
        doThrow(new RuntimeException("S3 down")).when(imageService).copyToFinal(anyString(), anyString());

        assertThatCode(() -> imageFinalizeListener.handle(event)).doesNotThrowAnyException();

        verify(memberChapterRepository, never()).updateImageKeyIfPending(anyLong(), anyString(), anyString());
        verify(imageService, never()).deleteObject(anyString());
    }

    @Test
    void delete가_실패해도_이미_반영된_갱신은_유지되고_예외만_삼킨다() {
        given(memberChapterRepository.updateImageKeyIfPending(1L, "pending/images/1/a.png", "images/1/a.png"))
                .willReturn(1);
        doThrow(new RuntimeException("S3 delete 실패")).when(imageService).deleteObject(anyString());

        assertThatCode(() -> imageFinalizeListener.handle(event)).doesNotThrowAnyException();

        verify(memberChapterRepository).updateImageKeyIfPending(1L, "pending/images/1/a.png", "images/1/a.png");
    }
}