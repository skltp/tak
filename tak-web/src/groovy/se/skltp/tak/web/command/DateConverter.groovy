package se.skltp.tak.web.command

import org.grails.databinding.converters.ValueConverter

class DateConverter implements ValueConverter {
    @Override
    boolean canConvert(Object o) {
        o instanceof java.util.Date
    }

    @Override
    Object convert(value) {
        if (value instanceof java.util.Date) {
            java.sql.Date sDate = new java.sql.Date(value.getTime());
            return sDate
        }
        return null
    }

    @Override
    Class<?> getTargetType() {
        return java.sql.Date
    }
}
