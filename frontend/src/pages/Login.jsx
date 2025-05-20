import React, { useState } from "react";
import "./Login.css";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const res = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: email.trim(),
                    password: password.trim(),
                }),
            });

            const text = await res.text();
            setLoading(false);

            if (res.ok && text.startsWith("Login successful")) {
                const role = text.split(", ")[1] || "";
                localStorage.setItem("staffEmail", email.trim());
                localStorage.setItem("staffRole", role);

                const route = {
                    kitchen_staff: "/kitchen-panel",
                    waiter: "/waiter-panel",
                    manager: "/manager-panel",
                }[role];

                window.location.href = route || "/";
            } else {
                setError(text || "Login failed. Please try again.");
            }
        } catch {
            setLoading(false);
            setError("Unable to reach server. Please try again.");
        }
    };

    return (
        <div className="login-bg">
            <form className="login-card" onSubmit={handleLogin}>
                <h2>Welcome back 👋</h2>
                <p className="sub">Staff access portal</p>

                <label htmlFor="email">Email</label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />

                <label htmlFor="pwd">Password</label>
                <input
                    id="pwd"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                {error && <p className="error">{error}</p>}
                {loading && <p className="loading">Logging in...</p>}

                <button type="submit" disabled={loading}>
                    {loading ? "Please wait..." : "Log in"}
                </button>
            </form>
        </div>
    );
}
