package net.geant.wifimon.agent.validator;

import net.geant.wifimon.agent.model.AccesspointCreateFormModel;
import net.geant.wifimon.agent.service.AccesspointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kokkinos on 8/3/2017.
 */
@Component
public class AccesspointCreateFormValidator implements Validator {

    private final AccesspointService accesspointService;

    @Autowired
    public AccesspointCreateFormValidator(AccesspointService accesspointService) {
        this.accesspointService = accesspointService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(AccesspointCreateFormModel.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccesspointCreateFormModel form = (AccesspointCreateFormModel) target;
        validateMac(form, errors);
        validateLatitude(form, errors);
        validateLongitude(form, errors);
    }

    private void validateMac(AccesspointCreateFormModel form, Errors errors) {
        if (accesspointService.getAccesspointByMac(form.getMac().toUpperCase().replaceAll("-", ":")).isPresent()) {
            errors.rejectValue("mac", "mac.exists", "The AP with this MAC already exists");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mac", "mac.empty", "The AP MAC is empty");

        if (!validateFormMac(form.getMac())) {
            errors.rejectValue("mac", "mac.notRegex", "Not regular expression for a MAC address");
        }
    }

    private void validateLongitude(AccesspointCreateFormModel form, Errors errors) {

        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex =
                ("[\\x00-\\x20]*" +  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string
                        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +
                        "(\\.(" + Digits + ")(" + Exp + ")?)|" +
                        "((" +
                        "(0[xX]" + HexDigits + "(\\.)?)|" +
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        if (Pattern.matches(fpRegex, form.getLongitude())) {
            Double.valueOf(form.getLongitude()); // Will not throw NumberFormatException
            double d = Double.parseDouble(form.getLongitude());
            if (d < -180 || d > 180) {
                errors.rejectValue("longitude", "longitude.outOfRange", "Longitude should be between -180 and 180 degrees");
            }
        } else {
            errors.rejectValue("longitude", "longitude.notNumber", "Longitude not a number");
        }
    }

    private void validateLatitude(AccesspointCreateFormModel form, Errors errors) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex =
                ("[\\x00-\\x20]*" +  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string
                        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +
                        "(\\.(" + Digits + ")(" + Exp + ")?)|" +
                        "((" +
                        "(0[xX]" + HexDigits + "(\\.)?)|" +
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        if (Pattern.matches(fpRegex, form.getLatitude())) {
            Double.valueOf(form.getLatitude()); // Will not throw NumberFormatException
            double d = Double.parseDouble(form.getLatitude());
            if (d < -90 || d > 90) {
                errors.rejectValue("latitude", "latitude.outOfRange", "Latitude should be between -90 and 90 degrees");
            }
        } else {
            errors.rejectValue("latitude", "latitude.notNumber", "Latitude not a number");
        }
    }

    private boolean validateFormMac(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        Matcher m = p.matcher(mac);
        return m.find();
    }
}
