package App.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.transaction.Transactional;
import App.model.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import App.repository.EventRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static dev.langchain4j.data.message.UserMessage.userMessage;

@Service
@Transactional
public class EventService {

    private static final String APPLICATION_NAME = "LifeSync";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private final NetHttpTransport HTTP_TRANSPORT;
    private final ChatLanguageModel llm = OpenAiChatModel
            .withApiKey("YOUR_API_KEY");

    private final Calendar service;
    private EventRepository eventRepository;


    //@Autowired
    public EventService() throws GeneralSecurityException, IOException {
        // Initialize HTTP_TRANSPORT and App.service in the constructor
        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        //this.eventRepository = eventRepository;

    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = EventService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return credential;
    }

    // Maybe write overload function with time
    // Will probably need another agent
    // Not sure about practicality of this as calendar is visualized
    public List<Event> getEvents(int maxResults) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(maxResults)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            List<String> eventSummaries = eventRepository.findAll()
                    .stream()
                    .map(AppEvent::getSummary)
                    .toList();
            System.out.println(eventSummaries); //for debugging purposes only
        }
        return items; //returns list of Google class Events
    }

    public void createEvent(String userMessage) throws IOException{
        CreationAssistant chain;
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        DateTime now = new DateTime(System.currentTimeMillis());
        System.out.println(now);
        memory.add(userMessage("Current time is " + now));
        chain = AiServices.builder(CreationAssistant.class)
                .chatLanguageModel(llm)
                .chatMemory(memory)
                .build();
        String answer = chain.chat(userMessage);

        System.out.println(answer);

        Gson gson = new Gson();
        HashMap<String, String> answerMap = gson.fromJson(answer, HashMap.class);
        String summary = answerMap.get("summary");
        String startTime = answerMap.get("startTime");
        String endTime = answerMap.get("endTime");
        //boolean isDynamic = Boolean.parseBoolean(answerMap.get("isDynamic"));


        Event event = new Event()
                .setSummary(summary);

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());

        //Save our event in our App.repository
//        AppEvent appEvent = new AppEvent(event.getId(),
//                                         event.getSummary(),
//                                         event.getStart().getDateTime(),
//                                         event.getEnd().getDateTime(),
//                                         isDynamic);
//        eventRepository.save(appEvent);
    }

    public void deleteEvent(String userMessage) throws IOException {
        DeletionAssistant chain;
        chain = AiServices.builder(DeletionAssistant.class)
                .chatLanguageModel(llm)
                .build();

        List<String> eventSummaries = eventRepository.findAll()
                .stream()
                .map(AppEvent::getSummary)
                .toList();

        String summary = chain.chat(eventSummaries + userMessage);
        AppEvent event = eventRepository.findBySummary(summary);
        if(event != null) {
            service.events().delete("primary", event.getEventId()).execute();
            eventRepository.delete(event);
        }
    }

}
