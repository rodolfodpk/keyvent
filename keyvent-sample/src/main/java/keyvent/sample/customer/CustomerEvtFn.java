package keyvent.sample.customer;


import javaslang.Function2;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class CustomerEvtFn implements Function2<CustomerSchema.CustomerEvent, CustomerAgg, CustomerAgg> {

    @Override
    public CustomerAgg apply(CustomerSchema.CustomerEvent customerEvent, CustomerAgg customer) {
        return Match(customerEvent).of(
                Case(instanceOf(CustomerSchema.CustomerCreated.class),
                        event -> customer.withId(event.customerId())),
                Case(instanceOf(CustomerSchema.CustomerActivated.class),
                        event -> customer.withIsActive(true).withActiveSince(event.date())
                )
        );
    }
}

