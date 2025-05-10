import React, { useState, useEffect } from "react";

function WaiterPanel() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const role = localStorage.getItem("staffRole");
    const email = localStorage.getItem("staffEmail");

    // --- Fetch orders ---
    const fetchOrders = () => {
        fetch("http://localhost:8080/api/orders/waiter")
            .then(res => {
                if (!res.ok) throw new Error("Failed to load orders.");
                return res.json();
            })
            .then(data => setOrders(data))
            .catch(() => setError("Error fetching orders."));
    };

    useEffect(() => {
        if (role !== "waiter") {
            setError("Access denied. You are not a waiter.");
            return;
        }
        fetchOrders();
    }, [role]);

    // --- Mark Delivered ---
    const markDelivered = (orderId) => {
        fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: "Delivered" })
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to update status.");
                // ✅ After marking delivered, reload the list!
                fetchOrders();
            })
            .catch(() => alert("Failed to update status."));
    };

    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div style={{ padding: "20px" }}>
            <h2>Waiter Panel</h2>
            <p>Logged in as: {email} ({role})</p>

            {orders.length === 0 ? (
                <p>No completed orders to deliver.</p>
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
