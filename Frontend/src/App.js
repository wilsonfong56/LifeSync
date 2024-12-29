import React, { useEffect } from "react";
import { gapi } from "gapi-script";
import { useNavigate } from "react-router-dom";
import "./index.css";
import "./App.css";
import Header from "./components/Header/Header";
import { Calendar, Bot, MessageSquare } from 'lucide-react';

function App() {
    const navigate = useNavigate();

    // Initialize Google API client
    useEffect(() => {
        const initClient = () => {
            gapi.client.init({
                clientId: "897659353364-t61qemfnk5jiju4b1ernfce6m46jicli.apps.googleusercontent.com",
                scope: "https://www.googleapis.com/auth/calendar",
            });
        };
        gapi.load("client:auth2", initClient);
    }, []);

    const handleLogin = async () => {
        const GoogleAuth = gapi.auth2.getAuthInstance();
        let calendarId = null;
        try {
            const response = await GoogleAuth.signIn();
            const accessToken = response.getAuthResponse().access_token;
            console.log("Access token:", accessToken);

            const apiResponse = await fetch("http://localhost:8080/api/v1/event/auth", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            const calendarResponse = await fetch(
                "https://www.googleapis.com/calendar/v3/users/me/calendarList",
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            );

            const calendarData = await calendarResponse.json();
            calendarData.items.forEach((item) => {
                if (item.primary) {
                    calendarId = item.id;
                }
            });

            navigate("/calendar", { state: { calendarId, accessToken } });
        } catch (error) {
            console.error("Error fetching calendar:", error);
        }
    };

    return (
        <>
            <Header onLogin={handleLogin}/>
            <div className="pt-24 pb-8 md:pt-32 max-w-7xl mx-auto px-4 sm:px-6">
                <div className="text-center">
                    <h1 className="text-4xl md:text-6xl font-bold mb-6">
                        <span className="bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
                          Welcome to LifeSync
                        </span>
                    </h1>
                    <p className="text-xl md:text-2xl text-gray-600 mb-12 max-w-2xl mx-auto">
                        Your personal scheduling assistant powered by AI, making time management effortless.
                    </p>

                    {/* Feature Cards */}
                    <div className="grid md:grid-cols-3 gap-8 mt-16">
                        <div className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition-shadow">
                            <Calendar className="h-12 w-12 text-indigo-600 mx-auto mb-4"/>
                            <h3 className="text-xl font-semibold mb-2">Smart Calendar Integration</h3>
                            <p className="text-gray-600">Seamlessly sync with Google Calendar and create events using
                                natural language</p>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition-shadow">
                            <Bot className="h-12 w-12 text-indigo-600 mx-auto mb-4"/>
                            <h3 className="text-xl font-semibold mb-2">AI-Powered Optimization</h3>
                            <p className="text-gray-600">Automatically schedules and adjusts events based on priorities
                                and deadlines</p>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition-shadow">
                            <MessageSquare className="h-12 w-12 text-indigo-600 mx-auto mb-4"/>
                            <h3 className="text-xl font-semibold mb-2">Intelligent Assistant</h3>
                            <p className="text-gray-600">Get personalized suggestions and event-specific resources to
                                help achieve your goals</p>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};
export default App;
