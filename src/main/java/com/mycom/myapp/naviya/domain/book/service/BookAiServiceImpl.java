package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookAiServiceImpl implements BookAiService {

    @Value("${spring.ai.openai.prompt}")
    private String openAiPrompt;

    @Value("${spring.ai.openai.max-tokens}")
    private int maxTokens;

    private final ChatModel chatModel;

    @Override
    public BookAiResponseDto generateFullStory(BookAiRequestDto requestDto) {
        try {
            System.out.println(requestDto);
            PromptTemplate template = new PromptTemplate(openAiPrompt);
            template.add("summary", requestDto.getSummary());

            // 메시지 생성
            String messageContent = template.render();
            Message userMessage = new UserMessage(messageContent);
            Message systemMessage = new SystemMessage("translate to korean");

            // AI 모델에 메시지 전송 및 응답 받기
            String response = chatModel.call(userMessage, systemMessage);
            System.out.println(response);

            // 로그 기록
            log.info("Response from OpenAI: " + response);

            // 응답을 DTO로 변환하여 반환
            return new BookAiResponseDto(response);
        } catch (Exception e) {
            log.error("Error occurred while generating full story", e);
            throw new RuntimeException("Full story generation failed", e);
        }
    }
}