import React, { useState } from "react";
import bcrypt from "bcryptjs"; // ✅ ADD THIS

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    // ✅ Hash the password in the frontend for testing only
    let hashedPassword = "";
    if (password) {
        const salt = bcrypt.genSaltSync(10);
        hashedPassword = bcrypt.hashSync(password, salt);
    }

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

            const text = await response.text();

            if (response.ok && text.startsWith("Login successful")) {
                // Save login info in localStorage
                localStorage.setItem("staffEmail", email.trim());
                localStorage.setItem("staffRole", text.split(", ")[1]); // Get role

                window.location.href = "/kitchen-panel";
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

            {/* ✅ Show hashed password for testing */}
            <pre style={{ backgroundColor: "#f0f0f0", padding: "10px", marginTop: "20px" }}>
                <strong>Hashed password (frontend test):</strong><br />
                {hashedPassword}
            </pre>
        </div>
    );
}

export default Login;
