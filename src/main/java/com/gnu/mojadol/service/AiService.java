package com.gnu.mojadol.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiService {
    String getPrediction(List<MultipartFile> files);
}
