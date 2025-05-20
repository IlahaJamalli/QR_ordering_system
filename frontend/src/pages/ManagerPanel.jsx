import React, { useEffect, useState } from "react";
import "./ManagerPanel.css";

function ManagerPanel() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const role = localStorage.getItem("staffRole");
    const email = localStorage.getItem("staffEmail");

    useEffect(() => {
        if (role !== "manager") {
            setError("Access denied. You are not a manager.");
            setLoading(false);
            return;
        }

        fetch("http://localhost:8080/api/orders")
            .then(res => {
                if (!res.ok) throw new Error("Failed to load");
                return res.json();
            })
            .then(data => {
                if (Array.isArray(data)) {
                    setOrders(data);
                } else {
                    setError("Unexpected data format received.");
                }
            })
            .catch(() => setError("Error fetching orders."))
            .finally(() => setLoading(false));
    }, [role]);

    return (
        <div className="manager-panel-container">
            <h2>Manager Panel</h2>
            <p>Logged in as: {email} ({role})</p>

            {loading && <p className="loading-msg">Loading orders...</p>}
            {error && <p className="error-msg">{error}</p>}

            {!loading && !error && orders.length === 0 && (
                <p className="dimmed">No orders found.</p>
            )}

            {!loading && !error && orders.length > 0 && (
                <table>
                    <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Table</th>
                        <th>Items</th>
                        <th>Status</th>
                        <th>Price</th>
                        <th>Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    {orders.map(order => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{order.tableNumber}</td>
                            <td>
                                {order.orderedItems
                                    .map(item => `${item.name} (x${item.quantity})`)
                                    .join(", ")}
                            </td>
                            <td>
                                    <span className={`status-badge ${order.status.toUpperCase()}`}>
                                        {order.status}
                                    </span>
                            </td>
                            <td>${order.totalPrice.toFixed(2)}</td>
                            <td>{new Date(order.orderedTime).toLocaleString()}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default ManagerPanel;
