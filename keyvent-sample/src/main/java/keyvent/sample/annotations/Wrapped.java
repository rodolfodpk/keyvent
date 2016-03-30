package keyvent.sample.annotations;

import org.immutables.value.Value;

@Value.Style(
        // Detect names starting with underscore
        typeAbstract = "*",
        // Generate without any suffix, just raw detected name
        typeImmutable = "*Val",
        // construct builder using 'new' instead of factory method
        builder = "new",
        // Make generated it public, leave underscored as package private
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        // Seems unnecessary to have builder or superfluous copy method
        defaults = @Value.Immutable(builder = false, copy = false))
public @interface Wrapped {}
