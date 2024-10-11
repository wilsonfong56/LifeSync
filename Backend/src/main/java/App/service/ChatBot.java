package App.service;

import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", chatMemory = "longChatMemory")
public interface ChatBot {
    String chat(String userMessage);
}
