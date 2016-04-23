package mothership.core.entities;

import javaslang.Tuple2;
import javaslang.collection.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import static mothership.core.MothershipDataSchema.*;

@Value
@Builder
@Wither
@AllArgsConstructor
public class Plateau {

    PlateauId id;
    PlateauDimension dimension;

    public boolean canLaunchRover(RoverId roverId, PlateauLocation location, RoverDirection direction,
                                  TemperatureService temperatureService,
                                  Map<Tuple2<PlateauLocation, RoverDirection>, RoverId> rovers) {

        if (temperatureService.currentTemperatureInCelsius() > 100 /*celsius*/) {
            throw new IllegalStateException("this plateau is too hot at this moment");
        }
        if (rovers.containsKey(new Tuple2<>(location, direction))) {
            throw new IllegalStateException("this location is not available at this moment");
        }
        if (rovers.containsValue(roverId)) {
            throw new IllegalStateException("this rover is already launched");
        }
        return true;
    }
}
