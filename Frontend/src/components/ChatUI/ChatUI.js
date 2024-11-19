import React, {useState} from "react";
import { ChatFeed, Message } from "react-chat-ui";
import Linkify from "react-linkify";
import "../../App.css";
import "./ChatUI.css";

function ChatUI({ fetchCalendar }) {
    const [userInput, setUserInput] = useState('');
    const [messages, setMessages] = React.useState([
        new Message({ id: 1, message: "Hello! I am your calendar assistant, how can I help you today?" }),
    ]);

    async function handleSubmit(event) {
        event.preventDefault();
        console.log(userInput);
        setMessages([...messages, new Message({ id: 0, message: userInput })]);
        setUserInput("");
        const apiResponse = await fetch(`http://localhost:8080/api/v1/event?userInput=${encodeURIComponent(userInput)}`, {
            method: 'POST',
        });
        const text = await apiResponse.text();
        setMessages(prevMessages => [...prevMessages, new Message({ id : 1, message: text })]);
        fetchCalendar();
    }

    return (
        <div className="chat-container">
                <ChatFeed
                    messages={messages.map((msg) => ({
                        ...msg,
                        message: (
                        <Linkify>{msg.message}</Linkify>
                        )
                    }))}
                    bubbleStyles={{
                        chatbubble: {
                            backgroundColor: '#3788d8'
                        },
                        userBubble: {
                            backgroundColor: '#44b332'
                        }
                    }}
                />
            <div className="input-container">
                <form onSubmit={handleSubmit}>
                    <input id="textInput"
                           value={userInput}
                           onChange={(event) => setUserInput(event.target.value)}
                    />
                    <button type="submit">Submit</button>
                </form>
            </div>
        </div>
    );
}

export default ChatUI;