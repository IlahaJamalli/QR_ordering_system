import React from "react";

function KitchenPanel() {
    const email = localStorage.getItem("staffEmail");
    const role = localStorage.getItem("staffRole");

    return (
        <div style={{ padding: "20px" }}>
            <h2>Welcome to Kitchen Panel</h2>
            <p>Logged in as: {email} ({role})</p>
            <p>Here you will see incoming orders soon!</p>
        </div>
    );
}

export default KitchenPanel;
