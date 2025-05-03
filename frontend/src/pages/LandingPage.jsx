import React, { useEffect, useState } from "react";

function LandingPage() {
    const [tableId, setTableId] = useState("");
    const [menuItems, setMenuItems] = useState([]);
    const [order, setOrder] = useState({}); // NEW: To track item quantities

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const tableIdParam = params.get("tableId");
        setTableId(tableIdParam);

        fetch("http://localhost:8080/api/menu")
            .then(response => response.json())
            .then(data => setMenuItems(data))
            .catch(error => {
                console.error("Error fetching menu:", error);
            });
    }, []);

    // Group menu items by category
    const groupedMenu = menuItems.reduce((groups, item) => {
        const category = item.category || "Others";
        if (!groups[category]) groups[category] = [];
        groups[category].push(item);
        return groups;
    }, {});

    // NEW: Add item to order
    const addToOrder = (item) => {
        setOrder(prev => ({
            ...prev,
            [item.id]: (prev[item.id] || 0) + 1
        }));
    };

    // NEW: Remove item from order
    const removeFromOrder = (item) => {
        setOrder(prev => {
            const newOrder = { ...prev };
            if (newOrder[item.id]) {
                newOrder[item.id]--;
                if (newOrder[item.id] <= 0) {
                    delete newOrder[item.id];
                }
            }
            return newOrder;
        });
    };

    // NEW: Calculate total price
    const total = Object.entries(order).reduce((sum, [id, qty]) => {
        const item = menuItems.find(i => i.id === parseInt(id));
        return sum + (item ? item.price * qty : 0);
    }, 0);

    return (
        <div style={{ textAlign: "center", marginTop: "30px" }}>
            <h1>Welcome to Our Restaurant</h1>
            {tableId ? (
                <h2>Table: {tableId}</h2>
            ) : (
                <h2>No Table ID found</h2>
            )}

            <h3>Menu</h3>
            {menuItems.length === 0 ? (
                <p>Loading menu or no items found.</p>
            ) : (
                Object.entries(groupedMenu).map(([category, items]) => (
                    <div key={category}>
                        <h4>{category}</h4>
                        <ul style={{ listStyleType: "none", padding: 0 }}>
                            {items.map(item => (
                                <li key={item.id} style={{ marginBottom: "10px" }}>
                                    <strong>{item.name}</strong> - ${item.price.toFixed(2)}
                                    <br />
                                    <em>{item.description}</em>
                                    <div style={{ marginTop: "5px" }}>
                                        <button onClick={() => removeFromOrder(item)}>-</button>
                                        <span style={{ margin: "0 10px" }}>
                                            {order[item.id] || 0}
                                        </span>
                                        <button onClick={() => addToOrder(item)}>+</button>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>
                ))
            )}

            <h3>Total: ${total.toFixed(2)}</h3>
        </div>
    );
}

export default LandingPage;
