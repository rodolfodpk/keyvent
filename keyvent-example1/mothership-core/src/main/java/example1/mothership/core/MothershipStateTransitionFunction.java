package example1.mothership.core;


import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import example1.mothership.core.entities.Rover;
import javaslang.Function2;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.Set;
import javaslang.control.Option;
import lombok.val;

import static example1.mothership.core.MothershipDataSchema.*;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.AVALIABLE;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.ON_MISSION;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

class MothershipStateTransitionFunction implements Function2<MothershipEvent, MothershipAggregateRoot, MothershipAggregateRoot> {

    @Override
    public MothershipAggregateRoot apply(MothershipEvent mothershipEvent, MothershipAggregateRoot mothership) {
        return Match(mothershipEvent).of(
                Case(instanceOf(MothershipCreated.class),
                        event -> mothership
                                    .withId(event.getMothershipId())
                                    .withRovers(HashMap.ofEntries(event.getRovers().map(rover -> Tuple.of(rover.getId().getId(), rover))))
                                    .withStatus(AVALIABLE).withMission(Option.none())
                        ),
                Case(instanceOf(MissionStarted.class),
                        event -> mothership.withStatus(ON_MISSION).withMission(Option.of(event.getMission()))
                ),
                Case(instanceOf(RoverLaunched.class),
                        event -> {
                            Mission currentMission = mothership.getMission().get();
                            Plateau currentPlateau = currentMission.getPlateau();
                            Plateau newPlateau = currentPlateau
                                    .withLandedRovers(currentPlateau.landedRovers()
                                    .put(event.getRoverId().getId(), event.getPlateauLocation()));
                            return mothership.withMission(Option.of(currentMission.withPlateau(newPlateau)));
                        })
                );
    }
}

