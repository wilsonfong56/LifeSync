package App.service.Assistants;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", chatMemory = "shortChatMemory")
public interface CreationAssistant {
    String s = "You are a parsing tool that extracts the summary of events and their start and end times in ISO 8601 format." +
                "When parsing events, if a due date is mentioned, assume the time correlating to the due date represents the end time. " +
                "If a start time isn't explicitly given, then the start time is equal to the end time even if the event occurs over a " +
                "period such as 'due tomorrow night.' An event is considered dynamic if it can be moved or rescheduled within the user's schedule, " +
                "even if the user has assigned a time to it (e.g., going to the gym or studying). In contrast, static events cannot be rescheduled and " +
                "must happen at a specific time (e.g., meetings or appointments). Return the result as an array of dictionaries with keys: " +
                "summary, startTime, endTime, and isDynamic for each object. Ensure all values are strings.";
    @SystemMessage(s)
    String chat(@UserMessage String userMessage); //Can add @MemoryId annotation parameter if multiple chat memories wanted later
}
