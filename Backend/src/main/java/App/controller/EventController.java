package App.controller;

import App.model.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import App.service.EventService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/event")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/auth")
    public ResponseEntity<HttpStatus> authenticate(@RequestHeader("Authorization") String auth) throws GeneralSecurityException, IOException {
        String token = auth.replace("Bearer ", "");
        eventService.authenticate(token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping
    public String parseInput(@RequestParam String userInput) throws IOException {
        return eventService.parseInput(userInput);
    }

    @GetMapping
    public List<AppEvent> getEvents() {
        return eventService.getEvents();
    }

    @PostMapping("/alert")
    public ResponseEntity<HttpStatus> setAlerts() throws IOException {
        eventService.setAlerts();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
