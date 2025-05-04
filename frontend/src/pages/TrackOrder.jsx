import React, { useEffect, useState } from 'react';
import axios from 'axios';

function TrackOrder() { 
    const searchParams = new URLSearchParams(window.location.search);
    const tableNumber = searchParams.get("tableId") || "A1";

    const [order, setOrder] = useState(null);
    const [newComment, setNewComment] = useState("");

    useEffect(() => {
        axios.get(`http://localhost:8080/api/orders?tableNumber=${tableNumber}`)
            .then(response => {
                if (response.data.length > 0) {
                    const latestOrder = response.data.reduce((latest, current) =>
                        new Date(current.orderedTime) > new Date(latest.orderedTime) ? current : latest
                    );
                    setOrder(latestOrder);
                }
            })
            .catch(error => console.error("Error fetching order:", error));
    }, [tableNumber]);

    const getStatusColor = (status) => {
        switch (status) {
            case 'Pending': return 'orange';
            case 'In Preparation': return 'blue';
            case 'Almost Ready': return 'purple';
            case 'Ready': return 'green';
            case 'Delivered': return 'gray';
            default: return 'black';
        }
    };

    const submitNewComment = () => {
        if (!newComment.trim()) return;
        axios.put(`http://localhost:8080/api/orders/${order.id}/comment`, { 
            comment: newComment,
            sender: "customer"
        })
            .then(() => {
                setNewComment("");
                window.location.reload();
            })
            .catch(err => console.error(err));
    };

    return (
        <div style={{ maxWidth: '600px', margin: 'auto', padding: '20px', fontFamily: 'Arial' }}>
            <h2>Track Your Order</h2>

            {!order && <p>Loading order details...</p>}

            {order && (
                <div style={{ border: '1px solid #ccc', borderRadius: '10px', padding: '20px' }}>
                    <h3>Table: {order.tableNumber}</h3>
                    <p><strong>Status:</strong> <span style={{ color: getStatusColor(order.status) }}>{order.status}</span></p>
                    <p><strong>Ordered Time:</strong> {new Date(order.orderedTime).toLocaleString()}</p>

                    <h4>Items:</h4>
                    <ul>
                        {order.orderedItems.map((item, index) => (
                            <li key={index}>
                                {item.name} x {item.quantity} — ${(item.price * item.quantity).toFixed(2)}
                            </li>
                        ))}
                    </ul>

                    <h4>Total Price: ${order.totalPrice.toFixed(2)}</h4>

                    <h4>Chat with Kitchen:</h4>
                    <div style={{ maxHeight: "200px", overflowY: "auto", border: "1px solid #ddd", padding: "10px", marginBottom: "10px" }}>
                        {order.commentsHistory && order.commentsHistory.map((c, idx) => (
                            <div key={idx} style={{ 
                                textAlign: c.sender === "customer" ? "right" : "left",
                                backgroundColor: c.sender === "customer" ? "#e1f5fe" : "#ffe0b2",
                                margin: "5px",
                                padding: "5px",
                                borderRadius: "5px"
                            }}>
                                <b>{c.sender}:</b> {c.message} <br/>
                                <small>{new Date(c.timestamp).toLocaleString()}</small>
                            </div>
                        ))}
                        {!order.commentsHistory?.length && <p>No comments yet.</p>}
                    </div>

                    <textarea 
                        value={newComment} 
                        onChange={(e) => setNewComment(e.target.value)} 
                        rows="2" 
                        style={{ width: "100%" }}
                        placeholder="Type your message to the kitchen..."
                    />
                    <button onClick={submitNewComment} style={{ marginTop: "5px" }}>Send Message</button>
                </div>
            )}
        </div>
    );
}

export default TrackOrder;
