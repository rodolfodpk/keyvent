package mothership.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.control.Option;
import javaslang.jackson.datatype.JavaslangModule;
import lombok.val;
import mothership.core.entities.Mission;
import mothership.core.entities.Plateau;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static mothership.core.MothershipDataSchema.*;
import static org.junit.Assert.assertEquals;

public class SerializationTest {

    static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaslangModule());
        mapper.registerModule(new Jdk8Module());
    }

    @Test
    public void createCmd() throws IOException {

        val mothershipId = new MothershipId("voyager");

        val createCmd = CreateMothership.builder()
                .commandId(new CommandId())
                .mothershipId(mothershipId)
                .rovers(HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto"))))
                .build();

        val asJson = mapper.writeValueAsString(createCmd);

        assertEquals(mapper.readerFor(MothershipCommand.class).readValue(asJson), createCmd);

        assertEquals(mapper.readerFor(CreateMothership.class).readValue(asJson), createCmd);

    }

    @Test
    public void startMissionCmd() throws IOException {

        val mothershipId = new MothershipId("voyager");

        val startMissionCmd = StartsMissionTo.builder()
                .mothershipId(mothershipId)
                .commandId(new CommandId())
                .missionId(new MissionId("mars"))
                .plateau(new Plateau(new PlateauId("deadSea"), new PlateauDimension(4, 4)))
                .build();

        val asJson = mapper.writeValueAsString(startMissionCmd);

        assertEquals(mapper.readerFor(MothershipCommand.class).readValue(asJson), startMissionCmd);

        assertEquals(mapper.readerFor(StartsMissionTo.class).readValue(asJson), startMissionCmd);

    }

    @Test
    public void listOfCommands() throws IOException {

        val typeRef = new TypeReference<List<MothershipCommand>>() {};

        val mothershipId = new MothershipId("voyager");

        val createCmd = CreateMothership.builder()
                .commandId(new CommandId())
                .mothershipId(mothershipId)
                .rovers(HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto"))))
                .build();

        val startMissionCmd = StartsMissionTo.builder()
                .mothershipId(mothershipId)
                .commandId(new CommandId())
                .missionId(new MissionId("mars"))
                .plateau(new Plateau(new PlateauId("deadSea"), new PlateauDimension(4, 4)))
                .build();

        List<MothershipCommand> listOfCommands = Arrays.asList(createCmd, startMissionCmd);

        val listJson = mapper.writerFor(typeRef).writeValueAsString(listOfCommands);

        List<MothershipCommand> backToList = mapper.readValue(listJson, typeRef);

        assertEquals(createCmd, backToList.get(0));

        assertEquals(startMissionCmd, backToList.get(1));

    }

    @Test @Ignore //failing
    public void mothership() throws IOException {

        Mission mission = new Mission(new MissionId("mars"), new Plateau(new PlateauId("dead-sea"), new PlateauDimension(2, 2)));

        MothershipAggregateRoot ar = MothershipAggregateRoot.builder().id(new MothershipId("voyager"))
                                                .mission(Option.of(mission)).build();

        val asJson = mapper.writeValueAsString(ar);

        assertEquals(ar, mapper.readerFor(MothershipAggregateRoot.class).readValue(asJson));

    }

    @Test @Ignore //failing TODO investigate
    public void testTuple2() throws IOException {

        // tuple2
        Tuple2<PlateauLocation, RoverDirection> t2 = Tuple.of(new PlateauLocation(0, 0), RoverDirection.NORTH);
        val tuple2AsJson = mapper.writeValueAsString(t2);
        val typeRefT2 = new TypeReference<Tuple2<PlateauLocation, RoverDirection>>() {};
        assertEquals(t2, mapper.readerFor(typeRefT2).readValue(tuple2AsJson));

        // map with tuple2 as key
        Map<Tuple2<PlateauLocation, RoverDirection>, RoverId> landedRovers =
                HashMap.of(Tuple.of(new PlateauLocation(0, 0), RoverDirection.NORTH), new RoverId("r1"));

        val typeRef = new TypeReference<Map<Tuple2<PlateauLocation, RoverDirection>, RoverId>>() {};
        val mapAsJson = mapper.writeValueAsString(landedRovers);
        assertEquals(landedRovers, mapper.readerFor(typeRef).readValue(mapAsJson));

    }

    // TODO test events

}
