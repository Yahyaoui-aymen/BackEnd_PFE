package com.example.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public class StatisticsResponse {

    private HashMap<Integer , Integer> approuved ;
    private HashMap<Integer , Integer> rejected ;
    private HashMap<Integer , Integer> waiting ;

}
