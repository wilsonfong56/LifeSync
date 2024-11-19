import React, {createContext, useEffect, useRef, useState} from 'react';
import { Calendar } from '@fullcalendar/core';
import googleCalendarPlugin from '@fullcalendar/google-calendar';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import './index.css'
import './CalendarPage.css';
import { useNavigate, useLocation } from "react-router-dom";
import { googleLogout } from "@react-oauth/google";
import Switch from "react-switch";
import ChatUI from "./components/ChatUI/ChatUI";

function CalendarPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const calendarRef = useRef(null);
    const { calendarId, accessToken } = location.state || {};
    const [alertsState, setAlertsState] = useState(true);

    const fetchCalendar = () => {
        console.log("Calendar fetching");
        try {
            let calendar = new Calendar(calendarRef.current, {
                plugins: [googleCalendarPlugin, dayGridPlugin, timeGridPlugin],
                initialView: 'dayGridMonth',
                headerToolbar: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                googleCalendarApiKey: 'AIzaSyDhF-MYO1Ukf2FgUD2341crTZ7--NiUEjs',
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

    async function handleSwitch() {
        setAlertsState(!alertsState);
        const apiResponse = await fetch(`http://localhost:8080/api/v1/event/alert`, {
            method: 'POST',
        });
    }

    return (
        <div className="CalendarUI">
            <button id="logoutButton" onClick={logOut}>Log out</button>
            <div className="flex-container">
                <div className="flex-child1">
                    <div id="calendar" ref={calendarRef}/>
                </div>
                <div className="flex-child2">
                    <div className="switch-container">
                        <Switch id="switch" checked={alertsState} onChange={handleSwitch}/>
                        <label htmlFor="switch">{alertsState ? "Alerts on" : "Alerts off"}</label>
                    </div>
                    <ChatUI fetchCalendar={ fetchCalendar }/>
                </div>
            </div>
        </div>

    )
        ;
}

export default CalendarPage;
