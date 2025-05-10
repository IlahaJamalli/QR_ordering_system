import React, { useState } from "react";
import bcrypt from "bcryptjs"; // ✅ already imported

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email: email.trim(), password: password.trim() })
            });

            console.log(response)
            const text = await response.text();

            if (response.ok && text.startsWith("Login successful")) {
                // Save login info in localStorage
                localStorage.setItem("staffEmail", email.trim());
                const role = text.split(", ")[1];
                localStorage.setItem("staffRole", role);

                // ✅ REDIRECT BASED ON ROLE
                if (role === "kitchen_staff") {
                    window.location.href = "/kitchen-panel";
                } else if (role === "waiter") {
                    window.location.href = "/waiter-panel";
                } else if (role === "manager") {
                    window.location.href = "/manager-panel";
                } else {
                    alert("Unknown role: " + role);
                }

            } else {
                setError(text);
            }
        } catch (err) {
            setError("Error connecting to server.");
        }
    };

    return (
        <div style={{ maxWidth: "400px", margin: "50px auto" }}>
            <h2>Staff Login</h2>
            <form onSubmit={handleLogin}>
                <div>
                    <label>Email:</label><br />
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label><br />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Login</button>
            </form>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
    );
}

export default Login;
