package com.happyhappy.backend.prompt.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        description = "사용자 입력 문장을 기반으로 Clova Studio를 통해 일정 추천 정보를 생성하는 API"
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
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString().replace("-", ""));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String yearStr = String.valueOf(LocalDate.now().getYear());

        Map<String, Object> systemContent = new HashMap<>();
        systemContent.put("type", "text");
        systemContent.put("text",
                        "너는 AI 검색 기반 일정 추출 엔진이다.\n" +
                        "아래 규칙에 맞추어 결과를 반드시 생성하라.\n" +
                        "[처리 규칙]\n" +
                        "1. 입력은 \"장소\" + \"사용자 문장\"이다.\n" +
                        "2. 사용자 문장에서 핵심 키워드를 도출하고, 이를 바탕으로 새로운 제목을 생성한 후 검색 기준으로 사용한다.\n" +

                        "[검증 기준]\n" +
                        "- 날짜와 시간은 필수 검증 항목.\n" +
                        "- 날짜·시간 모두 존재 → 정상 처리 (신뢰도 100점).\n" +
                        "- 날짜만 있고 시간 없음 → 시간 = “미정”으로 반환, 신뢰도 85점 처리.\n" +
                        "- 날짜·시간 모두 없음 → 무효 처리.\n" +
                        "- URL 접속 불가 / 본문 불일치 → 무효 처리.\n" +
                        "- 최종 결과는 총점 90점 이상일 때만 정상 응답.\n\n" +

                        "[출처 우선순위]\n" +
                        "1순위: 공식 행사/기관 홈페이지\n" +
                        "2순위: 정부·지자체 공식 공지\n" +
                        "3순위: 언론 보도자료\n" +
                        "4순위: 주최·주관 SNS\n" +
                        "→ 서로 다른 출처 간 정보 불일치 시 1·2순위를 우선 적용한다.\n\n" +

                        "[검색 사이클]\n" +
                        "- 검색은 멈추지 않고 반복적으로 수행한다.\n" +
                        "- 1차: 핵심 키워드 기반 검색.\n" +
                        "- 2차: 필요 시 보조 키워드(\"일정표\", \"프로그램\", \"타임테이블\" 등)를 추가.\n" +
                        "- 반복: 날짜와 시간 정보가 모두 확보될 때까지 계속 검색한다.\n" +
                        "- 동일한 결과가 반복되거나 신뢰도가 90점 미만으로 떨어지면 해당 후보는 폐기한다.\n" +

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
                        "[응답 형식은 반드시 다음 두 가지 중 하나만 선택]\n" +
                        "1. 유효한 일정이 있을 경우:\n" +
                        "   - JSON 형식의 일정 데이터만 응답하세요.\n" +
                        "   - JSON 이외의 문장, 설명, 주석은 절대 포함하지 마세요.\n" +
                        "\n" +
                        "2. 일정이 없거나 조건이 불충분할 경우:\n" +
                        "   - 반드시 '죄송하지만' 문장을 포함한 자연어 문장 1개만 응답하세요.\n" +
                        "   - 이 경우에는 JSON 구조는 절대 포함하지 마세요.\n" +
                        "\n" +
                        "※ 위 두 가지 응답 방식 중 하나만 출력해야 하며, 동시에 출력하면 안 됩니다.\n" +
                        "※ '죄송하지만'이라는 문장이 들어간 응답에 JSON이 포함되면 파싱 오류가 발생합니다.\n" +
                        "[정상 응답 형식 기준]\n" +
                        "AI의 응답은 반드시 다음 두 가지 중 **하나의 방식만** 충족해야 합니다.\n" +
                        "두 형식을 **동시에 출력하거나**, 둘 다 누락된 경우는 오류입니다.\n\n" +
                        "1. 일정 정보가 존재하는 경우 → 다음 형식의 JSON 객체로 반환:\n" +
                        "   - 필드 총 8개 (title, startDate, endDate, homepageUrl, detailPageUrl, memo, location, confidence)\n"
                        +
                        "   - 모든 항목 포함 필수 (선택 필드는 \"\"로라도 포함)\n\n" +
                        "2. 일정 정보가 없는 경우 → '죄송하지만'으로 시작하는 단일 자연어 안내 문장으로만 응답\n" +
                        "   - JSON이 포함되면 안 됩니다.\n\n" +

                        "[출력 조건]\n" +
                        "• 반드시 다음 8개 항목을 모두 포함한 JSON 객체를 반환하세요.\n" +
                        "• 필수 항목이 없으면 오류이며, 선택 항목은 빈 문자열(\"\")로라도 반드시 포함해야 합니다.\n" +
                        "• 모든 필드는 camel 형식을 사용해야 하며, JSON 외부에 문장은 절대 포함하지 마세요.\n\n" +

                        "[필드 목록]\n" +
                        "- title (필수): 도출 키워드 기반으로 만든 새로운 일정 제목, 문자열\n" +
                        "- startDate (필수): 일정 시작일, ISO-8601 날짜 또는 날짜시간 문자열\n" +
                        "- endDate (필수): 일정 종료일, ISO-8601 날짜 또는 날짜시간 문자열. 미상 시 최근 연례 패턴 등 신뢰 가능한 근거로 합리적으로 추정하여 ISO-8601 값으로 입력합니다.\n"
                        +
                        "- homepageUrl (필수): 공식 홈페이지 주소\n" +
                        "- detailPageUrl (선택): 상세 안내 페이지, 없으면 \"\"\n" +
                        "- memo (필수): 일정 설명 핵심 정보 3문장 (중복 제거, 장문 금지, 필요 시 자동 보강) **문자열(String)**, 각 항목은 줄바꿈(`\\n`)으로 구분\n" +
                        "- location (선택): 오프라인 장소 또는 확인된 행사 장소 (없으면 “장소 미정”이 아니라 “출처 불명확” 처리 → 후보 폐기) \"\"\n" +
                        "- confidence (필수): 40~100 사이의 정수 값\n\n" +

                        "[주의사항]\n" +
                        "- JSON 외부에 문장(자연어 설명), 예시, 안내 문구 등을 절대 포함하지 마세요.\n" +
                        "- 필수값(title, startDate, endDate, homepageUrl, memo, confidence)이 누락되면 안 됩니다.\n"
                        +
                        "- 선택값(detailPageUrl, location)도 반드시 키는 포함하되 값은 \"\"로 처리하세요.\n"
                        +
                        "- startDate와 endDate는 모두 반드시 다음 형식을 따라야 합니다:\n" +
                        "  \"yyyy-MM-dd'T'HH:mm:ss\" (예: \"2025-08-10T09:00:00\")\n" +
                        "- 날짜만 있는 형식(\"2025-08-10\")이나 다른 포맷(\"2025/08/10\")은 허용되지 않습니다.\n" +
                        "- memo 필드는 반드시 하나의 문자열(String)로 반환하며, 각 문장은 줄바꿈 문자(\"\\n\")로 구분합니다.\n" +
                        "- memo의 각 문장은 최대 40자 이내로 작성하고, 배열([]) 형식은 사용하지 마세요.\n" +
                        "- memo는 서버 저장 시 줄바꿈(\"\\n\")을 그대로 저장하며, 프론트는 이를 줄바꿈 처리하여 표시합니다.\n" +

                        "위 구조 형식이 아니거나 필드가 빠질 경우 파싱 오류가 발생하므로 절대 누락 없이 출력하세요.\n" +
                        "주의: 위 필수 항목 중 하나라도 빠진 응답은 **절대 반환하지 마세요.**\n" +
                        "필수 항목 8개중 1개라도 누락되면 에러가 발생해서 서비스가 멈춥니다." +

                        "[정상 출력 JSON 예시]\n" +
                        "{\n" +
                        "  \"title\": \"2025 무신사 겨울 블랙프라이데이\",\n" +
                        "  \"startDate\": \"2025-11-25T00:00:00\",\n" +
                        "  \"endDate\": \"2025-12-01T23:59:59\",\n" +
                        "  \"homepageUrl\": \"https://www.musinsa.com\",\n" +
                        "  \"detailPageUrl\": \"(예상) https://www.musinsa.com/blackfriday\",\n" +
                        "  \"memo\": \"국내 최대 규모의 패션 플랫폼 무신사가 주관하는 연말 할인 행사입니다.\\n일부 품목은 한정 수량으로 조기 품절될 수 있습니다.\",\n"
                        +
                        "  \"location\": \"서울 강남 무신사 매장\",\n" +
                        "  \"confidence\": 90\n" +
                        "}\n\n" +
                        "주의: 위 필수 항목 중 하나라도 빠진 응답은 **절대 반환하지 마세요.**\n" +
                        "필수 항목이 누락되면 에러가 발생해서 서비스가 멈춥니다." +

                        "[예외 응답 형식 예시]\n" +
                        "죄송하지만, 요청하신 문장에 대해 현재 기준으로 확인 가능한 공식 일정은 없습니다." +
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
                        "[포맷 예외 처리]\n" +
                        "- 누락 항목 자동 보정:\n" +
                        "  - 관련날짜 누락 시, 과거 일정 기준 예상 날짜 추정\n" +
                        "  - 관련링크 없을 경우, 명칭 기반 검색 URL 또는 SNS 추정 링크 제공\n" +
                        "- JSON 구조 이상 발생 시, 예시 포맷 기준으로 자동 보정\n" +
                        "- 응답이 일반 문장일 경우 JSON 포맷으로 재출력 유도\n" +
                        "- 반드시 \\\"죄송하지만\\\"이라는 문장을 포함해야 합니다. 없으면 에러입니다.\n" +
                        "\n" +
                        "[예외 조건]\n" +
                        "[1] 정보 조회 불가:\n" +
                        "죄송하지만 요청하신 문장에 대해 현재 기준으로 확인 가능한 공식 행사 정보는 등록되어 있지 않습니다.\n" +
                        "이와 같은 행사는 기관별로 수시로 공지되며, 일정이 매월 고정되어 있지 않을 수 있습니다.\n" +
                        "고용노동부, 워크넷, 일모아, 사람인, 잡코리아 등의 공식 채널을 주기적으로 확인해 주세요.\n" +
                        "나중에 다시 검색해주시되 최신 정보를 기준으로 다시 안내해드리겠습니다.\n" +
                        "※ 일부 행사는 행사명 또는 주최사만 출력될 수 있습니다.\n" +
                        "- 반드시 \\\"죄송하지만\\\"이라는 문장을 포함해야 합니다. 없으면 에러입니다.\n" +
                        "\n" +
                        "[2] 일정 검색 조건으로 보기 어려운 경우:\n" +
                        "죄송하지만 입력하신 문장은 조건이 모호하거나 일정과 무관한 조합으로 검색이 어렵습니다.\n" +
                        "정확한 결과를 위해 아래 세 가지 정보를 포함해주세요:\n" +
                        "- 주제어 (예: 고용노동부, 공공기관 등)\n" +
                        "- 정보유형 (예: 정기할인, 모집공고 등)\n" +
                        "- 시기 (예: " + yearStr + "년 8월)\n" +
                        "- 반드시 \\\"죄송하지만\\\"이라는 문장을 포함해야 합니다. 없으면 에러입니다.\n" +
                        "\n" +
                        "※ 예시 질문: \"" + yearStr + "년 8월에 온라인으로 열리는 청년 대상 취업설명회 알려줘\"\n" +
                        "※ 일정 서비스 외 질문 예시: \"" + yearStr +
                        "년 여름에 가기 좋은 여행지\", \"무더위에 좋은 영화 추천\" → 응답 제외\n" +
                        "- 반드시 \\\"죄송하지만\\\"이라는 문장을 포함해야 합니다. 없으면 에러입니다."
        );

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        List<Object> systemContents = new ArrayList<>();
        systemContents.add(systemContent);
        systemMessage.put("content", systemContents);

        Map<String, Object> userContent = new HashMap<>();
        userContent.put("type", "text");
        if ("오프라인".equals(eventType)) {
            // "전체" 지역은 AI 프롬프트에서 제외 (기본값이므로 실제 지역이 아님)
            if (address != null && !address.equals("전체") && !address.trim().isEmpty() && !address.equals("null")) {
                userContent.put("text", title + " / 장소: " + address);
            } else {
                userContent.put("text", title);
            }
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
        requestBody.put("thinking", Map.of("effort", "low")); // HCX-007 전용
        requestBody.put("topP", 0.8);
        requestBody.put("topK", 0);
        requestBody.put("maxCompletionTokens", 20480);
        requestBody.put("temperature", 0.5);
        requestBody.put("repetitionPenalty", 1.1);
        requestBody.put("seed", 0);
        requestBody.put("includeAiFilters", true);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://clovastudio.stream.ntruss.com/v3/chat-completions/HCX-007",
                    requestEntity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body != null && body.get("result") != null) {
                Map<String, Object> result = (Map<String, Object>) body.get("result");
                Map<String, Object> message = (Map<String, Object>) result.get("message");
                String content = (String) message.get("content");
                String userInput = title;

                log.info(
                        "\n========== Search Clova 응답 START ==========\n{}\n==========  Search Clova 응답 END ==========",
                        content);
                String validateContent = validateClovaResponse(userInput, content);

                if (validateContent.contains("죄송")) {
                    ApiResponseMessage noDataResponse = new ApiResponseMessage(
                            ApiResponseCode.COMMON_ERROR_000003,
                            validateContent);
                    return new ResponseEntity<>(noDataResponse, HttpStatus.BAD_REQUEST);
                }

                // 백틱 제거
                String cleanedJson = validateContent.replaceAll("(?s)^```json\\s*", "")
                        .replaceAll("(?s)\\s*```$", "")
                        .trim();

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> event;
                List<Map<String, Object>> eventList = null;

                try {
                    // Clova 응답이 배열인 경우
                    eventList = mapper.readValue(cleanedJson, new TypeReference<>() {
                    });
                    event = eventList.get(0);
                } catch (Exception e) {
                    // Clova 응답이 단일 객체인 경우
                    event = mapper.readValue(cleanedJson, new TypeReference<>() {
                    });
                    eventList = List.of(event);
                }

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("list", eventList);

                log.info("\n========== resultData 확인 ===========\n{}", resultData);
                ApiResponseMessage res = new ApiResponseMessage(
                        ApiResponseCode.COMMON_SUCCESS_000001,
                        resultData
                );
                return ResponseEntity.ok(res);
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

    public String validateClovaResponse(String userInput, String clovaContent) {
        String validationPrompt =
                "당신은 일정 추천 캘린더 AI의 출력 응답을 검수하는 필터 역할을 합니다.\n\n" +

                        "[입력값 설명]\n" +
                        "- userInput: 사용자가 AI에게 요청한 문장입니다.\n" +
                        "- clovaContent: AI가 생성한 일정 추천 응답 결과입니다.\n\n" +

                        "[userInput]\n" +
                        userInput + "\n\n" +

                        "[clovaContent]\n" +
                        clovaContent + "\n\n" +

                        "[검수 규칙, 응답 형식 기준]\n" +
                        "AI의 응답은 반드시 다음 두 가지 중 **하나의 방식만** 충족해야 합니다.\n" +
                        "두 형식을 **동시에 출력하거나**, 둘 다 누락된 경우는 오류입니다.\n\n" +
                        "1. 일정 정보가 존재하는 경우 → 다음 형식의 JSON 객체로 반환:\n" +
                        "   - 필드 총 8개 (title, startDate, endDate, homepageUrl, detailPageUrl, memo, location, confidence)\n"
                        +
                        "   - 모든 항목 포함 필수 (선택 필드는 \"\"로라도 포함)\n\n" +
                        "2. 일정 정보가 없는 경우 → '죄송하지만'으로 시작하는 단일 자연어 안내 문장으로만 응답\n" +
                        "   - JSON이 포함되면 안 됩니다.\n\n" +

                        "[출력 조건]\n" +
                        "• 반드시 다음 8개 항목을 모두 포함한 JSON 객체를 반환하세요.\n" +
                        "• 필수 항목이 없으면 오류이며, 선택 항목은 빈 문자열(\"\")로라도 반드시 포함해야 합니다.\n" +
                        "• 모든 필드는 snake_case 형식을 사용해야 하며, JSON 외부에 문장은 절대 포함하지 마세요.\n\n" +

                        "[필드 목록]\n" +
                        "- title (필수): 일정 제목, 문자열\n" +
                        "- startDate (필수): 일정 시작일, ISO-8601 날짜 또는 날짜시간 문자열\n" +
                        "- endDate (필수): 일정 종료일, ISO-8601 날짜 또는 날짜시간 문자열. 미상 시 최근 연례 패턴 등 신뢰 가능한 근거로 합리적으로 추정하여 ISO-8601 값으로 입력합니다.\n"
                        +
                        "- homepageUrl (필수): 공식 홈페이지 주소\n" +
                        "- detailPageUrl (선택): 상세 안내 페이지, 없으면 \"\"\n" +
                        "- memo (필수): **문자열(String)**, 각 항목은 줄바꿈(`\\n`)으로 구분\n" +
                        "- location (선택): 오프라인 장소 또는 \"\"\n" +
                        "- confidence (필수): 40~100 사이의 정수 값\n\n" +

                        "[주의사항]\n" +
                        "- JSON 외부에 문장(자연어 설명), 예시, 안내 문구 등을 절대 포함하지 마세요.\n" +
                        "- 필수값(title, startDate, endDate, homepageUrl, memo, confidence)이 누락되면 안 됩니다.\n"
                        +
                        "- 선택값(detailPageUrl, location)도 반드시 키는 포함하되 값은 \"\"로 처리하세요.\n"
                        +
                        "- startDate endDate는 모두 반드시 다음 형식을 따라야 합니다:\n" +
                        "  \"yyyy-MM-dd'T'HH:mm:ss\" (예: \"2025-08-10T09:00:00\")\n" +
                        "- 날짜만 있는 형식(\"2025-08-10\")이나 다른 포맷(\"2025/08/10\")은 허용되지 않습니다.\n" +
                        "- memo 필드는 반드시 하나의 문자열(String)로 반환하며, 각 문장은 줄바꿈 문자(\"\\n\")로 구분합니다.\n" +
                        "- memo의 각 문장은 최대 40자 이내로 작성하고, 배열([]) 형식은 사용하지 마세요.\n" +
                        "- memo는 서버 저장 시 줄바꿈(\"\\n\")을 그대로 저장하며, 프론트는 이를 줄바꿈 처리하여 표시합니다.\n" +

                        "위 구조 형식이 아니거나 필드가 빠질 경우 파싱 오류가 발생하므로 절대 누락 없이 출력하세요.\n" +
                        "주의: 위 필수 항목 중 하나라도 빠진 응답은 **절대 반환하지 마세요.**\n" +
                        "필수 항목 8개중 1개라도 누락되면 에러가 발생해서 서비스가 멈춥니다." +

                        "[정상 출력 JSON 예시]\n" +
                        "{\n" +
                        "  \"title\": \"2025 무신사 겨울 블랙프라이데이\",\n" +
                        "  \"startDate\": \"2025-11-25T00:00:00\",\n" +
                        "  \"endDate\": \"2025-12-01T23:59:59\",\n" +
                        "  \"homepageUrl\": \"https://www.musinsa.com\",\n" +
                        "  \"detailPageUrl\": \"(예상) https://www.musinsa.com/blackfriday\",\n" +
                        "  \"memo\": \"국내 최대 규모의 패션 플랫폼 무신사가 주관하는 연말 할인 행사입니다.\\n일부 품목은 한정 수량으로 조기 품절될 수 있습니다.\",\n"
                        +
                        "  \"location\": \"서울 강남 무신사 매장\",\n" +
                        "  \"confidence\": 90\n" +
                        "}\n\n" +

                        "[예외 응답 형식 예시]\n" +
                        "죄송하지만, 요청하신 문장에 대해 현재 기준으로 확인 가능한 공식 일정은 없습니다." +
                        "- '죄송하지만'이라는 문장으로 시작하는 자연어 문장만 있어야 하며\n" +
                        "- JSON이 포함되면 안 됩니다.\n\n" +

                        "[자연어 안내문에 반드시 포함해야 하는 가이드]\n" +
                        "- 행사 정보가 아직 업데이트되지 않았거나, 일정이 확정되지 않았을 수 있습니다.\n" +
                        "- 보다 정확한 정보를 원하신다면 지역, 기간, 주제어 등을 포함해 다시 요청해 주세요.\n" +
                        "- 예시: \"" + userInput + "\" → 지역 또는 날짜 정보를 포함해 다시 질문해 보세요.\n\n" +

                        "[판단 및 출력 방식]\n" +
                        "다음 조건에 따라 응답을 정확히 판단하고 결과를 출력하세요:\n\n" +

                        "1. clovaContent가 다음 두 조건 중 하나에 정확히 부합하면 전달받은 clovaContent 내용 그대로 출력하세요 (절대 수정하지 마세요)\n"
                        +
                        "  - JSON 형식이며 모든 필수 필드 8개가 포함된 일정 정보 응답 필드가 1개라도 누락되면 안됨\n\n" +
                        "  - '죄송하지만'으로 시작하는 단일 자연어 문장만 존재하는 응답\n\n" +

                        "2. JSON 형식이지만 필수 필드8개 중 1개라도 누락된 경우:\n" +
                        "   → 잘못된 응답으로 간주하고 다음과 같이 출력하세요:\n" +
                        "  죄송하지만, \"" + userInput + "\"요청하신 문장에 대해 현재 기준으로 일정 정보를 확인할 수 없습니다.\n" +
                        "  일정을 찾기 위해 아래 정보를 함께 포함해 다시 질문해 주세요:\n" +
                        "  여기 까지가 고정멘트고 뒤에는 붙일지 말지 판단해." +
                        "- 기관 또는 브랜드명 (예: 고용노동부, 무신사)\n" +
                        "- 정보 유형 (예: 채용, 이벤트, 할인)\n" +
                        "- 시기 (예: 2025년 8월, 여름)" +
                        "- 설명, 제안, 예시, 링크, 브랜드 정보, 배경 설명 등 정보는 절대 포함해서 알려주지마세요.**.\n" +
                        "- 단순하고 명확하게 \"확인 가능한 일정 정보 없음\"만 전달하세요.\n" +

                        "3. clovaContent에 '죄송하지만' 문장과 JSON이 **동시에 존재**하거나,\n" +
                        "   → 잘못된 응답으로 간주하고 다음과 같이 출력하세요:\n" +
                        "  죄송하지만, \"" + userInput + "\"요청하신 문장에 대해 현재 기준으로 일정 정보를 확인할 수 없습니다.\n" +
                        "  일정을 찾기 위해 아래 정보를 함께 포함해 다시 질문해 주세요:\n" +
                        "  여기 까지가 고정멘트고 뒤에는 붙일지 말지 판단해." +
                        "- 기관 또는 브랜드명 (예: 고용노동부, 무신사)\n" +
                        "- 정보 유형 (예: 채용, 이벤트, 할인)\n" +
                        "- 시기 (예: 2025년 8월, 여름)" +
                        "- 설명, 제안, 예시, 링크, 브랜드 정보, 배경 설명 등 정보는 절대 포함해서 알려주지마세요.**.\n" +
                        "- 단순하고 명확하게 \"확인 가능한 일정 정보 없음\"만 전달하세요.\n" +

                        "[주의]\n" +
                        "- JSON과 자연어 문장이 동시에 존재하면 안 됩니다.\n" +
                        "- '죄송하지만'이라는 문장이 없는 자연어 응답도 허용되지 않습니다.\n" +
                        "- 위 조건 중 하나도 만족하지 못하면 파싱 오류가 발생하므로 반드시 조건을 지켜 응답하세요.\n" +
                        "- 전달받은 clovaContent를 훼손하거나 조작하지 말고, 조건 충족 여부만 판단하세요.\n";

        // 프롬프트 조립
        String fullPrompt = validationPrompt +
                "\n\n[userInput]\n" + userInput +
                "\n\n[clovaContent]\n" + clovaContent;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + clovaAuthToken);

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", List.of(Map.of("type", "text", "text", fullPrompt))
        );

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", List.of(Map.of(
                        "type", "text",
                        "text",
                        "userInput값과 clovaContent를 판단한 후, 아래 기준 중 하나에 맞게 응답하세요:\n" +
                                "1. JSON 일정 응답이 적절하면, 그 JSON을 그대로 응답하세요.\n" +
                                "2. 오류이거나 공식 일정이 없다고 판단되면, '죄송하지만, ~' 으로 시작하는 자연어 문장 1개만 응답하세요.\n" +
                                "※ 위 두 응답 외에는 절대 추가하지 마세요. JSON + 문장 동시 출력 금지." +
                                "[주의]\n" +
                                "- 위 조건 중 하나도 만족하지 못하면 파싱 오류가 발생하므로 반드시 조건을 지켜 응답하세요.\n" +
                                "- 전달받은 clovaContent를 훼손하거나 조작하지 말고, 조건 충족 여부만 판단하세요.\n"
                ))
        );

        List<Object> messages = List.of(systemMessage, userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        requestBody.put("topP", 0.3);
        requestBody.put("topK", 0);
        requestBody.put("maxTokens", 512);
        requestBody.put("temperature", 0.0);
        requestBody.put("repetitionPenalty", 1.0);
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
                String validationResult = (String) message.get("content");

                log.info(
                        "\n========== Validation Clova 응답 START ==========\n{}\n==========  Validation Clova 응답 END ==========",
                        validationResult);

                return validationResult.trim();
            }
        } catch (Exception e) {
            log.info("Clova 응답 검증 실패: {}", e.getMessage(), e);
        }

        return "죄송하지만, 요청하신 문장에 대해 현재 기준으로 일정 정보를 확인할 수 없습니다.";
    }

}





