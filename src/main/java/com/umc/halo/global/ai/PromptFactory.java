package com.umc.halo.global.ai;

import java.util.List;

public final class PromptFactory {

    private PromptFactory() {}

    public static String chapterSummary(String chapterTitle, List<QuestionAnswer> questionAnswers) {

        return """
                당신은 가족 관계 기록을 정리하는 AI입니다.

                아래 사용자의 답변을 바탕으로
                스토리북에 들어갈 따뜻한 요약문을 작성해주세요.

                [장 제목]
                %s

                [질문과 답변]
                %s


                작성 조건:
                - 2~3문장으로 작성
                - 사용자의 감정과 경험이 드러나도록 작성
                - 없는 사실을 추가하지 말 것
                - 자연스럽고 따뜻한 문체 사용
                """
                .formatted(chapterTitle, formatQuestionAnswers(questionAnswers));
    }

    private static String formatQuestionAnswers(List<QuestionAnswer> questionAnswers) {

        StringBuilder qa = new StringBuilder();

        int index = 1;

        for (QuestionAnswer item : questionAnswers) {
            qa.append(index++)
                    .append(".\n")
                    .append("질문: ")
                    .append(item.question())
                    .append("\n")
                    .append("답변: ")
                    .append(item.answer())
                    .append("\n\n");
        }

        return qa.toString();
    }
}
