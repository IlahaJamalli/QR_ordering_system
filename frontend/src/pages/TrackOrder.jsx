import React, { useEffect, useState } from 'react';
import axios from 'axios';

function TrackOrder() { 
    // Automatically read tableId from URL parameters
    const searchParams = new URLSearchParams(window.location.search);
    const tableNumber = searchParams.get("tableId") || "A1"; // fallback if missing

    const [order, setOrder] = useState(null);

    useEffect(() => {
        axios.get(`http://localhost:8080/api/orders?tableNumber=${tableNumber}`)
            .then(response => {
                if (response.data.length > 0) {
                    setOrder(response.data[0]);  // Latest order
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

                    <h4>Order History:</h4>
                    <ul>
                        {order.history.map((entry, index) => (
                            <li key={index}>
                                <strong>{entry.status}</strong> at {new Date(entry.timestamp).toLocaleString()}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

export default TrackOrder;
