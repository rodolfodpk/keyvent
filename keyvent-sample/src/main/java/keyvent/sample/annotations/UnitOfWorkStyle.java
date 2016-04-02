package keyvent.sample.annotations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonDeserialize
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS) // Make it class retention for incremental compilation
@Value.Style(
        allParameters = true,
       // passAnnotations = {JsonTypeInfo.class, JsonSubTypes.class},
        // get = {"is*", "get*"}, // Detect 'get' and 'is' prefixes in accessor methods
        // init = "set*", // Builder initialization methods will have 'set' prefix
        typeAbstract = {"*UnitOfWork"}, // 'Abstract' prefix will be detected and trimmed
        typeImmutable = "*Uow", // No prefix or suffix for generated immutable type
        // builder = "new", // construct builder using 'new' instead of factory method
        // build = "create", // rename 'build' method on builder to 'create'
       // defaults = @Value.Immutable(builder = true, copy = false),
        visibility = Value.Style.ImplementationVisibility.PUBLIC // Generated class will be always public
)
public @interface UnitOfWorkStyle {}