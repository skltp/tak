package se.skltp.tak.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.service.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.containsWhitespace;

@Component
public class EntityValidator implements Validator {

    @Autowired AnropsAdressService anropsAdressService;
    @Autowired AnropsBehorighetService anropsBehorighetService;
    @Autowired FilterCategorizationService filterCategorizationService;
    @Autowired LogiskAdressService logiskAdressService;
    @Autowired RivTaProfilService rivTaProfilService;
    @Autowired TjanstekomponentService tjanstekomponentService;
    @Autowired TjanstekontraktService tjanstekontraktService;
    @Autowired VagvalService vagvalService;

    @Override
    public boolean supports(Class clazz) {
        return AbstractVersionInfo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (AnropsAdress.class.equals(target.getClass())) {
            validateAnropsadress(errors, (AnropsAdress) target);
        }
        if (Anropsbehorighet.class.equals(target.getClass())) {
            validateAnropsbehorighet(errors, (Anropsbehorighet) target);
        }
        if (Filtercategorization.class.equals(target.getClass())) {
            validateFiltercategorization(errors, (Filtercategorization) target);
        }
        if (Filter.class.equals(target.getClass())) {
            validateFilter(errors, (Filter) target);
        }
        if (LogiskAdress.class.equals(target.getClass())) {
            validateLogiskAdress(errors, (LogiskAdress) target);
        }
        if (RivTaProfil.class.equals(target.getClass())) {
            validateRivTaProfil(errors, (RivTaProfil) target);
        }
        if (Tjanstekomponent.class.equals(target.getClass())) {
            validateTjanstekomponent(errors, (Tjanstekomponent) target);
        }
        if (Tjanstekontrakt.class.equals(target.getClass())) {
            validateTjanstekontrakt(errors, (Tjanstekontrakt) target);
        }
        if (Vagval.class.equals(target.getClass())) {
            validateVagval(errors, (Vagval) target);
        }
    }

    private void validateAnropsadress(Errors errors, AnropsAdress a) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adress", "empty");
        rejectIfNotMatching(errors, a.getAdress(),"[0-9a-zA-Z_.:/\\-?&]*", "adress");
        rejectIfWrongLength(errors, a.getAdress(), 5, 255, "adress");

        rejectIfNull(errors, a.getTjanstekomponent(), "tjanstekomponent");
        rejectIfNull(errors, a.getRivTaProfil(), "rivTaProfil");

