import React, { useEffect, useState } from "react";
import axios from "axios";
import "./TrackOrder.css";   // ← make sure this path is correct

export default function TrackOrder() {
  /* — table ID from URL — */
  const tableNumber =
    new URLSearchParams(window.location.search).get("tableId") || "A1";

  /* — state — */
  const [order,   setOrder]   = useState(null);
  const [loading, setLoading] = useState(true);
  const [text,    setText]    = useState("");

  /* — fetch latest order for this table — */
  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/orders?tableNumber=${tableNumber}`)
      .then((r) => {
        if (r.data.length) {
          const latest = r.data.reduce((a, b) =>
            new Date(b.orderedTime) > new Date(a.orderedTime) ? b : a
          );
          setOrder(latest);
        }
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [tableNumber]);

  /* — helper: format dd MMM, hh:mm AM/PM — */
  const fmt = (d) =>
    new Intl.DateTimeFormat(undefined, {
      day: "2-digit",
      month: "short",
      hour: "2-digit",
      minute: "2-digit",
    }).format(new Date(d));

  /* — status → colour — */
  const hue = {
    Pending: "#f97316",
    "In Preparation": "#3b82f6",
    "Almost Ready": "#a855f7",
    Ready: "#22c55e",
    Delivered: "#6b7280",
  };

  /* — send chat message — */
  const send = () => {
    if (!text.trim()) return;
    axios
      .put(`http://localhost:8080/api/orders/${order.id}/comment`, {
        sender: "customer",
        comment: text.trim(),
      })
      .then(() => {
        setOrder((o) => ({
          ...o,
          commentsHistory: [
            ...(o.commentsHistory || []),
            { sender: "customer", message: text.trim(), timestamp: new Date() },
          ],
        }));
        setText("");
      })
      .catch(console.error);
  };

  /* — UI — */
  return (
    <div className="track-bg">
      <div className="track-wrap">
        <h2 className="pageTitle">Track Your Order</h2>

        {loading && <div className="loader" />}

        {!loading && !order && <p className="dim">No orders found.</p>}

        {order && (
          <div className="card">
            {/* header */}
            <div className="row between">
              <h3>Table&nbsp;{order.tableNumber}</h3>
              <span
                className="badge"
                style={{ background: hue[order.status] || "#94a3b8" }}
              >
                {order.status}
              </span>
            </div>

            <p className="dim time">Ordered&nbsp;{fmt(order.orderedTime)}</p>

            {/* items */}
            <h4 className="secHead">Items</h4>
            <ul className="items">
              {order.orderedItems.map((it, i) => (
                <li key={i}>
                  {it.name} × {it.quantity}
                  <span>${(it.price * it.quantity).toFixed(2)}</span>
                </li>
              ))}
            </ul>

            <div className="row between totalRow">
              <h4>Total</h4>
              <h4>${order.totalPrice.toFixed(2)}</h4>
            </div>

            {/* chat */}
            <h4 className="secHead" style={{ marginTop: "22px" }}>
              Chat with Kitchen
            </h4>

            <div className="chatBox">
              {(order.commentsHistory || []).map((c, i) => (
                <div
                  key={i}
                  className={`bubble ${c.sender === "customer" ? "me" : "them"}`}
                >
                  <b className="sender">
                    {c.sender === "customer" ? "Customer" : "Kitchen"}:
                  </b>{" "}
                  {c.message}
                  <small className="ts">{fmt(c.timestamp)}</small>
                </div>
              ))}
              {!order.commentsHistory?.length && (
                <p className="dim">No comments yet.</p>
              )}
            </div>

            {/* send bar */}
            <div className="sendBar">
              <textarea
                rows="1"
                value={text}
                onChange={(e) => setText(e.target.value)}
                placeholder="Type your message…"
              />
              <button onClick={send} disabled={!text.trim()}>
                Send
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
