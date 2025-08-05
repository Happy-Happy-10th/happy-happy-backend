package com.happyhappy.backend.prompt.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Tag(
        name = "Clova 프롬프트",
        description = "사용자 입력 문장을 기반으로 Clova Studio를 통해 일정 추천 정보를 생성하는 API입니다."
)
@RestController
public class PromptController {

    private static final Logger log = LoggerFactory.getLogger(PromptController.class);

    @Value("${clova.auth.token}")
    private String clovaAuthToken;

    @Operation(summary = "Clova 일정추천 프롬프트")
    @PostMapping("/chat")
    public ResponseEntity<ApiResponseMessage> chat(
            @RequestBody Map<String, Object> requestBodyMap) {
        Map<String, Object> parameters = (Map<String, Object>) requestBodyMap.get("parameters");

        String eventType = String.valueOf(parameters.get("eventType"));
        String title = String.valueOf(parameters.get("title"));
        String address = String.valueOf(parameters.get("address"));

        log.info("사용자 요청 - eventType: {}, title: {}, address: {}", eventType, title, address);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + clovaAuthToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String yearStr = String.valueOf(LocalDate.now().getYear());

        Map<String, Object> systemContent = new HashMap<>();
        systemContent.put("type", "text");
        systemContent.put("text",
                "당신은 '일정 추천 캘린더' 서비스를 위한 AI 비서입니다. 사용자의 문장을 이해하고, 공식 일정을 검색하기 위한 JSON 형식을 생성합니다.\n"
                        +
                        "\n" +
                        "[요약 목적]\n" +
                        "• 사용자의 입력 문장을 분석해 행사/채용/할인 등 관련 일정을 정리합니다.\n" +
                        "• 공식 홈페이지, 공식 SNS 우선 사용. 없을 경우 (예상) 표시 포함 가능\n" +
                        "• 출력 형식은 JSON 배열로 반환하며, 최대 4개의 일정만 포함하세요.\n" +
                        "\n" +
                        "[입력 해석 기준]\n" +
                        "사용자의 문장은 다음 세 가지 요소 중 2개 이상을 포함해야 합니다:\n" +
                        "- 주제어: 기관/브랜드명 (예: 고용노동부, 쿠팡, 11번가)\n" +
                        "- 시기: 연도, 월, 시즌 등 (예: " + yearStr + "년, 여름, 8월)\n" +
                        "- 정보유형: 공고, 지원, 채용, 할인, 이벤트 등\n" +
                        "※ 조건이 모호하거나 무관할 경우 [예외 조건]을 따릅니다.\n" +
                        "\n" +
                        "[출력 조건]\n" +
                        "1. 각 일정은 다음 항목 포함:\n" +
                        "   - 제목: 행사 또는 일정명 (40자 이내)\n" +
                        "   - 관련날짜: YYYY-MM-DD 또는 YYYY-MM-DD ~ YYYY-MM-DD\n" +
                        "   - 관련링크: 아래 두 개의 하위 항목을 포함하는 객체 형태로 구성\n" +
                        "       · \"홈페이지\": 브랜드 또는 주최사의 공식 홈페이지 URL (예: https://www.oliveyoung.co.kr)\n"
                        +
                        "       · \"세부안내\": 행사 관련 상세 페이지 또는 예측 URL (예: (예상) https://...)\n" +
                        "   - 행사에 대한 정보: 문장 단위 정보 2~10개 (각 문장 40단어 이내)\n" +
                        "   - (선택) 장소: 오프라인 행사 시 필수 / 불명확할 경우 (예상 장소) 표기\n" +
                        "\n" +
                        "2. 날짜 추정 기준:\n" +
                        "   - 동일 행사명이 1~2년 내 반복된 경우\n" +
                        "   - ‘정기’, ‘시즌’, ‘세일’ 등의 키워드 포함\n" +
                        "   - 날짜가 없으면 (예상) + 추정 근거 포함\n" +
                        "\n" +
                        "3. 출력 정렬 기준:\n" +
                        "   - 1순위: 공식 출처 일정 (신뢰도 81~99%)\n" +
                        "   - 2순위: 비공식 출처 일정 (신뢰도 40~80%)\n" +
                        "   - 3순위: 과거 반복 일정 기반 (신뢰도 표기)\n" +
                        "\n" +
                        "4. 신뢰도 표기:\n" +
                        "   - 공식 일정: 신뢰도 90~99%\n" +
                        "   - 커뮤니티/블로그 기반: 신뢰도 40~80%\n" +
                        "   - 과거 일정 추정 시: 신뢰도 55~90%\n" +
                        "   - 신뢰도는 다음 기준에 따라 수치화합니다:\n" +
                        "     · 최근 게시 날짜\n" +
                        "     · 공식 키워드와의 일치도\n" +
                        "     · 출처 유형 (공식/비공식)\n" +
                        "     · 구조화된 정보 여부 (날짜/타이틀/기간 명시)\n" +
                        "\n" +
                        "5. 출력 항목 순서 고정:\n" +
                        "   - 반드시 다음 순서로 출력: \"제목\" → \"관련날짜\" → \"관련링크\" → \"행사에 대한 정보\" → \"장소\"\n"
                        +
                        "\n" +
                        "[포맷 예외 처리]\n" +
                        "- 누락 항목 자동 보정:\n" +
                        "  - 관련날짜 누락 시, 과거 일정 기준 예상 날짜 추정\n" +
                        "  - 관련링크 없을 경우, 명칭 기반 검색 URL 또는 SNS 추정 링크 제공\n" +
                        "- JSON 구조 이상 발생 시, 예시 포맷 기준으로 자동 보정\n" +
                        "- 응답이 일반 문장일 경우 JSON 포맷으로 재출력 유도\n" +
                        "\n" +
                        "[예외 조건]\n" +
                        "[1] 정보 조회 불가:\n" +
                        "죄송하지만 요청하신 문장에 대해 현재 기준으로 확인 가능한 공식 행사 정보는 등록되어 있지 않습니다.\n" +
                        "이와 같은 행사는 기관별로 수시로 공지되며, 일정이 매월 고정되어 있지 않을 수 있습니다.\n" +
                        "고용노동부, 워크넷, 일모아, 사람인, 잡코리아 등의 공식 채널을 주기적으로 확인해 주세요.\n" +
                        "나중에 다시 검색해주시되 최신 정보를 기준으로 다시 안내해드리겠습니다.\n" +
                        "※ 일부 행사는 행사명 또는 주최사만 출력될 수 있습니다.\n" +
                        "\n" +
                        "[2] 일정 검색 조건으로 보기 어려운 경우:\n" +
                        "죄송하지만 입력하신 문장은 조건이 모호하거나 일정과 무관한 조합으로 검색이 어렵습니다.\n" +
                        "정확한 결과를 위해 아래 세 가지 정보를 포함해주세요:\n" +
                        "- 주제어 (예: 고용노동부, 공공기관 등)\n" +
                        "- 정보유형 (예: 정기할인, 모집공고 등)\n" +
                        "- 시기 (예: " + yearStr + "년 8월)\n" +
                        "\n" +
                        "※ 예시 질문: \"" + yearStr + "년 8월에 온라인으로 열리는 청년 대상 취업설명회 알려줘\"\n" +
                        "※ 일정 서비스 외 질문 예시: \"" + yearStr
                        + "년 여름에 가기 좋은 여행지\", \"무더위에 좋은 영화 추천\" → 응답 제외\n" +
                        "\n" +
                        "[출력 예시]\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"title\": \"2025 무신사 블랙 프라이데이\",\n" +
                        "    \"date\": \"(예상) 2025-11-26 ~ 2025-12-03\",\n" +
                        "    \"link\": {\n" +
                        "      \"homepageUrl\": \"https://www.musinsa.com\",\n" +
                        "      \"detailPageUrl\": \"(예상) https://www.musinsa.com/blackfriday\"\n" +
                        "    },\n" +
                        "    \"eventInfo\": [\n" +
                        "      \"국내 최대 규모의 패션 플랫폼 무신사가 주관하는 대규모 할인 행사입니다.\",\n" +
                        "      \"다양한 브랜드의 의류 및 액세서리 상품을 최대 80%까지 할인된 가격에 제공합니다.\",\n" +
                        "      \"오프라인 매장에서도 특별 기획전과 체험 이벤트를 만나볼 수 있습니다.\",\n" +
                        "      \"강남점에서는 VIP 고객 전용 라운지와 프리미엄 상품 전시가 예정되어 있습니다.\"\n" +
                        "    ]\n" +
                        "  }\n" +
                        "]"
        );

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        List<Object> systemContents = new ArrayList<>();
        systemContents.add(systemContent);
        systemMessage.put("content", systemContents);

        Map<String, Object> userContent = new HashMap<>();
        userContent.put("type", "text");
        if ("오프라인".equals(eventType)) {
            userContent.put("text", title + " / 장소: " + address);
        } else {
            userContent.put("text", title);
        }

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        List<Object> userContents = new ArrayList<>();
        userContents.add(userContent);
        userMessage.put("content", userContents);

        // 메시지 리스트
        List<Object> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        // 요청 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        requestBody.put("topP", 0.8);
        requestBody.put("topK", 0);
        requestBody.put("maxTokens", 512);
        requestBody.put("temperature", 0.5);
        requestBody.put("repetitionPenalty", 1.1);
        requestBody.put("stop", new ArrayList<>());
        requestBody.put("seed", 0);
        requestBody.put("includeAiFilters", true);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://clovastudio.stream.ntruss.com/v3/chat-completions/HCX-005",
                    requestEntity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body != null && body.get("result") != null) {
                Map<String, Object> result = (Map<String, Object>) body.get("result");
                Map<String, Object> message = (Map<String, Object>) result.get("message");
                String content = (String) message.get("content");

                log.info(
                        "\n==========  Clova 응답 START ==========\n{}\n==========  Clova 응답 END ==========",
                        content);

                if (content.contains("죄송")) {
                    ApiResponseMessage noDataResponse = new ApiResponseMessage(
                            ApiResponseCode.COMMON_ERROR_000003,
                            content);
                    return new ResponseEntity<>(noDataResponse, HttpStatus.OK);
                }

                // 백틱 제거
                String cleanedJson = content.replaceAll("(?s)^```json\\s*", "")
                        .replaceAll("(?s)\\s*```$", "")
                        .trim();

                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> eventList = mapper.readValue(
                        cleanedJson,
                        new TypeReference<List<Map<String, Object>>>() {
                        }
                );

                // eventInfo가 문자열일 경우 List<String>으로 변환
                for (Map<String, Object> event : eventList) {
                    Object info = event.get("eventInfo");
                    if (info instanceof String str) {
                        List<String> infoList = List.of(str.split("\n"));
                        event.put("eventInfo", infoList);
                    }
                }

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("list", eventList);

                ApiResponseMessage apiResponse = new ApiResponseMessage(
                        ApiResponseCode.COMMON_SUCCESS_000001, resultData);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            }

            ApiResponseMessage emptyResponse = new ApiResponseMessage(
                    ApiResponseCode.COMMON_ERROR_000003, "Clova 응답이 없습니다. 잠시 후 다시 시도해 주세요.");
            return new ResponseEntity<>(emptyResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.info("Clova 호출 실패: {}", e.getMessage(), e);
            ApiResponseMessage errorResponse = new ApiResponseMessage(
                    ApiResponseCode.INTER_SERVER_ERROR, "Clova 호출 실패: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}





