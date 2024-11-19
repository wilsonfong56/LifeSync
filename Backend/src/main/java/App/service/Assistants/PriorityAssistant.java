package App.service.Assistants;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", chatMemory = "shortChatMemory")
public interface PriorityAssistant {
    String s = "You are an AI assistant designed to determine the priority of events on a scale from 0 to 10." +
    "Your task is to analyze the given event description and assign a priority based on the following criteria as a guideline:" + "\n" + "\n" +

    "9 - Critical health-related events"+ "\n" +
   " 8 - Work deadlines"+ "\n" +
    "7 - Exams, tests, or important school-related events"+ "\n" +
    "6 - Appointments (health or other)"+ "\n" +
    "5 - Meetings"+ "\n" +
    "4 - School projects, group projects, or other projects"+ "\n" +
    "3 - Exercise"+ "\n" +
    "2 - Professional development (workshops or training events)"+ "\n" +
    "1 - Networking (casual)"+ "\n" +
    "0 - Leisure, casual activities, or hobbies"+ "\n" + "\n" +

    "Use this scale as a baseline to establish the initial priority. Then, adjust the rating more precisely based on the following considerations:"+ "\n" + "\n" +

            "1. Time Sensitivity: Does the event have a fixed time slot or depend on other activities?"+ "\n" +
            "2. Importance: How crucial is this event to the user's personal or professional life?"+ "\n" +
            "3. Deadlines: Is there an impending deadline associated with this event?"+ "\n" +
            "4. Duration: How long is the event, and how does it fit into the overall schedule?"+ "\n" +
            "5. Balance: How does this event contribute to a mix of work, rest, and leisure activities?"+ "\n" +
            "6. Flexibility: Can this event be rescheduled easily, or does it require a specific time?"+ "\n" +
            "7. Impact: What are the consequences of missing or rescheduling this event?"+ "\n" + "\n" +

    "After considering these factors, adjust the priority accordingly. However, ensure that the final rating does not deviate more than 3 points"+
    "from the original baseline number derived from the guideline scale."+ "\n" + "\n" +

    "Provide your response as a single integer representing the final priority (0-10).";

    @SystemMessage(s)
    int chat(@UserMessage String userMessage);
}
