import React, { useState } from "react";
import "./Login.css";          // ← new stylesheet

export default function Login() {
  const [email,    setEmail]    = useState("");
  const [password, setPassword] = useState("");
  const [error,    setError]    = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const res  = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email:    email.trim(),
          password: password.trim(),
        }),
      });
      const text = await res.text();

      if (res.ok && text.startsWith("Login successful")) {
        const role = text.split(", ")[1] || "";
        localStorage.setItem("staffEmail", email.trim());
        localStorage.setItem("staffRole",  role);

        const route = {
          kitchen_staff: "/kitchen-panel",
          waiter:        "/waiter-panel",
          manager:       "/manager-panel",
        }[role];

        window.location.href = route || "/";
      } else {
        setError(text);
      }
    } catch {
      setError("Unable to reach server. Try again.");
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

        <button type="submit">Log&nbsp;in</button>
      </form>
    </div>
  );
}
