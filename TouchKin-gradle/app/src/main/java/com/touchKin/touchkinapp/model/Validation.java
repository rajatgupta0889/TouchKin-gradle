package com.touchKin.touchkinapp.model;

import java.util.regex.Pattern;

import android.widget.EditText;

public class Validation {

	// Regular Expression
	// you can change the expression based on your need
	// private static final String EMAIL_REGEX =
	// "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_REGEX = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
	private static final String PHONE_CODE = "^(1\\-)?[+]{1}\\-?[0-9]{4}\\-?[0-9]{4}\\-?[0-9]{4}$";
	private static final String OTP_REGEX = "^(1\\-)?[0-9]{4}$";

	// Error Messages
	private static final String REQUIRED_MSG = "required";
	// private static final String EMAIL_MSG = "invalid email";
	private static final String PHONE_MSG = "Invalid Phone Number";
	private static final String PHONE_CODE_MSG = "Enter Valid Phone Number with Country Code";
	private static final String OTP_MSG = "Invalid OTP Number";

	// call this method when you need to check email validation
	// public static boolean isEmailAddress(EditText editText, boolean required)
	// {
	// return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
	// }

	// call this method when you need to check phone number validation
	public static boolean isPhoneNumber(EditText editText, boolean required) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
	}
	public static boolean isPhoneNumberWithCode(EditText editText, boolean required) {
		return isValid(editText, PHONE_CODE, PHONE_CODE_MSG, required);
	}

	public static boolean isOTPNumber(EditText editText, boolean required) {
		return isValid(editText, OTP_REGEX, OTP_MSG, required);
	}

	// return true if the input field is valid, based on the parameter passed
	public static boolean isValid(EditText editText, String regex,
			String errMsg, boolean required) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText(editText)) {
			return false;
		}

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, text)) {
			editText.setError(errMsg);
			return false;
		}
		;

		return true;
	}

	public static boolean isValid(String editText, String regex, String errMsg,
			boolean required) {

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, editText)) {

			return false;
		}
		;

		return true;
	}

	// check the input field has any text or not
	// return true if it contains text otherwise false
	public static boolean hasText(EditText editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}
}
