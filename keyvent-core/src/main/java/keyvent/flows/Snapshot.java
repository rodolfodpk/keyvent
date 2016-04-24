package keyvent.flows;

import lombok.Value;

@Value
public class Snapshot<T> {
    T value;
    Version version;
}
