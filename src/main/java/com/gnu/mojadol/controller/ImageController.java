package com.gnu.mojadol.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    // 파일이 저장된 경로를 지정
    private final Path rootLocation = Paths.get("/Users/byeongyeongtae/uploads");

    // /images/uploads/ 경로로 이미지를 제공
    @GetMapping("/images/uploads/{filename}")
    public Resource serveFile(@PathVariable String filename) {
        try {
            // 파일 경로를 찾기
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            // 파일이 존재하고 읽을 수 있으면 반환
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not find the file!");
        }
    }
}
