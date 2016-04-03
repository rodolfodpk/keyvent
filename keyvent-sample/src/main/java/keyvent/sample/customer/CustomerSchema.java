package keyvent.sample.customer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javaslang.collection.List;
import keyvent.sample.CommandId;
import keyvent.sample.UnitOfWorkId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Wither;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerSchema {

    @Value
    public static class CustomerId {
        UUID uuid;
    }

    // commands

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "getCommandType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CreateCustomer.class, name = "CreateCustomerCmd"),
            @JsonSubTypes.Type(value = CreateAndActivateCustomer.class, name = "CreateActivatedCustomerCmd")
    })
    public interface CustomerCommand {
        CommandId getCommandId();
        CustomerId getCustomerId();
    }

    @Value
    @Builder
    public static class CreateCustomer implements CustomerCommand {
        CommandId commandId;
        CustomerId customerId;
        @Pattern(regexp = "[a-zA-Z ]")
        String name;
        @Min(18)
        Integer age;
    }

    @Value
    @Builder
    public static class ActivateCustomer implements CustomerCommand {
        CommandId commandId;
        CustomerId customerId;
    }

    @Value
    @Builder
    public static class CreateAndActivateCustomer implements CustomerCommand {
        CommandId commandId;
        CustomerId customerId;
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
        CustomerId getCustomerId();
    }

    @Value
    @Builder
    public static class CustomerCreated implements CustomerEvent {
        CustomerId customerId;
    }

    @Value
    @Builder
    public static class CustomerActivated implements CustomerEvent {
        CustomerId customerId;
        LocalDateTime date;
    }

    // Aggregate root

    @Data
    @Builder
    @Wither
    @AllArgsConstructor
    public static class Customer {
        CustomerId id;
        String name;
        Integer age;
        Boolean isActive;
        LocalDateTime activeSince;
    }

    // unit of work

    @Value
    @Builder
    public static class CustomerUow {
        UnitOfWorkId id;
        CustomerCommand command;
        Long version;
        List<CustomerEvent> events;
        Instant instant;
    }

}
