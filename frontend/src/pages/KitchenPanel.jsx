import React, { useEffect, useState } from "react";
import axios from "axios";

function KitchenPanel() {
    const [orders, setOrders] = useState([]);
    const [statusUpdates, setStatusUpdates] = useState({});

    // Fetch kitchen orders (NOT completed)
    const fetchOrders = () => {
        axios
            .get("http://localhost:8080/api/orders/kitchen")
            .then((response) => {
                setOrders(response.data);
            })
            .catch((error) => {
                console.error("Error fetching orders:", error);
            });
    };

    // On first load
    useEffect(() => {
        fetchOrders();
    }, []);

    // Handle status change in dropdown
    const handleStatusChange = (orderId, newStatus) => {
        setStatusUpdates((prev) => ({
            ...prev,
            [orderId]: newStatus,
        }));
    };

    // Save status change to backend
    const saveStatus = (orderId) => {
        const newStatus = statusUpdates[orderId];
        axios
            .put(`http://localhost:8080/api/orders/${orderId}/status`, {
                status: newStatus,
            })
            .then(() => {
                alert(`Order ${orderId} updated to ${newStatus}`);
                fetchOrders(); // Refresh list
            })
            .catch((error) => {
                console.error("Error updating status:", error);
            });
    };

    return (
        <div style={{ padding: "20px" }}>
            <h2>Kitchen Panel</h2>
            {orders.length === 0 ? (
                <p>No active orders</p>
            ) : (
                <table border="1" cellPadding="10">
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
                    {orders.map((order) => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{order.tableNumber}</td>
                            <td>
                                {order.items && order.items.map((item) => (
                                    <div key={item.name}>
                                        {item.name} x {item.quantity}
                                    </div>
                                ))}
                            </td>
                            <td>
                                <select
                                    value={statusUpdates[order.id] || order.status}
                                    onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                >
                                    <option value="NEW">NEW</option>
                                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                                    <option value="COMPLETED">COMPLETED</option>
                                </select>
                            </td>
                            <td>
                                <button onClick={() => saveStatus(order.id)}>
                                    Save Status
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

export default KitchenPanel;
