package com.mycom.myapp.naviya.domain.book.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookAiServiceImpl implements BookAiService {

    private final BookService bookService;

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
            String messageContent = template.render();
            Message userMessage = new UserMessage(messageContent);
            Message systemMessage = new SystemMessage("translate to korean");

            String response = chatModel.call(userMessage, systemMessage);
            System.out.println(response);
            log.info("Response from OpenAI: " + response);

            ObjectMapper objectMapper = new ObjectMapper();
            BookInsertDto bookInsertDto = objectMapper.readValue(response, BookInsertDto.class);

            // Generate a real book cover image using DALL-E and set it in bookInsertDto
            String bookImg = generateBookImage(bookInsertDto.getTitle(), bookInsertDto.getSummary());
            bookInsertDto.setBookImage(bookImg); // This now holds the Base64 string of the image

            return bookInsertDto;
        } catch (Exception e) {
            log.error("Error occurred while generating full story", e);
            throw new RuntimeException("Full story generation failed", e);
        }
    }

    @Override
    public String generateBookImage(String title, String summary) {
        try {
            String prompt = "Create a book cover for a story titled: '" + title + "'." +
                    " The summary of the story is: '" + summary + "'." +
                    " Ensure that any text, especially the title on the book cover, is written in clear and legible English.";
            String dallERequest = "{ \"prompt\": \"" + prompt + "\", \"n\": 1, \"size\": \"512x512\" }";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(dallERequest, headers);
            String dallEResponse = restTemplate.postForObject(dallEApiUrl, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(dallEResponse);

            JsonNode dataNode = root.path("data");
            if (!dataNode.isMissingNode() && dataNode.has(0)) {
                String imageUrl = dataNode.get(0).path("url").asText();

                // Fetch the image from the URL and convert it to Base64 string
                Resource resource = new UrlResource(imageUrl);
                try (InputStream inputStream = resource.getInputStream()) {
                    byte[] imageBytes = IOUtils.toByteArray(inputStream);
                    return Base64.getEncoder().encodeToString(imageBytes); // Convert to Base64 string
                }
            } else {
                log.warn("이미지 URL을 가져오지 못했습니다.");
                return Base64.getEncoder().encodeToString("이미지 생성 실패".getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("책 표지 이미지를 생성하는 도중 오류 발생", e);
        }
    }




}