package keyvent.sample.customer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javaslang.collection.List;
import keyvent.core.data.CommandId;
import keyvent.core.data.UnitOfWorkId;
import keyvent.core.data.Version;
import keyvent.sample.annotations.*;
import org.immutables.value.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerSchema {

    @Value.Immutable
    @Wrapped
    @JsonDeserialize(as = CustomerIdVal.class)
    public static abstract class CustomerId extends Wrapper<UUID> {}

    // commands

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "commandType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CreateCustomer.class, name = "CreateCustomerCmd"),
            @JsonSubTypes.Type(value = CreateAndActivateCustomer.class, name = "CreateActivatedCustomerCmd")
            })
    public interface CustomerCommand {
        CommandId commandId();
        CustomerId customerId();
    }

    @CommandStyle
    @Value.Immutable
        @JsonSerialize(as = CreateCustomerCmd.class)
        @JsonDeserialize(as = CreateCustomerCmd.class)
    public interface CreateCustomer extends CustomerCommand {}

    @CommandStyle
    @Value.Immutable
        @JsonSerialize(as = ActivateCustomerCmd.class)
        @JsonDeserialize(as = ActivateCustomerCmd.class)
    public interface ActivateCustomer extends CustomerCommand {
    }

    @Value.Immutable
    @CommandStyle
        @JsonSerialize(as = CreateAndActivateCustomerCmd.class)
        @JsonDeserialize(as = CreateAndActivateCustomerCmd.class)
    public interface CreateAndActivateCustomer extends CustomerCommand {
    }

    // events

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "eventType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CustomerCreated.class, name = "CustomerCreatedEvt"),
            @JsonSubTypes.Type(value = CustomerActivated.class, name = "CustomerActivatedEvt")})
    public interface CustomerEvent {
        CustomerId customerId();
    }

    @Value.Immutable
    @EventStyle
    @JsonSerialize(as = CustomerCreatedEvt.class)
    @JsonDeserialize(as = CustomerCreatedEvt.class)
    public interface CustomerCreated extends CustomerEvent {
    }

    @Value.Immutable
    @EventStyle
    @JsonSerialize(as = CustomerActivatedEvt.class)
    @JsonDeserialize(as = CustomerActivatedEvt.class)
    public interface CustomerActivated extends CustomerEvent {
        LocalDateTime date();
    }

    // Aggregate root

    @Value.Immutable
    @AggregateRootStyle
    @JsonSerialize(as = CustomerAgg.class)
    @JsonDeserialize(as = CustomerAgg.class)
    public interface Customer {
        @Nullable
        CustomerId id();
        @Nullable
        Boolean isActive();
        @Nullable
        LocalDateTime activeSince();
    }

    // unit of work

    @Value.Immutable
    @UnitOfWorkStyle
    @JsonSerialize(as = CustomerUnitOfWork.class)
    @JsonDeserialize(as = CustomerUnitOfWork.class)
    public interface CustomerUnitOfWork {
        UnitOfWorkId id();
        CustomerCommand command();
        Version version();
        List<CustomerEvent> events();
        Instant instant();
    }

}
