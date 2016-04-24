package keyvent.flows;

import lombok.Value;

@Value
public class Version {
    Long value;
    public static Version nextVersion(Version version) {
        return new Version(version.getValue()+1);
    }
}
