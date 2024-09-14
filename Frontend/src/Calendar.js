import React, { useEffect, useRef } from 'react';
import { Calendar } from '@fullcalendar/core'; // Correctly import Calendar
import googleCalendarPlugin from '@fullcalendar/google-calendar'; // Google Calendar plugin
import dayGridPlugin from '@fullcalendar/daygrid'; // Month view
import timeGridPlugin from '@fullcalendar/timegrid';
import './App.css';
import { useNavigate, useLocation } from "react-router-dom";
import { googleLogout} from "@react-oauth/google";

function CalendarUI() {
    const calendarRef = useRef(null);
    const navigate = useNavigate();
    const location = useLocation();
    const { calendarId, accessToken } = location.state || {};

    useEffect(() => {
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
    }, [calendarId, accessToken]);

    const logOut = () => {
        console.log("Logout successful");
        googleLogout();
        navigate("/");
    }

    return (
        <div className="CalendarUI">
            <header className="Calendar-header">
                <h1>My Calendar</h1>
                <button id='logoutButton' onClick={logOut}>Log out</button>
                <div id="calendar" ref={calendarRef}></div>
            </header>
        </div>

    );
}

export default CalendarUI;
