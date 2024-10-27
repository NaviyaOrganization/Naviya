package com.mycom.myapp.naviya.domain.book.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookAiServiceImpl implements BookAiService {

    @Value("${spring.ai.openai.prompt}")
    private String openAiPrompt;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.prompt}")
    private String generateBookInfo;

    @Value("${spring.ai.openai.max-tokens}0")
    private int maxTokens;

    private final String dallEApiUrl = "https://api.openai.com/v1/images/generations";

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

    @Override
    public BookInsertDto generateBookInfo() {
        try {
            PromptTemplate template = new PromptTemplate(generateBookInfo);

            // 메시지 생성
            String messageContent = template.render();
            Message userMessage = new UserMessage(messageContent);
            Message systemMessage = new SystemMessage("translate to korean");

            // AI 모델에 메시지 전송 및 응답 받기
            String response = chatModel.call(userMessage, systemMessage);
            System.out.println(response);

            // 로그 기록
            log.info("Response from OpenAI: " + response);

            // GPT 응답을 DTO로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            BookInsertDto bookInsertDto = objectMapper.readValue(response, BookInsertDto.class);

            // DALL-E를 사용해 책 표지 이미지 생성
            String bookImageUrl = generateBookImage(bookInsertDto.getTitle(), bookInsertDto.getSummary());
            bookInsertDto.setBookImage(bookImageUrl);

            return bookInsertDto;
        } catch (Exception e) {
            log.error("Error occurred while generating full story", e);
            throw new RuntimeException("Full story generation failed", e);
        }
    }

    @Override
    public String generateBookImage(String title, String summary) {
        try {
            String prompt = "Create a book cover for a story titled: " + title + ". The summary of the story is: " + summary;
            String dallERequest = "{ \"prompt\": \"" + prompt + "\", \"n\": 1, \"size\": \"512x512\" }";

            // RestTemplate 사용
            RestTemplate restTemplate = new RestTemplate();

            // Spring의 HttpHeaders 사용
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // HttpEntity에 요청 본문과 헤더 추가
            HttpEntity<String> entity = new HttpEntity<>(dallERequest, headers);

            // DALL-E API 호출
            String dallEResponse = restTemplate.postForObject(dallEApiUrl, entity, String.class);

            // 응답에서 이미지 URL 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(dallEResponse);
            String imageUrl = root.path("data").get(0).path("url").asText();

            return imageUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("책 표지 이미지를 생성하는 도중 오류 발생", e);
        }
    }
}