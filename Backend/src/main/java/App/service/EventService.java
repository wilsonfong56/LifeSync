package App.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import jakarta.transaction.Transactional;
import App.model.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import App.repository.EventRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class EventService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Calendar service;
    private final EventRepository eventRepository;
    private final ChatLanguageModel chatLanguageModel;

    @Autowired
    private CreationAssistant creationChat;
    @Autowired
    private DeletionAssistant deletionChat;


    @Autowired
    public EventService(EventRepository eventRepository, ChatLanguageModel chatLanguageModel) {
        this.eventRepository = eventRepository;
        this.chatLanguageModel = chatLanguageModel;

    }

    public void authenticate(String token) throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(token);
        this.service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();

        interface DynamicAnalyzer {
            @UserMessage("Determine whether the event, {{it}}, is dynamic or not. An event is considered dynamic if it can be moved or rescheduled within the user's schedule, " +
                    "even if the user has assigned a time to it (e.g., going to the gym or studying). In contrast, static events cannot be rescheduled and " +
                    "must happen at a specific time (e.g., meetings or appointments).")
            boolean isDynamic(String text);
        }

        DynamicAnalyzer dynamicAnalyzer = AiServices.create(DynamicAnalyzer.class, chatLanguageModel);
        Events events = service.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setSingleEvents(true)
                .execute();

        List<Event> eventItems = events.getItems();
        for(Event item : eventItems) {
            eventRepository.save(new AppEvent(item.getId(),
                    item.getSummary(),
                    item.getStart().getDateTime(),
                    item.getEnd().getDateTime(),
                    dynamicAnalyzer.isDynamic(item.getSummary())));
        }
    }

    public void createEvent(String userMessage) throws IOException{
        DateTime now = new DateTime(System.currentTimeMillis());
        creationChat.chat("Current time is " + now);
        String answer = creationChat.chat(userMessage);

        System.out.println(answer);

        Type listType = new TypeToken<List<HashMap<String, String>>>(){}.getType();
        Gson gson = new Gson();
        List<HashMap<String, String>> events = gson.fromJson(answer, listType);
        for(HashMap<String, String> event : events) {
            String summary = event.get("summary");
            String startTime;
            String endTime = event.get("endTime");
            boolean isDynamic = Boolean.parseBoolean(event.get("isDynamic"));
            if (isDynamic) {
                startTime = endTime;
            } else {
                startTime = event.get("startTime");
            }

            Event eventObj = new Event()
                    .setSummary(summary);

            DateTime startDateTime = new DateTime(startTime);
            DateTime endDateTime = new DateTime(endTime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime);
            eventObj.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime);
            eventObj.setEnd(end);

            String calendarId = "primary";
            eventObj = service.events().insert(calendarId, eventObj).execute();
            System.out.printf("Event created: %s\n", eventObj.getHtmlLink());

            //Save our event in our App.repository
            AppEvent appEvent = new AppEvent(eventObj.getId(),
                    eventObj.getSummary(),
                    eventObj.getStart().getDateTime(),
                    eventObj.getEnd().getDateTime(),
                    isDynamic);
            eventRepository.save(appEvent);
        }
    }

    public void deleteEvent(String userMessage) throws IOException {
        List<String> eventSummaries = eventRepository.findAll()
                .stream()
                .map(AppEvent::getSummary)
                .toList();

        String summary = deletionChat.chat(eventSummaries + userMessage);
        System.out.println(eventSummaries);
        System.out.println(summary);
        AppEvent event = eventRepository.findBySummary(summary);
        if(event != null) {
            service.events().delete("primary", event.getEventId()).execute();
            eventRepository.delete(event);
        }
    }

    public List<AppEvent> getEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsFromNow() throws IOException {
        Events events = service.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    public void alertsOn() throws IOException {
        for(Event item : getEventsFromNow()) {
            item.setReminders(new Event.Reminders());
            service.events().update("primary", item.getId(), item).execute();
        }
    }

    public void alertsOff() throws IOException {
        for (Event item : getEventsFromNow()) {
            item.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(new ArrayList<>()));
            service.events().update("primary", item.getId(), item).execute();
        }
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {

    }


}
