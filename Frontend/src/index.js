import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { GoogleOAuthProvider } from "@react-oauth/google";
import CalendarPage from "./CalendarPage";
import {BrowserRouter, Route, Routes} from "react-router-dom";
const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
      <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/calendar" element={<CalendarPage />} />
            </Routes>
        </BrowserRouter>
      </React.StrictMode>
    </GoogleOAuthProvider>
);
