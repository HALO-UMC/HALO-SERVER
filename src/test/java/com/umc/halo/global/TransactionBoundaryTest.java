package com.umc.halo.global;

import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.image.listener.ImageFinalizeListener;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.service.MemberCreator;
import com.umc.halo.domain.member.service.MemberService;
import com.umc.halo.domain.member.service.MemberWriter;
import com.umc.halo.domain.record.dto.RecordReqDTO;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.service.ChapterRecordWriter;
import com.umc.halo.domain.record.service.RecordService;
import com.umc.halo.domain.record.service.RecordValidationReader;
import com.umc.halo.domain.record.service.ValidatedChapterRecord;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.event.ChapterCompletedEvent;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.service.NotificationTransactionService;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.global.ai.listener.AnniversaryNotificationListener;
import com.umc.halo.global.ai.listener.ChapterSummaryListener;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S3/JWKS/AI 같은 느린 외부 호출을 담은 메서드에는 @Transactional이 없고
 * 실제 DB 쓰기만 담당하는 메서드에는 @Transactional이 있어야 한다는 경계를 구조적으로 고정
 * 둘 중 하나라도 실수로 원상복구되지 않도록(예: 어노테이션을 다시 붙이거나 떼기) 검증
 */
class TransactionBoundaryTest {

    @TestFactory
    Stream<DynamicTest> 외부_호출을_포함한_메서드는_트랜잭션이_없어야_한다() throws NoSuchMethodException {
        List<Method> methods = List.of(
                MemberService.class.getDeclaredMethod("login", MemberReqDTO.Login.class),
                RecordService.class.getDeclaredMethod("writeChapterRecord", Long.class, RecordReqDTO.WriteChapterRecord.class),
                RecordService.class.getDeclaredMethod("validate", Long.class, RecordReqDTO.WriteChapterRecord.class),
                ImageFinalizeListener.class.getDeclaredMethod("handle", ImageFinalizeRequestedEvent.class),
                ChapterSummaryListener.class.getDeclaredMethod("generateSummary", ChapterCompletedEvent.class),
                AnniversaryNotificationListener.class.getDeclaredMethod("generateNotificationMessage", AnniversaryCreatedEvent.class),
                AnniversaryNotificationListener.class.getDeclaredMethod("updateNotificationMessage", AnniversaryUpdatedEvent.class)
        );

        return methods.stream().map(method ->
                DynamicTest.dynamicTest(method.getDeclaringClass().getSimpleName() + "." + method.getName(), () ->
                        assertThat(method.isAnnotationPresent(Transactional.class))
                                .as("%s는 외부 호출(S3/JWKS/AI)을 포함하므로 @Transactional이 없어야 함", method)
                                .isFalse()
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> 실제_DB_쓰기를_담당하는_메서드는_트랜잭션이_있어야_한다() throws NoSuchMethodException {
        List<Method> methods = List.of(
                MemberWriter.class.getDeclaredMethod("persist", Provider.class, OidcUserInfo.class),
                ChapterRecordWriter.class.getDeclaredMethod("persist", Long.class, RecordReqDTO.WriteChapterRecord.class, ValidatedChapterRecord.class),
                NotificationTransactionService.class.getDeclaredMethod("saveOrUpdateBoth", Anniversary.class, MemberSetting.class,
                        String.class, String.class, LocalDateTime.class, String.class, String.class, LocalDateTime.class, LocalDateTime.class),
                NotificationTransactionService.class.getDeclaredMethod("updateOrCancelBoth", Anniversary.class, MemberSetting.class,
                        String.class, String.class, LocalDateTime.class, boolean.class,
                        String.class, String.class, LocalDateTime.class, boolean.class, LocalDateTime.class),
                NotificationTransactionService.class.getDeclaredMethod("cancelBoth", Anniversary.class)
        );

        return methods.stream().map(method ->
                DynamicTest.dynamicTest(method.getDeclaringClass().getSimpleName() + "." + method.getName(), () ->
                        assertThat(method.isAnnotationPresent(Transactional.class))
                                .as("%s는 실제 DB 쓰기(락/원자성)를 담당하므로 @Transactional이 있어야 함", method)
                                .isTrue()
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> LAZY_연관_접근이_있는_조회_메서드는_트랜잭션이_있어야_한다() throws NoSuchMethodException {
        List<Method> methods = List.of(
                RecordValidationReader.class.getDeclaredMethod("load", Long.class, Long.class)
        );

        return methods.stream().map(method ->
                DynamicTest.dynamicTest(method.getDeclaringClass().getSimpleName() + "." + method.getName(), () ->
                        assertThat(method.isAnnotationPresent(Transactional.class))
                                .as("%s는 LAZY 연관 접근(Chapter.getStorybook() 등)이 있어 open-in-view 없이도 안전해야 하므로 @Transactional이 있어야 함", method)
                                .isTrue()
                )
        );
    }

    @org.junit.jupiter.api.Test
    void MemberCreator_create는_REQUIRES_NEW로_격리되어야_한다() throws NoSuchMethodException {
        Method method = MemberCreator.class.getDeclaredMethod("create", Provider.class, OidcUserInfo.class);
        Transactional annotation = method.getAnnotation(Transactional.class);

        assertThat(annotation)
                .as("%s는 실패해도 바깥 트랜잭션을 오염시키면 안 되므로 @Transactional이 있어야 함", method)
                .isNotNull();
        assertThat(annotation.propagation())
                .as("%s는 실패 시 이 트랜잭션만 롤백되어야 하므로 REQUIRES_NEW여야 함", method)
                .isEqualTo(Propagation.REQUIRES_NEW);
    }

    @org.junit.jupiter.api.Test
    void updateImageKeyIfPending은_REQUIRES_NEW로_명시적_트랜잭션이_있어야_한다() throws NoSuchMethodException {
        Method method = MemberChapterRepository.class.getDeclaredMethod(
                "updateImageKeyIfPending", Long.class, String.class, String.class);
        Transactional annotation = method.getAnnotation(Transactional.class);

        assertThat(annotation)
                .as("%s는 @Async 리스너(앰비언트 트랜잭션 없음)에서 호출되므로 명시적 @Transactional이 필요함 (실측: 없으면 TransactionRequiredException)", method)
                .isNotNull();
        assertThat(annotation.propagation())
                .as("%s는 REQUIRES_NEW여야 함", method)
                .isEqualTo(Propagation.REQUIRES_NEW);
    }
}