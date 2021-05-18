package championoftaste.validators;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

@Service
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private final Pattern russianPhoneNumber =
            Pattern.compile("^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$");

    private final Pattern ukrainianPhoneNumber =
            Pattern.compile("^\\+?3?8?(0[5-9][0-9]\\d{7})$");

    private final Pattern kazakhstanPhoneNumber =
            Pattern.compile("^\\+?77([0124567][0-8]\\d{7})$");

    // TODO: CHECK REGEX
    private static final String belarusPhoneNumber =
            "^(\\+375|375|80)?[\\s\\-]?\\(?[17|29|33|44][0-9]\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$";

    @Override
    public boolean isValid(final String phone, final ConstraintValidatorContext constraintValidatorContext) {
        //return phone != null && phone.matches(belarusPhoneNumber);
        return true;
    }
}
