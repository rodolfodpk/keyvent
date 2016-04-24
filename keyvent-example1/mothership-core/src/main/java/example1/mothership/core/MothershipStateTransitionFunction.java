package example1.mothership.core;


import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import javaslang.Function2;
import javaslang.control.Option;

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
                        event -> mothership.withId(event.getMothershipId()).withRovers(event.getRovers()).withStatus(AVALIABLE).withMission(Option.none())),
                Case(instanceOf(MissionStarted.class),
                        event -> mothership.withStatus(ON_MISSION).withMission(Option.of(event.getMission()))
                ),
                Case(instanceOf(RoverLaunched.class),
                        event -> {
                            Mission currentMission = mothership.getMission().get();
                            Plateau currentPlateau = currentMission.getPlateau();
                            Plateau newPlateau = currentPlateau
                                    .withLaunchedRovers(currentPlateau.launchedRovers()
                                    .put(event.getRoverId().getId(), event.getRoverPosition()));
                            return mothership.withMission(Option.of(currentMission.withPlateau(newPlateau)));
                        })
                );
    }
}

