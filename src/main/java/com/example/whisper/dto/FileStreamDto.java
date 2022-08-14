package com.example.whisper.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Builder
@Getter
public class FileStreamDto {

    private InputStream inputStream;
    private long contentLength;
}