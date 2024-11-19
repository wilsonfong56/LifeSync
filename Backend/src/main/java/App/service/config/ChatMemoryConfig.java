package App.service.config;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public MessageWindowChatMemory shortChatMemory() {
        return MessageWindowChatMemory.withMaxMessages(3);
    }

    @Bean MessageWindowChatMemory longChatMemory() {
        return MessageWindowChatMemory.withMaxMessages(100);
    }
}
