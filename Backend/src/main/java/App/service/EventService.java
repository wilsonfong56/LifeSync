package App.service;

import App.util.DateUtils;
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
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.langchain4j.model.chat.ChatLanguageModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.DayOfWeek;
import static com.google.api.client.util.DateTime.parseRfc3339ToSecondsAndNanos;

@Service
@Transactional
public class EventService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Calendar service;
    private final EventRepository eventRepository;
    private final ChatLanguageModel chatLanguageModel;
    private boolean alertsOn = true;

    @Autowired
    private CreationAssistant creationChat;
    @Autowired
    private DeletionAssistant deletionChat;
    @Autowired
    private ParsingAssistant parsingAssistant;
    @Autowired
    private PriorityAssistant priorityAssistant;
    @Autowired
    private ChatBot chatBot;

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

        List<Event> events = getEventsFromNow();

        for(Event item : events) {
            int priority;
            boolean isDynamic = dynamicAnalyzer.isDynamic(item.getSummary());
            if (isDynamic) {
                priority = priorityAssistant.chat(item.getSummary());
            }
            else {
                priority = 10;
            }
            eventRepository.save(new AppEvent(item.getId(), item.getSummary(),
                                item.getStart().getDateTime(), item.getEnd().getDateTime(),
                                isDynamic, priority));
        }
    }

    public String parseInput(String userMessage) throws IOException {
        String answer = parsingAssistant.chat(userMessage);
        if(answer.equals("create")) {
            // For testing without creating an event
//            System.out.println(answer);
//            DateTime now = new DateTime(System.currentTimeMillis());
//            String answer1 = creationChat.chat("The current time is " + now + userMessage);
//            System.out.println(answer1);
            return createEvent(userMessage);   //comment out when testing
        }
        else if(answer.equals("delete")) {
//            System.out.println(answer);
            return deleteEvent(userMessage);
        }
        else {
            return chatBot.chat(userMessage);
        }
    }

    public String createEvent(String userMessage) throws IOException{ //creates multiple if needed (might want to abstract this later)
        DateTime now = new DateTime(System.currentTimeMillis());
        String dayOfWeek = DateUtils.dayOfWeek(now);
        System.out.println(dayOfWeek + now);
        String answer = creationChat.chat("The current time is " + dayOfWeek + ", " + now + ". " + userMessage);

        System.out.println("Create event says: " + answer);
        String answerMod = answer.substring(answer.indexOf("["), answer.indexOf("]")+1);
        System.out.println(answerMod);

        Type listType = new TypeToken<List<HashMap<String, String>>>(){}.getType();
        Gson gson = new Gson();
        List<HashMap<String, String>> events = gson.fromJson(answerMod, listType);
        for(HashMap<String, String> event : events) {
            String summary = event.get("summary");
            String startTime;
            String endTime = event.get("endTime");
            boolean isDynamic = Boolean.parseBoolean(event.get("isDynamic"));
            startTime = event.get("startTime");

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
            int priority;
            if (isDynamic) {
                priority = priorityAssistant.chat(eventObj.getSummary());
            }
            else {
                priority = 10;
            }
            AppEvent appEvent = new AppEvent(eventObj.getId(),
                                            eventObj.getSummary(),
                                            eventObj.getStart().getDateTime(),
                                            eventObj.getEnd().getDateTime(),
                                            isDynamic, priority);

            eventRepository.save(appEvent);
        }
        return "Event created";
    }

    public String deleteEvent(String userMessage) throws IOException{    //
        List<String> eventSummaries = eventRepository.findAll()
                .stream()
                .map(AppEvent::getSummary)
                .toList();

        String summary = deletionChat.chat(eventSummaries + " " + userMessage);
        System.out.println("UserMessage: " + userMessage);
        System.out.println("Event summaries: " + eventSummaries);
        System.out.println("Summary: " + summary);

        List<AppEvent> events = eventRepository.findBySummary(summary);
        for (AppEvent event : events) {
            service.events().delete("primary", event.getEventId()).execute();
            eventRepository.delete(event);
        }
        return "Event(s) deleted";
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

    public void setAlerts() throws IOException {
        for(Event item : getEventsFromNow()) {
            if(!alertsOn) {
                System.out.println("Turning alerts on");
                item.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(
                        new EventReminder().setMethod("email").setMinutes(30),
                        new EventReminder().setMethod("popup").setMinutes(30)
                )));
                service.events().update("primary", item.getId(), item).execute();
            }
            else {
                System.out.println("Turning alerts off");
                item.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(new ArrayList<>()));
                service.events().update("primary", item.getId(), item).execute();
            }
        }
        alertsOn = !alertsOn;
    }

    public long getDurationInMinutes(DateTime startTime, DateTime endTime) throws ParseException {
        long start = DateTime.parseRfc3339ToSecondsAndNanos(startTime.toString()).getSeconds();
        long end = DateTime.parseRfc3339ToSecondsAndNanos(endTime.toString()).getSeconds();
        return end-start/60;
    }

    public ArrayList<String> findFreeSlots() throws IOException {
        ArrayList<String> freeSlots = new ArrayList<>();
        long now = System.currentTimeMillis();
        DateTime nowDateTime = new DateTime(now);
        DateTime endDateTime = new DateTime(now + 604800000);
        List<Event> events = service.events().list("primary")
                        .setTimeMin(nowDateTime)
                        .setTimeMax(endDateTime)
                        .execute().getItems();
        String currentDateTime = nowDateTime.toString();
        String endTime = endDateTime.toString();
        for (Event event : events) {
            String eventStart = event.getStart().toString();
            //check currentDateTime is not in the middle of another event
            if (!(currentDateTime.compareTo(eventStart) >= 0 &&
                    currentDateTime.compareTo(eventStart) < 0)) {
                    String slot = currentDateTime + "/" + event.getStart().toString();
                    freeSlots.add(slot);
                    currentDateTime = event.getEnd().toString();
            }
            else {
                currentDateTime = event.getEnd().toString();
            }
        }
        if (currentDateTime.compareTo(endTime) < 0) {
            freeSlots.add(currentDateTime + "/" + endTime);
        }

        return freeSlots;
    }

}
