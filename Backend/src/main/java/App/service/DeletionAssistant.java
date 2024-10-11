package App.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", chatMemory = "shortChatMemory")
public interface DeletionAssistant {
    String s = "You are going to be given a list of event summaries and a user message." +
                "You have to return the element in the list that best matches the context of the user message." +
                "Only respond with the element in the list without brackets.";
    @SystemMessage(s)
    String chat(@UserMessage String userMessage);
}
