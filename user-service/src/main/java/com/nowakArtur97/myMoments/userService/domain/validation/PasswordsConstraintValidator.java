package com.nowakArtur97.myMoments.userService.domain.validation;

import com.nowakArtur97.myMoments.userService.domain.resource.UserDTO;
import org.passay.*;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.sort.ArraysSort;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class PasswordsConstraintValidator implements ConstraintValidator<ValidPasswords, UserDTO> {

    private static final String PASSAY_PROPERTIES_FILE = "/validation/passay.properties";
    private static final String COMMON_PASSWORDS_LIST = "/validation/common-passwords-list.txt";

    private DictionaryRule notCommonPasswordRule;

    private MessageResolver customMessagesResolver;

    @Override
    public void initialize(ValidPasswords constraintAnnotation) {

        loadCustomPassayMessages();

        loadCommonPasswordsList();
    }

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {

        String userName = user.getUsername();
        String password = user.getPassword();
        String matchingPassword = user.getMatchingPassword();

        if (password == null || matchingPassword == null) {
            return false;
        }

        PasswordValidator validator = new PasswordValidator(customMessagesResolver, defineRules());

        PasswordData passwordData = new PasswordData(password);
        PasswordData matchingPasswordData = new PasswordData(matchingPassword);

        passwordData.setUsername(userName);
        matchingPasswordData.setUsername(userName);

        RuleResult passwordResult = validator.validate(passwordData);
        RuleResult matchingPasswordResult = validator.validate(matchingPasswordData);

        if (passwordResult.isValid() && matchingPasswordResult.isValid()) {
            return true;
        }

        List<String> passwordResultMessages = validator.getMessages(passwordResult);
        List<String> matchingPasswordResultMessages = validator.getMessages(matchingPasswordResult);

        passwordResultMessages.addAll(matchingPasswordResultMessages);

        context.disableDefaultConstraintViolation();

        matchingPasswordResultMessages.forEach(message ->
                context.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation());

        return false;
    }

    private List<Rule> defineRules() {

        CharacterCharacteristicsRule characterCharacteristicsRule = new CharacterCharacteristicsRule();
        characterCharacteristicsRule.setNumberOfCharacteristics(3);

        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Special, 1));

        return Arrays.asList(
                new LengthRule(6, 30),
                new WhitespaceRule(),
                new UsernameRule(true, true, MatchBehavior.Contains),
                new RepeatCharacterRegexRule(3, true),
                notCommonPasswordRule,
                characterCharacteristicsRule
        );
    }

    private void loadCustomPassayMessages() {

        Properties props = new Properties();

        InputStream inputStream = getClass().getResourceAsStream(PASSAY_PROPERTIES_FILE);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            props.load(bufferedReader);

        } catch (IOException exception) {
            throw new RuntimeException("Could not find passay properties file with custom messages ", exception);
        }

        customMessagesResolver = new PropertiesMessageResolver(props);
    }

    private void loadCommonPasswordsList() {

        try {

            List<String> forbiddenPasswords = loadCommonPasswordsFromFile();

            notCommonPasswordRule = new DictionaryRule(new WordListDictionary(
                    new ArrayWordList(forbiddenPasswords.toArray(
                            new String[0]), false,
                            new ArraysSort()
                    )));

        } catch (IOException exception) {
            throw new RuntimeException("Could not find list of common passwords ", exception);
        }
    }

    private List<String> loadCommonPasswordsFromFile() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream(COMMON_PASSWORDS_LIST);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> forbiddenPasswords = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            forbiddenPasswords.add(line);
        }
        bufferedReader.close();

        return forbiddenPasswords;
    }
}
