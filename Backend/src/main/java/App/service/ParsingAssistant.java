package App.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", chatMemory = "longChatMemory")
public interface ParsingAssistant {
    String s = "Your job is to determine if the user intends to create an event, delete an event, or neither. " +
            "An event creation request must include explicit mention of a specific time, date, or deadline for a task, meeting, or activity. " +
            "If the message includes such information, respond only with 'create.' " +
            "If the user wants to delete an event, respond only with 'delete.' " +
            "If there is no mention of a specific time or date, respond normally to the user's message.";
    @SystemMessage(s)
    String chat(@UserMessage String userMessage);
}
