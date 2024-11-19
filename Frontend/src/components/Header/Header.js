import React from "react";
import "./Header.css";

function Header({ onLogin }) {
    return (
        <header className="header">
            <div className="header-left">
                <h1 className="app-logo">LifeSync</h1>
            </div>
            <nav className="header-nav">
                <a href="#features" className="nav-link">Features</a>
                <a href="#about" className="nav-link">About</a>
                <a href="#contact" className="nav-link">Contact</a>
            </nav>
            <div className="header-right">
                <button className="login-button" onClick={onLogin}>
                    Login with Google
                </button>
            </div>
        </header>
    );
}

export default Header;
