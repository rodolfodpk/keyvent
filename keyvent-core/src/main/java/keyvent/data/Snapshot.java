package keyvent.data;

import lombok.Value;

@Value
public class Snapshot<T> {
    T value;
    Version version;
}
