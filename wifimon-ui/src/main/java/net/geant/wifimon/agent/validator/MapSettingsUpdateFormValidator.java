package net.geant.wifimon.agent.validator;

import net.geant.wifimon.agent.model.MapSettingsUpdateFormModel;
import net.geant.wifimon.agent.service.MapSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kokkinos on 3/7/2017.
 */

@Component
public class MapSettingsUpdateFormValidator implements Validator {

    private final MapSettingsService mapSettingsService;

    @Autowired
    public MapSettingsUpdateFormValidator(MapSettingsService mapSettingsService) {
        this.mapSettingsService = mapSettingsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MapSettingsUpdateFormModel.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MapSettingsUpdateFormModel form = (MapSettingsUpdateFormModel) target;
        validateMaplatitude(form, errors);
        validateMaplongitude(form, errors);
        validateMapzoom(errors, form);

    }

    private void validateMapzoom(Errors errors, MapSettingsUpdateFormModel form) {
        final String fpRegex1 = "\\d{1}";
        final String fpRegex2 = "\\d{2}";
        /*try{
            Integer number = Integer.parseInt(form.getRadiuslife().toString());
            if (Pattern.matches(fpRegex1, form.getRadiuslife().toString()) || Pattern.matches(fpRegex2, form.getRadiuslife().toString())) {
                if (form.getRadiuslife() < 1 || form.getRadiuslife() > 24){
                    errors.rejectValue("radiuslife", "radiuslife.wrong", "Radius Life should be an Integer between 1 and 24");
                }
            }else{
                errors.rejectValue("radiuslife", "radiuslife.wrong", "Radius Life should be an Integer between 1 and 24");
            }
        }catch (NumberFormatException|NullPointerException ex) {
            System.out.println("Inserted Radius Life value different than Integer");
            errors.rejectValue("radiuslife", "radiuslife.notnumber", "Radius Life is not an Integer");
        }*/
        if (Pattern.matches(fpRegex1, form.getMapzoom().toString()) || Pattern.matches(fpRegex2, form.getMapzoom().toString())) {
            if (form.getMapzoom() < 0 || form.getMapzoom() > 22){
                errors.rejectValue("mapzoom", "mapzoom.wrong", "Map zoom should be an Integer between 0 and 22");
            }
        }else{
            errors.rejectValue("mapzoom", "mapzoom.wrong", "Map zoom should be an Integer between 0 and 22");
        }
    }

    private void validateMaplongitude(MapSettingsUpdateFormModel form, Errors errors) {

        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
                        "(\\.("+Digits+")("+Exp+")?)|"+
                        "((" +
                        "(0[xX]" + HexDigits + "(\\.)?)|" +
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        if (Pattern.matches(fpRegex, form.getMaplongitude())) {
            Double.valueOf(form.getMaplongitude()); // Will not throw NumberFormatException
            double d = Double.parseDouble(form.getMaplongitude());
            if (d  < -180 || d > 180){
                errors.rejectValue("longitude", "longitude.outOfRange", "Longitude should be between -180 and 180 degrees");
            }
        }
        else {
            errors.rejectValue("longitude", "longitude.notNumber", "Longitude not a number");
        }
    }

    private void validateMaplatitude(MapSettingsUpdateFormModel form, Errors errors) {
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
                        "(\\.("+Digits+")("+Exp+")?)|"+
                        "((" +
                        "(0[xX]" + HexDigits + "(\\.)?)|" +
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        if (Pattern.matches(fpRegex, form.getMaplatitude())) {
            Double.valueOf(form.getMaplatitude()); // Will not throw NumberFormatException
            double d = Double.parseDouble(form.getMaplatitude());
            if (d  < -90 || d > 90){
                errors.rejectValue("latitude", "latitude.outOfRange", "Latitude should be between -90 and 90 degrees");
            }
        }
        else {
            errors.rejectValue("latitude", "latitude.notNumber", "Latitude not a number");
        }
    }
}

