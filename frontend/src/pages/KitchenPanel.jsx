import React, { useState, useEffect } from "react";

function KitchenPanel() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const [reply, setReply] = useState({});

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

    const sendReply = (orderId) => {
        const message = reply[orderId];
        if (!message) return;

        fetch(`http://localhost:8080/api/orders/${orderId}/comment`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ comment: message, sender: "kitchen" })
        })
            .then(() => {
                setReply({ ...reply, [orderId]: "" });
                window.location.reload();
            })
            .catch(() => alert("Failed to send message."));
    };

    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div style={{ padding: "20px" }}>
            <h2>Kitchen Panel</h2>
            <p>Logged in as: {email} ({role})</p>

            {orders.length === 0 ? (
                <p>No orders found.</p>
            ) : (
                orders.map((order) => (
                    <div key={order.id} style={{ border: "1px solid #ccc", marginBottom: "20px", padding: "10px" }}>
                        <h4>Order ID: {order.id} | Table: {order.tableNumber}</h4>
                        <p>Status: {order.status}</p>
                        <select value={order.status} onChange={(e) => updateStatus(order.id, e.target.value)}>
                            <option value="Pending">Pending</option>
                            <option value="In Preparation">In Preparation</option>
                            <option value="Almost Ready">Almost Ready</option>
                            <option value="Ready">Ready</option>
                            <option value="Delivered">Delivered</option>
                        </select>

                        <p><strong>Items:</strong> {order.orderedItems.map(item => item.name).join(", ")}</p>

                        <h5>Chat History:</h5>
                        <div style={{ border: "1px solid #eee", padding: "5px", maxHeight: "150px", overflowY: "auto" }}>
                            {order.commentsHistory && order.commentsHistory.map((c, idx) => (
                                <div key={idx}>
                                    <b>{c.sender}:</b> {c.message} ({new Date(c.timestamp).toLocaleString()})
                                </div>
                            ))}
                            {!order.commentsHistory?.length && <p>No comments yet.</p>}
                        </div>

                        <textarea
                            rows="2"
                            style={{ width: "100%", marginTop: "5px" }}
                            value={reply[order.id] || ""}
                            onChange={(e) => setReply({ ...reply, [order.id]: e.target.value })}
                            placeholder="Reply to customer..."
                        />
                        <button onClick={() => sendReply(order.id)}>Send Reply</button>
                    </div>
                ))
            )}
        </div>
    );
}

export default KitchenPanel;
