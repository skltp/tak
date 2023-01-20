package se.skltp.tak.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.service.AnropsAdressService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EntityValidator implements Validator {

    private final AnropsAdressService anropsAdressService;

    public EntityValidator(AnropsAdressService anropsAdressService) {
        this.anropsAdressService = anropsAdressService;
    }

    @Override
    public boolean supports(Class clazz) {
        return AbstractVersionInfo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (AnropsAdress.class.equals(target.getClass())) {
            validateAnropsadress(errors, (AnropsAdress) target);
        }
    }

    private void validateAnropsadress(Errors errors, AnropsAdress a) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adress", "empty");
        if(a.getTjanstekomponent() == null) errors.reject("missing.instance.tjanstekomponent");
        if(a.getRivTaProfil() == null) errors.reject("missing.instance.rivTaProfil");

        if (!stringMatchesPattern(a.getAdress(), "[0-9a-zA-Z_.:/\\-?&]*")) {
            errors.reject("invalid.content.instance.adress");
        }
        if (a.getAdress().length() < 5 || a.getAdress().length() > 255) {
            errors.reject("invalid.length.instance.adress");
        }
        if (anropsAdressService.hasDuplicate(a)) errors.reject("duplicate.anropsadress");
    }

    private boolean stringMatchesPattern(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
