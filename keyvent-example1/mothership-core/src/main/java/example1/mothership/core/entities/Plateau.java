package example1.mothership.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

import javax.validation.Valid;

import static example1.mothership.core.MothershipDataSchema.*;

@Value @Wither @AllArgsConstructor public class Plateau {

    PlateauId id;
    @Valid PlateauDimension dimension;
    @JsonIgnore Map<String, RoverPosition> launchedRovers;

    public Plateau(PlateauId id, PlateauDimension dimension) {
        this.id = id;
        this.dimension = dimension;
        this.launchedRovers = HashMap.empty();
    }

    public boolean canLaunchRover(RoverId roverId, RoverPosition roverPosition, TemperatureService temperatureService) {

        if (temperatureService.currentTemperatureInCelsius() > 100 /*celsius*/) {
            throw new IllegalStateException("this plateau is too hot at this moment");
        }
        if (launchedRovers.containsValue(roverPosition)) {
            throw new IllegalStateException("this location is not available at this moment");
        }
        if (launchedRovers.containsKey(roverId.getId())) {
            throw new IllegalStateException("this rover is already launched");
        }
        return true;
    }

    @JsonIgnore public Map<String, RoverPosition> launchedRovers() { return launchedRovers; }

}
