import React, {useEffect, useRef, useState} from 'react';
import { Calendar } from '@fullcalendar/core'; // Correctly import Calendar
import googleCalendarPlugin from '@fullcalendar/google-calendar'; // Google Calendar plugin
import dayGridPlugin from '@fullcalendar/daygrid'; // Month view
import timeGridPlugin from '@fullcalendar/timegrid';
import './index.css'
import './CalendarUI.css';
import { useNavigate, useLocation } from "react-router-dom";
import { googleLogout } from "@react-oauth/google";
import Switch from "react-switch";

function CalendarUI() {
    const calendarRef = useRef(null);
    const navigate = useNavigate();
    const location = useLocation();
    const { calendarId, accessToken } = location.state || {};
    const [userInput, setUserInput] = useState('');
    const [alertsState, setAlertsState] = useState(true);

    const fetchCalendar = () => {
        try {
            let calendar = new Calendar(calendarRef.current, {
                plugins: [googleCalendarPlugin, dayGridPlugin, timeGridPlugin],
                initialView: 'dayGridMonth',
                headerToolbar: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                googleCalendarApiKey: 'GOOGLE_CALENDAR_API_KEY',
                events: {
                    googleCalendarId: calendarId,
                    extraParams: {
                        access_token: accessToken
                    }
                }
            });
            calendar.render();
        } catch (error) {
            console.log("Error loading in calendar:", error);
        }
    };

    useEffect(() => {
        fetchCalendar();
    }, [calendarId, accessToken]);


    const logOut = () => {
        console.log("Logout successful");
        googleLogout();
        navigate("/");
    }

    async function handleSubmit(event) {
        event.preventDefault();
        console.log(userInput);
        setUserInput("");
        const apiResponse = await fetch(`http://localhost:8080/api/v1/event?userInput=${encodeURIComponent(userInput)}`, {
            method: 'POST',
        });
        fetchCalendar();
    }

    async function handleSwitch() {
        setAlertsState(!alertsState);
        const apiResponse = await fetch(`http://localhost:8080/api/v1/event/alert`, {
            method: 'POST',
        });
    }

    return (
        <div className="CalendarUI">
            <button id="logoutButton" onClick={logOut}>Log out</button>
            <div id="calendar" ref={calendarRef}/>
            <div className="input-container">
                <form onSubmit={handleSubmit}>
                    <input id="textInput"
                           value={userInput}
                           onChange={(event) => setUserInput(event.target.value)}
                    />
                    <button type="submit">Submit</button>
                </form>
            </div>
            <div className="switch-container">
                <label htmlFor="switch">{alertsState ? "Alerts on" : "Alerts off"}</label>
                <Switch id="switch" checked={alertsState} onChange={handleSwitch}/>
            </div>

        </div>

    )
        ;
}

export default CalendarUI;
