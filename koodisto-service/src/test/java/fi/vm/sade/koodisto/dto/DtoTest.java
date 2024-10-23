package fi.vm.sade.koodisto.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DtoTest {

    protected static final SpringValidatorAdapter validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());

    @DisplayName("Verify validation routines")
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("parameters")
    void test(String msg, Object bean, Set<String> expected) {
        assertThat(validator.validate(bean).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet())).isEqualTo(expected);
    }

    protected static Date dateOf(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
