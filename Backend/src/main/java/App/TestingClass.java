package App;

import App.controller.EventController;
import App.repository.EventRepository;
import App.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TestingClass {

    public static void main(String[] args) {
        try {
            EventService eventService = new EventService();
            eventService.createEvent("I have a 2hr meeting tomorrow at 4pm");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
