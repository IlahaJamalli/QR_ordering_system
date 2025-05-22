import React, { useEffect, useState } from "react";
import axios from "axios";
import "./MenuEditor.css";

export default function MenuEditor() {
    const [menuItems, setMenuItems] = useState([]);
    const [form, setForm] = useState({
        name: "", description: "", price: "", category: "", customizationOptions: ""
    });
    const [editId, setEditId] = useState(null);

    useEffect(() => {
        loadMenu();
    }, []);

    const loadMenu = async () => {
        try {
            const res = await axios.get("http://localhost:8080/api/menu");
            setMenuItems(res.data);
        } catch (err) {
            alert("Failed to load menu items");
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = {
            ...form,
            price: parseFloat(form.price),
            customizationOptions: form.customizationOptions.split(",").map(opt => opt.trim()),
        };

        try {
            if (editId) {
                await axios.put(`http://localhost:8080/api/menu/${editId}`, payload);
            } else {
                await axios.post("http://localhost:8080/api/menu", payload);
            }
            setForm({ name: "", description: "", price: "", category: "", customizationOptions: "" });
            setEditId(null);
            loadMenu();
        } catch (err) {
            alert("Error saving menu item");
        }
    };

    const handleEdit = (item) => {
        setForm({
            name: item.name,
            description: item.description,
            price: item.price,
            category: item.category,
            customizationOptions: item.customizationOptions.join(", "),
        });
        setEditId(item.id);
        window.scrollTo(0, 0);
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this item?")) return;
        try {
            await axios.delete(`http://localhost:8080/api/menu/${id}`);
            loadMenu();
        } catch (err) {
            alert("Failed to delete item");
        }
    };

    return (
        <div className="menu-editor-container">
            <h2>{editId ? "Edit" : "Add"} Menu Item</h2>

            <form onSubmit={handleSubmit} className="menu-form">
                <input type="text" name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
                <input type="text" name="description" placeholder="Description" value={form.description} onChange={handleChange} />
                <input type="number" name="price" placeholder="Price" value={form.price} onChange={handleChange} required />
                <input type="text" name="category" placeholder="Category" value={form.category} onChange={handleChange} />
                <input type="text" name="customizationOptions" placeholder="Customizations (comma-separated)" value={form.customizationOptions} onChange={handleChange} />
                <button type="submit" className="submit-btn">{editId ? "Update" : "Add"} Item</button>
            </form>

            <div className="menu-section">
                <h3 className="menu-title">Current Menu</h3>
                <div className="menu-list">
                    {menuItems.map(item => (
                        <div key={item.id} className="menu-card">
                            <div className="menu-header">
                                <span className="menu-name">{item.name}</span>
                                <span className="menu-price">${item.price.toFixed(2)}</span>
                            </div>
                            <div className="menu-meta">{item.category}</div>
                            <div className="menu-description">{item.description}</div>
                            <div className="menu-options">
                                <strong>Options:</strong> {item.customizationOptions?.join(", ")}
                            </div>
                            <div className="menu-buttons">
                                <button className="edit-btn" onClick={() => handleEdit(item)}>Edit</button>
                                <button className="delete-btn" onClick={() => handleDelete(item.id)}>Delete</button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
