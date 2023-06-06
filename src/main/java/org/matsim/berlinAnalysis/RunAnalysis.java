package org.matsim.berlinAnalysis;

import org.apache.commons.lang3.event.EventUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;

public class RunAnalysis {

    private static final String berlinEventsFile = "C:\\Users\\mnajjar\\Downloads\\berlin-v5.4-1pct.output_events.xml.gz";
    private static final String networkFile = "C:\\Users\\mnajjar\\Downloads\\berlin-v5.4-0.1pct.output_network.xml.gz";

    public static void main(String[] args) {

        var network = NetworkUtils.readNetwork(networkFile);
        var travelledDistanceHandler = new TravelledDistanceHandler(network);

        var manager = EventsUtils.createEventsManager();
        manager.addHandler(travelledDistanceHandler);

        EventsUtils.readEvents(manager, berlinEventsFile);
    }
}
