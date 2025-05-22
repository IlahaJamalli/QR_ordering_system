import React, { useEffect, useState } from "react";
import axios from "axios";
import "./KitchenPanel.css";

function KitchenPanel() {
    const [orders, setOrders] = useState([]);
    const [statusUpdates, setStatusUpdates] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const fetchOrders = () => {
        setLoading(true);
        setError("");

        axios
            .get("http://localhost:8080/api/orders/kitchen")
            .then((res) => {
                const withChat = res.data.map((o) => ({ ...o, replyText: "" }));
                setOrders(withChat);
            })
            .catch((err) => {
                console.error("Error fetching orders:", err);
                setError("Failed to load orders. Please try again.");
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchOrders();
    }, []);

    const handleStatusChange = (orderId, newStatus) => {
        setStatusUpdates((prev) => ({
            ...prev,
            [orderId]: newStatus,
        }));
    };

    const saveStatus = (orderId) => {
        const newStatus = statusUpdates[orderId];
        if (!newStatus) return;

        setLoading(true);
        axios
            .put(`http://localhost:8080/api/orders/${orderId}/status`, {
                status: newStatus,
            })
            .then(() => {
                fetchOrders();
            })
            .catch((err) => {
                console.error("Error updating status:", err);
                setError("Failed to update status.");
            })
            .finally(() => setLoading(false));
    };

    const sendReply = async (orderId, message) => {
        if (!message.trim()) return;

        try {
            setLoading(true);
            await axios.put(`http://localhost:8080/api/orders/${orderId}/comment`, {
                sender: "kitchen",
                comment: message.trim(),
            });

            setOrders((prev) =>
                prev.map((o) =>
                    o.id === orderId ? { ...o, replyText: "" } : o
                )
            );
            fetchOrders();
        } catch (err) {
            console.error("Failed to send reply", err);
            setError("Failed to send chat message.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="kitchen-wrapper">
            <div className="kitchen-panel">
                <h2>Kitchen Panel</h2>

                {loading && <div className="loader">Loading orders...</div>}
                {error && <div className="error-message">⚠️ {error}</div>}

                {!loading && !error && orders.length === 0 && (
                    <p className="dimmed">No active orders</p>
                )}

                {!loading && !error &&
                    orders.map((order) => (
                        <div key={order.id} className="order-card">
                            <div className="order-meta">
                                <strong>Order ID:</strong> <span>{order.id}</span> <br />
                                <strong>Table:</strong> {order.tableNumber}
                            </div>

                            <div className="order-items">
                                {(order.orderedItems || []).map((item, i) => (
                                    <div key={i} style={{ marginBottom: "8px" }}>
                                        <div>
                                            🍽️ <strong>{item.name}</strong> × {item.quantity}
                                        </div>
                                        {item.customizations && item.customizations.length > 0 && (
                                            <div className="customization-display">
                                                <span className="customization-label">➤ Customizations:</span>
                                                <ul className="customization-list">
                                                    {item.customizations.map((c, idx) => (
                                                        <li key={idx}>🛠️ {c}</li>
                                                    ))}
                                                </ul>
                                            </div>
                                        )}

                                    </div>
                                ))}
                            </div>

                            <div className="order-controls">
                                <label>Status:</label>
                                <select
                                    value={statusUpdates[order.id] || order.status}
                                    onChange={(e) =>
                                        handleStatusChange(order.id, e.target.value)
                                    }
                                >
                                    <option value="NEW">NEW</option>
                                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                                    <option value="COMPLETED">COMPLETED</option>
                                </select>
                                <button onClick={() => saveStatus(order.id)}>
                                    💾 Save Status
                                </button>
                            </div>

                            <div className="chat-box">
                                <strong>Chat:</strong>
                                <div className="chat-history">
                                    {(order.commentsHistory || []).map((c, i) => (
                                        <div key={i} className={`chat-bubble ${c.sender}`}>
                                            <b>{c.sender}:</b> {c.message}
                                        </div>
                                    ))}
                                </div>
                                <textarea
                                    rows="1"
                                    placeholder="Type your reply to customer..."
                                    value={order.replyText}
                                    onChange={(e) =>
                                        setOrders((prev) =>
                                            prev.map((o) =>
                                                o.id === order.id
                                                    ? { ...o, replyText: e.target.value }
                                                    : o
                                            )
                                        )
                                    }
                                />
                                <button
                                    className="send-btn"
                                    onClick={() => sendReply(order.id, order.replyText)}
                                    disabled={!order.replyText?.trim()}
                                >
                                    📤 Send Reply
                                </button>
                            </div>
                        </div>
                    ))}
            </div>
        </div>
    );
}

export default KitchenPanel;
