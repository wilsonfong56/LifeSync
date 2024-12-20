import React, { useEffect } from "react";
import { gapi } from "gapi-script";
import { useNavigate } from "react-router-dom";
import "./index.css";
import "./App.css";
import Header from "./components/Header/Header";

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
        <div>
            <Header onLogin={handleLogin}/>

            <div className="content-container">
                <div id="welcome">
                    <h1>Welcome to LifeSync</h1>
                    <p>Your personal scheduling assistant powered by AI.</p>
                </div>
            </div>
        </div>
    );
}

export default App;
