package org.matsim.berlinAnalysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

import java.util.*;

public class TravelledDistanceHandler implements TransitDriverStartsEventHandler, LinkLeaveEventHandler, PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler {

    // Maps a vehicle to a person
    private final Map<Id<Vehicle>, Id<Person>> personInCar = new HashMap<>();
    // The container which collects the data we are interested in
    private final Map<Id<Person>, List<Double>> personToTrips = new HashMap<>();
    // Get tje transit drivers
    private final Set<Id<Person>> transitDrivers = new HashSet<>();
    private final Network network;

    public TravelledDistanceHandler(Network network) {
        this.network = network;
    }

    public Map<Id<Person>, List<Double>> getPersonToTrips() {
        return personToTrips;
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        if (!personInCar.containsKey(event.getVehicleId())) return;
        var personId = personInCar.get(event.getVehicleId());
        var link = network.getLinks().get(event.getLinkId());
        var trips = personToTrips.get(personId);
        var currentValue = trips.get(trips.size() -1);
        trips.set(trips.size() - 1, currentValue + link.getLength());
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {

        //condition to handle events for only non transit driver
        if (transitDrivers.contains(event.getPersonId())) return;
        personInCar.put(event.getVehicleId(), event.getPersonId());
        personToTrips.computeIfAbsent(event.getPersonId(), id -> new ArrayList<>()).add(0.0);

    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent event) {

        if (transitDrivers.contains(event.getPersonId())) return;
        personInCar.remove(event.getVehicleId());

    }

    @Override
    public void handleEvent(TransitDriverStartsEvent event) {
        transitDrivers.add(event.getDriverId());
    }
}
