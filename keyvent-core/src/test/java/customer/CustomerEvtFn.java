package customer;


import javaslang.Function2;

import static customer.CustomerSchema.*;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class CustomerEvtFn implements Function2<CustomerEvent, Customer, Customer> {

    @Override
    public Customer apply(CustomerEvent customerEvent, Customer customer) {
        return Match(customerEvent).of(
                Case(instanceOf(CustomerCreated.class),
                        event -> customer.withId(event.getCustomerId())),
                Case(instanceOf(CustomerActivated.class),
                        event -> customer.withIsActive(true).withActiveSince(event.getDate())
                )
        );
    }
}

