package App.service;

import dev.langchain4j.service.SystemMessage;

public interface DeletionAssistant {
    String s = "You are going to be given a list of event summaries and a user message." +
                "You have to return the element in the list that best matches the context of the user message." +
                "Only respond with the element in the list.";
    @SystemMessage(s)
    String chat(String userMessage);
}
