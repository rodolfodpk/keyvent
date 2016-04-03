package keyvent.sample.customer.validation;

import javaslang.Function4;
import javaslang.collection.CharSeq;
import javaslang.collection.List;
import javaslang.control.Validation;
import keyvent.sample.customer.CustomerSchema;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;

import static keyvent.sample.customer.CustomerSchema.CreateCustomer;
import static keyvent.sample.customer.CustomerSchema.CustomerId;

public class CreateCustomerValidator {

    public static void main(String[] args) {

        CreateCustomer cmd = CreateCustomer.builder()
                .commandId(new CustomerSchema.CommandId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .name("!@#¨#(#(")
                .age(17)
                .build();

        javaslang(cmd);

        javax(cmd);

    }

    static void javaslang(CreateCustomer cmd) {
        CreateCustomerValidator validator = new CreateCustomerValidator();
        System.out.println(validator.validate(cmd.getCommandId(), cmd.getCustomerId(), cmd.getName(), cmd.getAge()));
    }

    static void javax(CreateCustomer cmd) {
        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateCustomer>> constraintViolations =
                validator.validate(cmd);
        constraintViolations.forEach(System.out::println);
    }

    static final String VALID_NAME_CHARS = "[a-zA-Z ]";
    private static final int MIN_AGE = 18;

    public Validation<List<String>, CustomerSchema.CreateCustomer> validate(
            CustomerSchema.CommandId commandId, CustomerId customerId, String name, Integer age) {

        Function4<CustomerSchema.CommandId, CustomerSchema.CustomerId, String, Integer, CustomerSchema.CreateCustomer> f =
                (commandId1, customerId1, s, integer) ->
                        CreateCustomer.builder()
                                .commandId(commandId1)
                                .customerId(customerId1)
                                .name(s)
                                .age(integer)
                                .build();

        return Validation.combine(Validation.valid(commandId),
                Validation.valid(customerId),
                validateName(name),
                validateAge(age))
                .ap(f);
    }

    private Validation<String, String> validateName(String name) {
        return CharSeq.of(name).replaceAll(VALID_NAME_CHARS, "").transform(seq -> seq.isEmpty()
                ? Validation.valid(name)
                : Validation.invalid("Name contains invalid characters: '"
                + seq.distinct().sorted() + "'"));
    }

    private Validation<String, Integer> validateAge(int age) {
        return age < MIN_AGE
                ? Validation.invalid("Age must be greater than " + MIN_AGE)
                : Validation.valid(age);
    }
}
