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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import static dev.langchain4j.data.message.UserMessage.userMessage;

@Service
@Transactional
public class EventService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private NetHttpTransport HTTP_TRANSPORT;
    private final ChatLanguageModel llm = OpenAiChatModel
            .withApiKey("YOUR_API_KEY");

    private Calendar service;
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void setToken(String token) throws GeneralSecurityException, IOException {
        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(token);
        this.service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
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
        String startTime;
        String endTime = answerMap.get("endTime");
        boolean isDynamic = Boolean.parseBoolean(answerMap.get("isDynamic"));
        if(isDynamic) {
            startTime = endTime;
        }
        else {
            startTime = answerMap.get("startTime");
        }


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
        AppEvent appEvent = new AppEvent(event.getId(),
                                         event.getSummary(),
                                         event.getStart().getDateTime(),
                                         event.getEnd().getDateTime(),
                                         isDynamic);
        eventRepository.save(appEvent);
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

    public List<AppEvent> getEvents() {
        return eventRepository.findAll();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {

    }


}
