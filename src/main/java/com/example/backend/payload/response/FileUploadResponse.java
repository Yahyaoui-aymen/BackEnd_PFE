package com.example.backend.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadResponse {

    private String fileName;
    private String fileType;
    private long size;

    public FileUploadResponse(String fileName, String fileType, long size) {
        this.fileName = fileName;

        this.fileType = fileType;
        this.size = size;
    }

}
