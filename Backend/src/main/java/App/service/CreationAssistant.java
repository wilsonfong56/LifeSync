package App.service;

import dev.langchain4j.service.SystemMessage;

public interface CreationAssistant {
    String s = "You are a parsing tool that will extract the summary of events and their start and and times in ISO 8601 format." +
            "When parsing events, if a due date is mentioned, assume the time correlating to the due date represents the end time. If a start time isn't explicitly given, then the start time is equal to the end time" +
            "Can you also let me know if the event is static or dynamic?" +
            "Return in form of a Java dictionary with keys summary, startTime, endTime, and isDynamic.";
    @SystemMessage(s)
    String chat(String userMessage); // Note: If change return type to HashMap, user is responsible for providing enough info to create the Map.
}
