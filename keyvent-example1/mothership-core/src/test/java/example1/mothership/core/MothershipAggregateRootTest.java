package example1.mothership.core;

import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.control.Option;
import lombok.val;
import org.junit.Test;

import static example1.mothership.core.MothershipDataSchema.*;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MothershipAggregateRootTest {

    @Test
    public void create_mothership_on_fresh_should_fire_event() {

        // given
        val mId = new MothershipId("startreck");
        val freshMothership = MothershipAggregateRoot.builder().build();
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));

        // when
        val fired_events = freshMothership.create(mId, rovers);

        // then
        assertEquals(List.of(new MothershipCreated(mId, rovers)), fired_events);
    }

    @Test
    public void start_mission_on_fine_mothership_should_fire_event() {

        // given
        val mId = new MothershipId("startreck");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(rovers).status(AVALIABLE).mission(Option.none()).build();

        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();

        // when
        val fired_events = avaliableMothership.startMission(mission.getMissionId(), initialPlateau);

        // then
        assertEquals(List.of(new MissionStarted(mission)), fired_events);
    }

    @Test
    public void land_rover_should_fire_event() {

        // given
        val mId = new MothershipId("startreck");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();
        val mockService = mock(TemperatureService.class);
        when(mockService.currentTemperatureInCelsius()).thenReturn(99f);
        val onMissionMothership = MothershipAggregateRoot.builder().id(mId).rovers(rovers).status(ON_MISSION).mission(Option.of(mission))
                    .temperatureService(mockService).build();

        // when
        val fired_events = onMissionMothership.landRover(new RoverId("enio"), new RoverPosition(new PlateauLocation(0,0), RoverDirection.NORTH));

        // then
        assertEquals(List.of(new RoverLaunched(new RoverId("enio"), new RoverPosition(new PlateauLocation(0,0), RoverDirection.NORTH))), fired_events);
    }

}