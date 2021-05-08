package com.nowakArtur97.myMoments.commentService.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
class ValidationMessagesSourceConfiguration {

    @Bean
    LocalValidatorFactoryBean getValidator() {

        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

        localValidatorFactoryBean.setValidationMessageSource(getMessageSource());

        return localValidatorFactoryBean;
    }

    @Bean
    MessageSource getMessageSource() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:/validation/messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }
}