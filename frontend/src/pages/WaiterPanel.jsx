import React, { useEffect, useState } from "react";
import "./WaiterPanel.css";

function WaiterPanel() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const role = localStorage.getItem("staffRole");
    const email = localStorage.getItem("staffEmail");

    const fetchOrders = () => {
        fetch("http://localhost:8080/api/orders/waiter")
            .then((res) => {
                if (!res.ok) throw new Error("Failed to load orders.");
                return res.json();
            })
            .then((data) => setOrders(data))
            .catch(() => setError("Error fetching orders."));
    };

    useEffect(() => {
        if (role !== "waiter") {
            setError("Access denied. You are not a waiter.");
            return;
        }
        fetchOrders();
    }, [role]);

    const markDelivered = (orderId) => {
        fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: "Delivered" }),
        })
            .then((res) => {
                if (!res.ok) throw new Error("Failed to update status.");
                fetchOrders();
            })
            .catch(() => alert("Failed to update status."));
    };

    return (
        <div className="waiter-wrapper">
            <div className="waiter-panel">
                <h2>Waiter Panel</h2>
                <p>
                    Logged in as: <strong>{email}</strong> ({role})
                </p>

                {error && <p className="error">{error}</p>}

                {orders.length === 0 ? (
                    <p className="dim">No completed orders to deliver.</p>
                ) : (
                    orders.map((order) => (
                        <div key={order.id} className="order-card">
                            <div className="order-info">
                                <strong>Order ID:</strong> {order.id}
                                <br />
                                <strong>Table:</strong> {order.tableNumber}
                            </div>
                            <div className="item-list">
                                {order.orderedItems
                                    .map((item) => `${item.name} × ${item.quantity || 1}`)
                                    .join(", ")}
                            </div>
                            <div className="status">Status: {order.status}</div>
                            <button
                                className="deliver-btn"
                                onClick={() => markDelivered(order.id)}
                            >
                                Mark as Delivered
                            </button>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default WaiterPanel;
