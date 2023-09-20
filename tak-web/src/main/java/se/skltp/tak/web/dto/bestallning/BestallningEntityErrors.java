package se.skltp.tak.web.dto.bestallning;

import org.springframework.validation.*;

import java.util.ArrayList;
import java.util.List;

public class BestallningEntityErrors extends AbstractErrors
{
    String objectName = "instance";
    List<ObjectError> errors = new ArrayList<>();

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
        String[] messageCodes = new String[] { errorCode };
        errors.add(new ObjectError(objectName, messageCodes, errorArgs, defaultMessage));
    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
        String[] messageCodes = new String[] { errorCode };
        errors.add(new FieldError(objectName, field, null, false, messageCodes, errorArgs, defaultMessage));
    }

    @Override
    public void addAllErrors(Errors errors) {
        this.errors.addAll(errors.getAllErrors());
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        List<ObjectError> result = new ArrayList<>();
        for (ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) {
                result.add(objectError);
            }
        }
        return result;
    }

    @Override
    public List<FieldError> getFieldErrors() {
        List<FieldError> result = new ArrayList<>();
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                result.add((FieldError) objectError);
            }
        }
        return result;
    }

    @Override
    public Object getFieldValue(String field) {
        return null;
    }
}
