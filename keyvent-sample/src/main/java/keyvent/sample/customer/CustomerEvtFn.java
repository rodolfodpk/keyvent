package keyvent.sample.customer;


import javaslang.Function2;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class CustomerEvtFn implements Function2<CustomerSchema.CustomerEvent, CustomerSchema.Customer, CustomerSchema.Customer> {

    @Override
    public CustomerSchema.Customer apply(CustomerSchema.CustomerEvent customerEvent, CustomerSchema.Customer customer) {
        return Match(customerEvent).of(
                Case(instanceOf(CustomerSchema.CustomerCreated.class),
                        event -> customer.withId(event.getCustomerId())),
                Case(instanceOf(CustomerSchema.CustomerActivated.class),
                        event -> customer.withIsActive(true).withActiveSince(event.getDate())
                )
        );
    }
}

