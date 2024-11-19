package App.service.Assistants;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", chatMemory = "shortChatMemory")
public interface DeletionAssistant {
    String s = "You are going to be given a list of events as a JSON array and a user message." +
                "You have to return the eventId of the event who's time and summary best matches the user message." +
                "Only return the eventId.";
    @SystemMessage(s)
    String chat(@UserMessage String userMessage);
}