        if (anropsAdressService.hasDuplicate(a)) errors.reject("duplicate.anropsadress");
    }

    private void validateAnropsbehorighet(Errors errors, Anropsbehorighet a) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "integrationsavtal", "empty");
        rejectIfLeadingOrTrailingWhitespace(errors, a.getIntegrationsavtal(), "integrationsavtal");

        rejectIfNull(errors, a.getTjanstekonsument(), "tjanstekonsument");
        rejectIfNull(errors, a.getTjanstekontrakt(), "tjanstekontrakt");
        rejectIfNull(errors, a.getLogiskAdress(), "logiskAdress");
        rejectIfNull(errors, a.getFromTidpunkt(), "fromTidpunkt");
        rejectIfNull(errors, a.getTomTidpunkt(), "tomTidpunkt");

        if (errors.hasErrors()) return;

        if (a.getFromTidpunkt().after(a.getTomTidpunkt())) {
            errors.reject("date.order.anropsbehorighet");
        } else if (anropsBehorighetService.hasOverlappingDuplicate(a)) {
            errors.reject("overlapping.anropsbehorighet");
        }
    }

    private void validateFiltercategorization(Errors errors, Filtercategorization fc) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "category", "empty");
        rejectIfLeadingOrTrailingWhitespace(errors, fc.getCategory(), "category");

        rejectIfNull(errors, fc.getFilter(), "filter");
        if (errors.hasErrors()) return;

        if (filterCategorizationService.hasDuplicate(fc)) errors.reject("duplicate.filtercategorization");
    }

    private void validateFilter(Errors errors, Filter f) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "servicedomain", "empty");
        rejectIfAnyWhitespace(errors, f.getServicedomain(), "servicedomain");
        rejectIfWrongLength(errors, f.getServicedomain(), 0, 255, "servicedomain");

        // Not able to check anropsbeorighet here since it is looked up in controller
    }

    private void validateLogiskAdress(Errors errors, LogiskAdress la) {
        rejectIfWrongLength(errors, la.getBeskrivning(), 0, 255, "beskrivning");

        rejectIfWrongLength(errors, la.getHsaId(), 0, 255, "hsaId");
        if (!la.getHsaId().equals("*")) {
            rejectIfNotMatching(errors, la.getHsaId(),"[0-9a-zA-Z_\\-]+", "hsaId");
        }

        if (logiskAdressService.hasDuplicate(la)) errors.reject("duplicate.logiskAdress");
    }

    private void validateRivTaProfil(Errors errors, RivTaProfil r) {
        rejectIfWrongLength(errors, r.getBeskrivning(), 0, 255, "beskrivning");

        rejectIfWrongLength(errors, r.getNamn(), 0, 255, "namn");
        rejectIfLeadingOrTrailingWhitespace(errors, r.getNamn(), "namn");

        if (rivTaProfilService.hasDuplicate(r)) errors.reject("duplicate.rivTaProfil");
    }

    private void validateTjanstekomponent(Errors errors, Tjanstekomponent t) {
        rejectIfWrongLength(errors, t.getBeskrivning(), 0, 255, "beskrivning");

        rejectIfWrongLength(errors, t.getHsaId(), 0, 255, "hsaId");
        rejectIfNotMatching(errors, t.getHsaId(),"[0-9a-zA-Z_\\-]+", "hsaId");

        if (tjanstekomponentService.hasDuplicate(t)) errors.reject("duplicate.tjanstekomponent");
    }

    private void validateTjanstekontrakt(Errors errors, Tjanstekontrakt tk) {
        rejectIfWrongLength(errors, tk.getBeskrivning(), 0, 255, "beskrivning");

        rejectIfWrongLength(errors, tk.getNamnrymd(), 0, 255, "namnrymd");
        rejectIfNotMatching(errors, tk.getNamnrymd(),"[0-9a-zA-Z_.:\\-]+", "namnrymd");

        if (tjanstekontraktService.hasDuplicate(tk)) errors.reject("duplicate.tjanstekontrakt");
    }

    private void validateVagval(Errors errors, Vagval v) {
        rejectIfNull(errors, v.getTjanstekontrakt(), "tjanstekontrakt");
        rejectIfNull(errors, v.getLogiskAdress(), "logiskAdress");
        rejectIfNull(errors, v.getAnropsAdress(), "anropsadress");
        rejectIfNull(errors, v.getFromTidpunkt(), "fromTidpunkt");
        rejectIfNull(errors, v.getTomTidpunkt(), "tomTidpunkt");

        if (errors.hasErrors()) return;

        if (v.getFromTidpunkt().after(v.getTomTidpunkt())) {
            errors.reject("date.order.vagval");
        } else if (vagvalService.hasOverlappingDuplicate(v)) {
            errors.reject("overlapping.vagval");
        }
    }

    private void rejectIfNull(Errors errors, Object value, String fieldName) {
        if(value == null) {
            errors.reject("missing.instance." + fieldName);
        }
    }

    private void rejectIfLeadingOrTrailingWhitespace(Errors errors, String value, String fieldName) {
        if(value == null || !value.trim().equals(value)) {
            errors.reject("whitespace.instance." + fieldName);
        }
    }

    private void rejectIfAnyWhitespace(Errors errors, String value, String fieldName) {
        if(containsWhitespace(value)) {
            errors.reject("whitespace.instance." + fieldName);
        }
    }

    private void rejectIfNotMatching(Errors errors, String value, String regex, String fieldName) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if(!matcher.matches()) {
            errors.reject("invalid.content.instance." + fieldName);
        }
    }

    private void rejectIfWrongLength(Errors errors, String value, int min, int max, String fieldName) {
        if(value.length() < min || value.length() > max) {
            errors.reject("invalid.length.instance." + fieldName);
        }
    }
}
