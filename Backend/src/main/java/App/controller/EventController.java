package App.controller;

import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import App.service.EventService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/event")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    // Will take user input later
    public List<Event> getEvents(int maxResults) throws IOException {
        return eventService.getEvents(maxResults);
    }

    @PostMapping
    public void createEvent(@RequestParam String userInput) throws IOException {
        eventService.createEvent(userInput);
    }

    @DeleteMapping
    public void deleteEvent(@RequestParam String userInput) throws IOException {
        eventService.deleteEvent(userInput);
    }

}
