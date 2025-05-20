import React, { useEffect, useState, useMemo } from "react";
import "./LandingPage.css";

export default function LandingPage() {
  const [tableId, setTableId] = useState("");
  const [menuItems, setMenuItems] = useState([]);
  const [order, setOrder] = useState({});
  const [customizations, setCustomizations] = useState({});
  const [message, setMessage] = useState("");
  const [comments, setComments] = useState("");
  const [filterCat, setFilterCat] = useState("All");
  const [sortOption, setSortOption] = useState("name");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
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

    fetch("http://localhost:8080/api/menu")
        .then((r) => {
          if (!r.ok) throw new Error("Failed to load menu");
          return r.json();
        })
        .then((data) => {
          setMenuItems(data);
          setLoading(false);
        })
        .catch((err) => {
          console.error(err);
          setError("Could not load menu. Please try again later.");
          setLoading(false);
        });
  }, []);

  const handleCustomizationChange = (itemId, option, isChecked) => {
    setCustomizations((prev) => {
      const existing = prev[itemId] || [];
      return {
        ...prev,
        [itemId]: isChecked
            ? [...new Set([...existing, option])]
            : existing.filter((o) => o !== option),
      };
    });
  };

  const filtered = useMemo(() => {
    return filterCat === "All"
        ? menuItems
        : menuItems.filter((m) => m.category === filterCat);
  }, [menuItems, filterCat]);

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

  const grouped = useMemo(() => {
    return sorted.reduce((g, it) => {
      (g[it.category] ||= []).push(it);
      return g;
    }, {});
  }, [sorted]);

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

  const placeOrder = async () => {
    if (!tableId || total === 0) return;

    const items = Object.entries(order).map(([id, q]) => {
      const it = menuItems.find((m) => m.id === Number(id));
      return {
        name: it.name,
        quantity: q,
        price: it.price,
        customizations: customizations[id] || [],
      };
    });

    const payload = {
      tableNumber: tableId,
      orderedItems: items,
      totalPrice: total,
      customerComments: comments,
    };

    try {
      const res = await fetch("http://localhost:8080/api/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) throw new Error("Order failed");
      const saved = await res.json();
      setMessage(`Order #${saved.id || saved._id || "(saved)"} placed!`);
      setOrder({});
      setComments("");
      setCustomizations({});
    } catch (e) {
      console.error(e);
      setMessage("Order failed – try again.");
    }
  };

  return (
      <div className="landing-container">
        <h1 className="table-heading">Table {tableId || "?"}</h1>

        {error && <p className="error-box">{error}</p>}
        {loading && <p className="loader">Loading menu...</p>}

        {!loading && !error && (
            <>
              {/* Filter + Sort */}
              <div className="sort-bar">
                <div className="sort-group">
                  <label htmlFor="cat">Category:</label>
                  <select
                      id="cat"
                      value={filterCat}
                      onChange={(e) => setFilterCat(e.target.value)}
                  >
                    {["All", ...new Set(menuItems.map((m) => m.category))].map(
                        (c) => (
                            <option key={c}>{c}</option>
                        )
                    )}
                  </select>
                </div>

                <div className="sort-group">
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
              </div>

              {/* Menu Items */}
              {Object.keys(grouped).map((cat) => (
                  <div key={cat} className="category">
                    <h3>{cat}</h3>
                    {grouped[cat].map((it) => (
                        <div key={it.id} className="menu-item">
                          <div className="item-header">
                            {it.flagEmoji && (
                                <span className="flag">{it.flagEmoji}</span>
                            )}
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

                          {it.customizationOptions?.length > 0 && (
                              <div className="customization-options">
                                <p>
                                  <strong>Customize:</strong>
                                </p>
                                {it.customizationOptions.map((option, index) => (
                                    <label key={index} style={{ marginRight: "10px" }}>
                                      <input
                                          type="checkbox"
                                          value={option}
                                          checked={
                                              customizations[it.id]?.includes(option) || false
                                          }
                                          onChange={(e) =>
                                              handleCustomizationChange(
                                                  it.id,
                                                  option,
                                                  e.target.checked
                                              )
                                          }
                                      />
                                      {option}
                                    </label>
                                ))}
                              </div>
                          )}

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
            </>
        )}
      </div>
  );
}
