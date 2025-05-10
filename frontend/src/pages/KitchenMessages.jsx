import React, { useEffect, useState } from "react";
import axios from "axios";

function KitchenMessages() {
    const [messages, setMessages] = useState([]);

    const fetchMessages = () => {
        axios
            .get("http://localhost:8080/api/kitchen/messages")
            .then((response) => {
                setMessages(response.data);
            })
            .catch((error) => {
                console.error("Error fetching messages:", error);
            });
    };

    const markAsProcessed = (id) => {
        axios
            .put(`http://localhost:8080/api/kitchen/messages/${id}/mark-processed`)
            .then(() => {
                fetchMessages(); // Refresh after marking
            })
            .catch((error) => {
                console.error("Error marking message as processed:", error);
            });
    };

    useEffect(() => {
        fetchMessages();
    }, []);

    return (
        <div style={{ padding: "20px" }}>
            <h2>Kitchen Messages</h2>
            <table border="1" cellPadding="10">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Content</th>
                    <th>Processed</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {messages.map((msg) => (
                    <tr key={msg.id}>
                        <td>{msg.id}</td>
                        <td>{msg.content}</td>
                        <td>{msg.processed ? "✅" : "❌"}</td>
                        <td>
                            {!msg.processed && (
                                <button onClick={() => markAsProcessed(msg.id)}>
                                    Mark as processed
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default KitchenMessages;
