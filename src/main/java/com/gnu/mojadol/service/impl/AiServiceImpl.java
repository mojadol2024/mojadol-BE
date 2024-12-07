package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.service.AiService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    public String getPrediction(List<MultipartFile> files) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5001/prediction";

        try {
            // Multipart 형식의 데이터를 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for (MultipartFile file : files) {
                body.add("file", file.getResource());
            }

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 요청 엔티티 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask 서버 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get prediction: " + response.getStatusCode());
            }

            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with AI service";
        }
    }
}
