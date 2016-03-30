package keyvent.core.utils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS) // Make it class retention for incremental compilation
@Value.Style(
      //  passAnnotations = {JsonTypeInfo.class, JsonSubTypes.class, NotNull.class, Min.class, Size.class},
        passAnnotations = {JsonTypeInfo.class, JsonSubTypes.class, Nullable.class},
      //  get = {"is*", "get*"}, // Detect 'get' and 'is' prefixes in accessor methods
      //  init = "set*", // Builder initialization methods will have 'set' prefix
        typeAbstract = {"*"}, // 'Abstract' prefix will be detected and trimmed
        typeImmutable = "*Agg", // No prefix or suffix for generated immutable type
      //  builder = "new", // construct builder using 'new' instead of factory method
      //  build = "create", // rename 'build' method on builder to 'create'
     //   defaults = @Value.Immutable(builder = true, copy = true),
        visibility = Value.Style.ImplementationVisibility.PUBLIC // Generated class will be always public
)
public @interface AggregateRootStyle {}