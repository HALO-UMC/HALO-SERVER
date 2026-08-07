package com.umc.halo.global;

import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.image.listener.ImageFinalizeListener;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.service.MemberService;
import com.umc.halo.domain.member.service.MemberWriter;
import com.umc.halo.domain.record.dto.RecordReqDTO;
import com.umc.halo.domain.record.service.ChapterRecordWriter;
import com.umc.halo.domain.record.service.RecordService;
import com.umc.halo.domain.record.service.ValidatedChapterRecord;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.event.ChapterCompletedEvent;
import com.umc.halo.global.ai.listener.AnniversaryNotificationListener;
import com.umc.halo.global.ai.listener.ChapterSummaryListener;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
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
                ChapterRecordWriter.class.getDeclaredMethod("persist", Long.class, RecordReqDTO.WriteChapterRecord.class, ValidatedChapterRecord.class)
        );

        return methods.stream().map(method ->
                DynamicTest.dynamicTest(method.getDeclaringClass().getSimpleName() + "." + method.getName(), () ->
                        assertThat(method.isAnnotationPresent(Transactional.class))
                                .as("%s는 실제 DB 쓰기(락/원자성)를 담당하므로 @Transactional이 있어야 함", method)
                                .isTrue()
                )
        );
    }
}