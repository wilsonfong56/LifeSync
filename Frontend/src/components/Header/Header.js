import React from "react";
import "./Header.css";
import { Calendar } from 'lucide-react';

function Header({ onLogin }) {
    return (
        <>
            <header className="fixed w-full bg-white/80 backdrop-blur-md border-b border-gray-100 z-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <div className="flex justify-between items-center py-4">
                        {/* Logo */}
                        <div className="flex items-center">
                            <a href="/" className="flex items-center space-x-2">
                                <Calendar className="h-8 w-8 text-indigo-600"/>
                                <span
                                    className="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
                                  LifeSync
                                </span>
                            </a>
                        </div>

                        <nav className="hidden md:flex items-center space-x-8">
                            <a href="#features" className="text-gray-600 hover:text-indigo-600 transition-colors">
                                Features
                            </a>
                            <a href="#about" className="text-gray-600 hover:text-indigo-600 transition-colors">
                                About
                            </a>
                            <a href="#contact" className="text-gray-600 hover:text-indigo-600 transition-colors">
                                Contact
                            </a>
                            <button
                                onClick={onLogin}
                                className="bg-indigo-600 text-white px-6 py-2 rounded-full hover:bg-indigo-700 transition-colors flex items-center space-x-2"
                            >
                                Login with Google
                            </button>
                        </nav>
                    </div>
                </div>
            </header>
        </>
    );
}

export default Header;
