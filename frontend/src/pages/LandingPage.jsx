import React, { useEffect, useState, useMemo } from "react";
import "./LandingPage.css";

export default function LandingPage() {
  /* ---------------- state ---------------- */
  const [tableId, setTableId]   = useState("");
  const [menuItems, setMenuItems] = useState([]);
  const [order, setOrder]       = useState({});
  const [message, setMessage]   = useState("");
  const [comments, setComments] = useState("");

  /* filter / sort UI */
  const [filterCat,  setFilterCat]  = useState("All");
  const [sortOption, setSortOption] = useState("name"); // name | price | flag

  /* ---------------- fetch menu + table number ---------------- */
  useEffect(() => {
    /* detect table # from url, path, or localStorage */
    const params = new URLSearchParams(window.location.search);
    let id = params.get("tableId");

    if (!id) {
      const parts = window.location.pathname.split("/");
      const idx = parts.findIndex((p) => p === "table");
      if (idx !== -1 && parts[idx + 1]) id = parts[idx + 1];
    }
    if (!id) id = localStorage.getItem("tableNumber") || "";

    setTableId(id);
    if (id) localStorage.setItem("tableNumber", id);

    /* fetch menu */
    fetch("http://localhost:8080/api/menu")
      .then((r) => r.json())
      .then(setMenuItems)
      .catch(console.error);
  }, []);

  /* ---------------- derive visible list ---------------- */
  /* 1) filter */
  const filtered = useMemo(
    () =>
      filterCat === "All"
        ? menuItems
        : menuItems.filter((m) => m.category === filterCat),
    [menuItems, filterCat]
  );

  /* 2) sort */
  const sorted = useMemo(() => {
    const arr = [...filtered];
    switch (sortOption) {
      case "price":
        return arr.sort((a, b) => a.price - b.price);
      case "flag":
        return arr.sort((a, b) =>
          (a.flagEmoji || "").localeCompare(b.flagEmoji || "")
        );
      case "name":
      default:
        return arr.sort((a, b) => a.name.localeCompare(b.name));
    }
  }, [filtered, sortOption]);

  /* 3) group by category */
  const grouped = useMemo(() => {
    return sorted.reduce((g, it) => {
      (g[it.category] ||= []).push(it);
      return g;
    }, {});
  }, [sorted]);

  /* ---------------- qty handlers ---------------- */
  const add  = (it) =>
    setOrder((p) => ({ ...p, [it.id]: (p[it.id] || 0) + 1 }));
  const sub  = (it) =>
    setOrder((p) => {
      const n = { ...p };
      if (n[it.id]) {
        n[it.id]--;
        if (n[it.id] <= 0) delete n[it.id];
      }
      return n;
    });

  /* ---------------- totals ---------------- */
  const total = Object.entries(order).reduce((sum, [id, q]) => {
    const it = menuItems.find((m) => m.id === Number(id));
    return sum + (it ? it.price * q : 0);
  }, 0);

  /* ---------------- place order ---------------- */
  const placeOrder = async () => {
    if (!tableId || total === 0) return;

    const items = Object.entries(order).map(([id, q]) => {
      const it = menuItems.find((m) => m.id === Number(id));
      return { name: it.name, quantity: q, price: it.price };
    });

    /* ⚠️ use the field name your backend expects.
       If it expects "customerComments", rename below. */
    const payload = {
      tableNumber: tableId,
      orderedItems: items,
      totalPrice: total,
      customerComments: comments // <-- sent to backend
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
      setComments("");
    } catch (e) {
      console.error(e);
      setMessage("Order failed – try again.");
    }
  };

  /* ---------------- render ---------------- */
  return (
    <div className="landing-container">
      <h1>Table {tableId || "?"}</h1>

      {/* filter + sort bar */}
      <div className="sort-bar">
        <label htmlFor="cat">Category:</label>
        <select
          id="cat"
          value={filterCat}
          onChange={(e) => setFilterCat(e.target.value)}
        >
          {["All", ...new Set(menuItems.map((m) => m.category))].map((c) => (
            <option key={c}>{c}</option>
          ))}
        </select>

        <label htmlFor="sort">Sort by:</label>
        <select
          id="sort"
          value={sortOption}
          onChange={(e) => setSortOption(e.target.value)}
        >
          <option value="name">Name (A-Z)</option>
          <option value="price">Price (low-high)</option>

        </select>
      </div>

      {/* menu list */}
      {Object.keys(grouped).map((cat) => (
        <div key={cat} className="category">
          <h3>{cat}</h3>

          {grouped[cat].map((it) => (
            <div key={it.id} className="menu-item">
              <div className="item-header">
                {it.flagEmoji && <span className="flag">{it.flagEmoji}</span>}
                <b>{it.name}</b> — ${it.price.toFixed(2)}
              </div>

              {it.imageUrl && (
                <img
                  src={it.imageUrl}
                  alt={it.name}
                  className="item-image"
                  onError={(e) =>
                    (e.target.src =
                      "https://via.placeholder.com/600x180?text=No+Image")
                  }
                />
              )}

              <small>{it.description}</small>

              {/* inline qty bar */}
              <div className="qty-bar">
                <button onClick={() => sub(it)}>-</button>
                <span className="quantity">{order[it.id] || 0}</span>
                <button onClick={() => add(it)}>+</button>
              </div>
            </div>
          ))}
        </div>
      ))}

      <h2 className="total">Total: ${total.toFixed(2)}</h2>

      <textarea
        placeholder="Add special instructions for the kitchen…"
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
          onClick={() =>
            (window.location.href = `/trackorder?tableId=${tableId}`)
          }
          className="track-order-btn"
        >
          Track My Order
        </button>
      )}
    </div>
  );
}
