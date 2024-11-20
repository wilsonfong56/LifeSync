package App;
import App.util.JsonUtils;
import com.google.api.client.util.DateTime;
import java.util.ArrayList;
import java.util.List;
import App.model.AppEvent;

public class ActualTestingClass {
    public static void main(String[] args) {
        // Create a list to hold AppEvent objects
        List<AppEvent> events = new ArrayList<>();

        // Create five sample AppEvent objects
        AppEvent event1 = new AppEvent("event1", "Meeting with Team", new DateTime("2024-11-06T10:00:00.000-08:00"), new DateTime("2024-11-06T11:00:00.000-08:00"), false, 5);
        AppEvent event2 = new AppEvent("event2", "Lunch with Client", new DateTime("2024-11-07T12:00:00.000-08:00"), new DateTime("2024-11-07T13:00:00.000-08:00"), true, 7);
        AppEvent event3 = new AppEvent("event3", "Project Presentation", new DateTime("2024-11-08T09:00:00.000-08:00"), new DateTime("2024-11-08T10:30:00.000-08:00"), false, 10);
        AppEvent event4 = new AppEvent("event4", "Team Sync", new DateTime("2024-11-09T15:00:00.000-08:00"), new DateTime("2024-11-09T16:00:00.000-08:00"), true, 3);
        AppEvent event5 = new AppEvent("event5", "Annual Review", new DateTime("2024-11-10T14:00:00.000-08:00"), new DateTime("2024-11-10T15:30:00.000-08:00"), false, 8);

        // Add the events to the list
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);

        System.out.println(JsonUtils.eventsToJson(events));

    }
}
