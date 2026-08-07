package com.umc.halo.domain.image.listener;

import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.image.service.ImageService;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 확정 리스너는 copy -> DB 반영 -> pending 삭제 순서를 지켜야 함
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
    void 성공하면_copy_DB반영_delete_순서로_실행된다() {
        MemberChapter memberChapter = MemberChapter.builder().id(1L).build();
        given(memberChapterRepository.findById(1L)).willReturn(Optional.of(memberChapter));

        imageFinalizeListener.handle(event);

        assertThat(memberChapter.getImageKey()).isEqualTo("images/1/a.png");

        InOrder inOrder = inOrder(imageService, memberChapterRepository);
        inOrder.verify(imageService).copyToFinal("pending/images/1/a.png", "images/1/a.png");
        inOrder.verify(memberChapterRepository).save(memberChapter);
        inOrder.verify(imageService).deleteObject("pending/images/1/a.png");
    }

    @Test
    void copy가_실패하면_DB반영과_delete를_건너뛰고_예외를_삼킨다() {
        doThrow(new RuntimeException("S3 down")).when(imageService).copyToFinal(anyString(), anyString());

        assertThatCode(() -> imageFinalizeListener.handle(event)).doesNotThrowAnyException();

        verify(memberChapterRepository, never()).save(any());
        verify(imageService, never()).deleteObject(any());
    }

    @Test
    void delete가_실패해도_이미_반영된_DB변경은_유지되고_예외만_삼킨다() {
        MemberChapter memberChapter = MemberChapter.builder().id(1L).build();
        given(memberChapterRepository.findById(1L)).willReturn(Optional.of(memberChapter));
        doThrow(new RuntimeException("S3 delete 실패")).when(imageService).deleteObject(anyString());

        assertThatCode(() -> imageFinalizeListener.handle(event)).doesNotThrowAnyException();

        assertThat(memberChapter.getImageKey()).isEqualTo("images/1/a.png");
        verify(memberChapterRepository).save(memberChapter);
    }
}