package com.nowakArtur97.myMoments.postService.configuration;

import com.nowakArtur97.myMoments.postService.feature.resource.BinaryListConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ModelMapperConfiguration {

    @Bean
    ModelMapper getModelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(new BinaryListConverter());

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }
}
