import React, { useState, useEffect } from "react";

function KitchenPanel() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const role = localStorage.getItem("staffRole");
    const email = localStorage.getItem("staffEmail");

    useEffect(() => {
        if (role !== "kitchen_staff") {
            setError("Access denied. You are not kitchen staff.");
            return;
        }

        fetch("http://localhost:8080/api/orders")
            .then((res) => res.json())
            .then((data) => {
                // Ensure data is an array (important!)
                if (Array.isArray(data)) {
                    setOrders(data);
                } else {
                    setError("Failed to load orders: unexpected response format.");
                }
            })
            .catch(() => setError("Failed to fetch orders."));
    }, [role]);

    const updateStatus = (orderId, newStatus) => {
        fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: newStatus })
        })
            .then((res) => {
                if (res.ok) {
                    // Update the order status in state
                    setOrders((prev) =>
                        prev.map((order) =>
                            order.id === orderId ? { ...order, status: newStatus } : order
                        )
                    );
                } else {
                    alert("Failed to update status.");
                }
            })
            .catch(() => alert("Failed to update status."));
    };

    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div style={{ padding: "20px" }}>
            <h2>Kitchen Panel</h2>
            <p>Logged in as: {email} ({role})</p>

            {orders.length === 0 ? (
                <p>No orders found.</p>
            ) : (
                <table border="1" cellPadding="10" style={{ width: "100%", marginTop: "20px" }}>
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Table</th>
                            <th>Items</th>
                            <th>Status</th>
                            <th>Change Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>{order.tableNumber}</td>
                                <td>
                                    {order.orderedItems
                                        ? order.orderedItems.map((item) => item.name).join(", ")
                                        : "No items"}
                                </td>
                                <td>{order.status}</td>
                                <td>
                                    <select
                                        value={order.status}
                                        onChange={(e) => updateStatus(order.id, e.target.value)}
                                    >
                                        <option value="Pending">Pending</option>
                                        <option value="In Preparation">In Preparation</option>
                                        <option value="Almost Ready">Almost Ready</option>
                                        <option value="Ready">Ready</option>
                                        <option value="Delivered">Delivered</option>
                                    </select>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default KitchenPanel;
