package com.nowakArtur97.myMoments.postService.testUtil.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectTestMapper {

    public static String asJsonString(final Object obj) {

        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

