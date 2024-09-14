import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { GoogleOAuthProvider } from "@react-oauth/google";
import CalendarUI from "./Calendar";
import {BrowserRouter, Route, Routes} from "react-router-dom";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <GoogleOAuthProvider clientId="897659353364-t61qemfnk5jiju4b1ernfce6m46jicli.apps.googleusercontent.com">
      <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/calendar" element={<CalendarUI />} />
            </Routes>
        </BrowserRouter>
      </React.StrictMode>
    </GoogleOAuthProvider>
);