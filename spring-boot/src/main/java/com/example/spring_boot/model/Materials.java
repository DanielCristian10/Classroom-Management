package com.example.spring_boot.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Materials {

    private boolean singleFile; // If it stores just one or multiple
    private String fileName;
    private List<String> structured;
}
