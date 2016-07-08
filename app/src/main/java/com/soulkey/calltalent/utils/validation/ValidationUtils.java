package com.soulkey.calltalent.utils.validation;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public final class ValidationUtils {

    private static final String MOBILE_NUMBER_REGEX = "[1]\\d{10}$";
    private static final String TEXT_WITH_MOBILE_NUMBER_REGEX = ".*[7-9][0-9]{9}.*";
    private static final String TEXT_WITH_EMAIL_ADDRESS_REGEX = ".*[a-zA-Z0-9\\+\\" +
            "._%\\-\\+]{1,256}\\@[a-zA-Z0-9]{1,64}\\.[a-zA-Z0-9]{2,25}.*";

    private static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z._0-9]{2,19}$";
    private static final String TEXT_WITH_FOUR_CONSECUTIVE_NUMBERS_REGEX = ".*[0-9]{5,}.*";
    private static final String TEXT_WITH_PASSWORD_REGEX = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$*%]).{6,20})";

    public static boolean isValidMobileNumber(String number) {
        Pattern mPattern = Pattern.compile(MOBILE_NUMBER_REGEX);
        Matcher matcher = mPattern.matcher(number);
        return matcher.find();
    }

    public static ValidationResult<String> isValidUsername(String username) {
        if (username.isEmpty()) return ValidationResult.failure(null, username);

        if (username.length() < 3)
            return ValidationResult.failure("username should have 3 or more characters", username);

        Pattern mPattern = Pattern.compile(USERNAME_REGEX);
        Matcher matcher = mPattern.matcher(username);
        boolean isValid = matcher.find();

        if (isValid) return ValidationResult.success(username);

        return ValidationResult.failure(
                "username should contain only alphanumeric characters", username);
    }

    public static boolean containsFourConsecutiveNumbers(String text) {
        Pattern mPattern = Pattern.compile(TEXT_WITH_FOUR_CONSECUTIVE_NUMBERS_REGEX);
        Matcher matcher = mPattern.matcher(text);
        return matcher.find();
    }

    public static boolean containsMobileNumber(String text) {
        Pattern mPattern = Pattern.compile(TEXT_WITH_MOBILE_NUMBER_REGEX);
        Matcher matcher = mPattern.matcher(text);
        return matcher.find();
    }

    public static ValidationResult<String> isValidEmailAddress(@NonNull String text) {
        if (text.isEmpty()) return ValidationResult.failure(null, text);

        Pattern mPattern = Pattern.compile(TEXT_WITH_EMAIL_ADDRESS_REGEX);
        Matcher matcher = mPattern.matcher(text);
        boolean isValid = matcher.find();

        if (isValid) return ValidationResult.success(text);

        return ValidationResult.failure("Please enter correct email address", text);
    }

    public static ValidationResult<String> isValidPassword(@NonNull String text) {
        if (text.isEmpty()) return ValidationResult.failure(null, text);
        Pattern mPattern = Pattern.compile(TEXT_WITH_PASSWORD_REGEX);
        Matcher matcher = mPattern.matcher(text);
        boolean isValid = matcher.find();

        if (isValid) return ValidationResult.success(text);

        return ValidationResult.failure("Please enter correct password", text);
    }

    public static ValidationResult<Boolean> validateRequiredField(@NonNull String text) {
        if (text.isEmpty())
            return ValidationResult.failure("The field is required and cannot be blank", false);
        return ValidationResult.success(true);
    }

    public static ValidationResult<Boolean> validateMinLength(
            @NonNull String text, int minLength) {
        if (text.length() < minLength)
            return ValidationResult.failure("At least " + minLength + " characters are required", false);
        return ValidationResult.success(true);
    }

    public static ValidationResult<Boolean> validateMaxLength(
            @NonNull String text, int maxLength) {
        if (text.length() > maxLength)
            return ValidationResult.failure("no more than " + maxLength + " characters are required", false);
        return ValidationResult.success(true);
    }

    public static ValidationResult<Boolean> validateRepeatPassword(
            @NonNull String password, @NonNull String repeatPassword) {
        if (!password.equals(repeatPassword))
            return ValidationResult.failure("the two passwords are not identical", false);
        return ValidationResult.success(true);
    }

}

