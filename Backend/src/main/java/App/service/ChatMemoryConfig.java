package App.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public MessageWindowChatMemory shortChatMemory() {
        return MessageWindowChatMemory.withMaxMessages(3);
    }
}
