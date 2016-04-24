package keyvent.flows.commands;

import javaslang.collection.List;
import javaslang.control.Validation;

public interface CommandValidator {

    <T> Validation<List<String>, T> validate(T object);
}

