import React, { useEffect, useState } from "react";
import "./LandingPage.css"; // ✅ CSS stays the same

export default function LandingPage() {
  const [tableId, setTableId] = useState("");
  const [menuItems, setMenuItems] = useState([]);
  const [order, setOrder] = useState({});            // {itemId: qty}
  const [message, setMessage] = useState("");
  const [comments, setComments] = useState("");       // ✅ NEW

  // --- load table id & menu -----------------------------
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    let id = params.get("tableId");

    // ✅ If no ?tableId param, try to get from the URL path like /menu/table/5
    if (!id) {
      const pathParts = window.location.pathname.split("/");
      const index = pathParts.findIndex(p => p === "table");
      if (index !== -1 && pathParts[index + 1]) {
        id = pathParts[index + 1];
      }
    }

    // ✅ If still no ID, try to get from localStorage (returning customer)
    if (!id) {
      id = localStorage.getItem("tableNumber") || "";
    }

    setTableId(id);

    // ✅ Save to localStorage so future pages can use it
    if (id) {
      localStorage.setItem("tableNumber", id);
    }

    fetch("http://localhost:8080/api/menu")
        .then((res) => res.json())
        .then(setMenuItems)
        .catch((err) => console.error(err));
  }, []);

  // --- helpers ------------------------------------------
  const grouped = menuItems.reduce((g, it) => {
    (g[it.category] ||= []).push(it);
    return g;
  }, {});

  const add = (it) =>
      setOrder((p) => ({ ...p, [it.id]: (p[it.id] || 0) + 1 }));
  const sub = (it) =>
      setOrder((p) => {
        const n = { ...p };
        if (n[it.id]) {
          n[it.id]--;
          if (n[it.id] <= 0) delete n[it.id];
        }
        return n;
      });

  const total = Object.entries(order).reduce((sum, [id, q]) => {
    const it = menuItems.find((m) => m.id === Number(id));
    return sum + (it ? it.price * q : 0);
  }, 0);

  // --- place order --------------------------------------
  const placeOrder = async () => {
    if (!tableId || total === 0) return;

    const items = Object.entries(order).map(([id, q]) => {
      const it = menuItems.find((m) => m.id === Number(id));
      return { name: it.name, quantity: q, price: it.price };
    });

    const payload = {
      tableNumber: tableId,
      orderedItems: items,
      totalPrice: total,
      customerComments: comments   // ✅ NEW field
    };

    try {
      const res = await fetch("http://localhost:8080/api/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) throw new Error("failed");
      const saved = await res.json();
      setMessage(`Order #${saved.id || saved._id || "(saved)"} placed!`);
      setOrder({});
      setComments("");  // ✅ reset comment box
    } catch (e) {
      console.error(e);
      setMessage("Order failed – try again.");
    }
  };

  // --- UI -----------------------------------------------
  return (
      <div className="landing-container">
        <h1>Table {tableId || "?"}</h1>

        {Object.keys(grouped).map((cat) => (
            <div key={cat} className="category">
              <h3>{cat}</h3>
              {grouped[cat].map((it) => (
                  <div key={it.id} className="menu-item">
                    <b>{it.name}</b> – ${it.price.toFixed(2)}
                    <br />
                    <small>{it.description}</small>
                    <br />
                    <button onClick={() => sub(it)}>-</button>
                    <span className="quantity">{order[it.id] || 0}</span>
                    <button onClick={() => add(it)}>+</button>
                  </div>
              ))}
            </div>
        ))}

        <h2>Total: ${total.toFixed(2)}</h2>

        <textarea
            placeholder="Add special instructions for the kitchen..."
            value={comments}
            onChange={(e) => setComments(e.target.value)}
            className="comments-box"
        />

        <button
            onClick={placeOrder}
            disabled={!tableId || total === 0}
            className="place-order-btn"
        >
          Place Order
        </button>

        {message && <p className="message">{message}</p>}

        {tableId && (
            <button
                onClick={() => window.location.href = `/track?tableId=${tableId}`}
                className="track-order-btn"
            >
              Track My Order
            </button>
        )}
      </div>
  );
}
