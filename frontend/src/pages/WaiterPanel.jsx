import React, { useState, useEffect } from "react";

function WaiterPanel() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const role = localStorage.getItem("staffRole");
    const email = localStorage.getItem("staffEmail");

    useEffect(() => {
        if (role !== "waiter") {
            setError("Access denied. You are not a waiter.");
            return;
        }

        fetch("http://localhost:8080/api/orders")
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) {
                    // Only show orders that are ready to be delivered
                    const readyOrders = data.filter(order => order.status === "Ready");
                    setOrders(readyOrders);
                } else {
                    setError("Failed to load orders.");
                }
            })
            .catch(() => setError("Error fetching orders."));
    }, [role]);

    const markDelivered = (orderId) => {
        fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: "Delivered" })
        })
            .then(res => {
                if (res.ok) {
                    // Remove order from list
                    setOrders(prev => prev.filter(order => order.id !== orderId));
                } else {
                    alert("Failed to update status.");
                }
            })
            .catch(() => alert("Failed to update status."));
    };

    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div style={{ padding: "20px" }}>
            <h2>Waiter Panel</h2>
            <p>Logged in as: {email} ({role})</p>

            {orders.length === 0 ? (
                <p>No orders ready for delivery.</p>
            ) : (
                <table border="1" cellPadding="10" style={{ width: "100%", marginTop: "20px" }}>
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Table</th>
                            <th>Items</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map(order => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>{order.tableNumber}</td>
                                <td>{order.orderedItems.map(item => item.name).join(", ")}</td>
                                <td>{order.status}</td>
                                <td>
                                    <button onClick={() => markDelivered(order.id)}>
                                        Mark as Delivered
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default WaiterPanel;
