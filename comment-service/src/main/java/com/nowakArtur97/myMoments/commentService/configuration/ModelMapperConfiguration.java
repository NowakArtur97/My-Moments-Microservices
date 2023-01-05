package com.nowakArtur97.myMoments.commentService.configuration;

import com.nowakArtur97.myMoments.commentService.feature.document.CommentDocument;
import com.nowakArtur97.myMoments.commentService.feature.resource.CommentModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ModelMapperConfiguration {

    @Bean
    ModelMapper getModelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.createTypeMap(CommentDocument.class, CommentModel.class)
                .addMapping(CommentDocument::getCreateDate, CommentModel::setCreateDate)
                .addMapping(CommentDocument::getModifyDate, CommentModel::setModifyDate);

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }
}
