import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function MyProperties() {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) { navigate('/login'); return; }
    fetchMyProperties();
  }, []);

  const fetchMyProperties = async () => {
    try {
      const res = await axios.get('https://toletbd-30a6.onrender.com/api/properties/my', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setProperties(res.data);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('এই property delete করতে চান?')) return;
    try {
      await axios.delete(`https://toletbd-30a6.onrender.com/api/properties/delete/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchMyProperties();
    } catch (err) {
      alert(err.response?.data?.message || 'Error হয়েছে');
    }
  };

  const handleRented = async (id) => {
    if (!window.confirm('এই বাসা ভাড়া হয়ে গেছে mark করতে চান?')) return;
    try {
      await axios.put(`https://toletbd-30a6.onrender.com/api/properties/rented/${id}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchMyProperties();
    } catch (err) {
      alert(err.response?.data?.message || 'Error হয়েছে');
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>🏠 আমার বিজ্ঞাপনগুলো</h2>

      {loading ? (
        <p style={styles.loading}>লোড হচ্ছে...</p>
      ) : properties.length === 0 ? (
        <div style={styles.empty}>
          <p>কোনো বিজ্ঞাপন নেই</p>
          <Link to="/post-property" style={styles.addBtn}>+ নতুন বিজ্ঞাপন দিন</Link>
        </div>
      ) : (
        <div>
          {properties.map(p => (
            <div key={p._id} style={styles.card}>
              <div style={styles.cardLeft}>
                {p.images && p.images.length > 0 ? (
                  <img src={p.images[0]} alt={p.title} style={styles.img} />
                ) : (
                  <div style={styles.noImg}>📷</div>
                )}
              </div>
              <div style={styles.cardRight}>
                <h3 style={styles.propTitle}>{p.title}</h3>
                <p style={styles.propInfo}>📍 {p.location} — ৳{p.rent}/মাস</p>
                <span style={{
                  ...styles.badge,
                  backgroundColor: p.status === 'approved' ? '#d4edda' :
                    p.status === 'rejected' ? '#f8d7da' :
                    p.status === 'rented' ? '#d1ecf1' : '#fff3cd',
                  color: p.status === 'approved' ? '#155724' :
                    p.status === 'rejected' ? '#721c24' :
                    p.status === 'rented' ? '#0c5460' : '#856404',
                }}>
                  {p.status === 'approved' ? '✅ Approved' :
                    p.status === 'rejected' ? '❌ Rejected' :
                    p.status === 'rented' ? '🔑 ভাড়া হয়ে গেছে' : '⏳ Pending'}
                </span>
                <div style={styles.btnRow}>
                  {p.status !== 'rented' && (
                    <>
                      <Link to={`/edit-property/${p._id}`} style={styles.editBtn}>✏️ Edit</Link>
                      <button style={styles.rentedBtn} onClick={() => handleRented(p._id)}>🔑 ভাড়া হয়ে গেছে</button>
                    </>
                  )}
                  <button style={styles.deleteBtn} onClick={() => handleDelete(p._id)}>🗑️ Delete</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  container: { maxWidth: '900px', margin: '0 auto', padding: '32px 24px' },
  title: { fontSize: '24px', color: '#333', marginBottom: '24px' },
  card: { backgroundColor: 'white', borderRadius: '10px', padding: '16px', marginBottom: '16px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)', display: 'flex', gap: '16px' },
  cardLeft: { flexShrink: 0 },
  img: { width: '120px', height: '90px', objectFit: 'cover', borderRadius: '8px' },
  noImg: { width: '120px', height: '90px', backgroundColor: '#f0f0f0', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px' },
  cardRight: { flex: 1 },
  propTitle: { margin: '0 0 6px', fontSize: '17px', color: '#333' },
  propInfo: { margin: '0 0 8px', color: '#666', fontSize: '14px' },
  badge: { padding: '3px 10px', borderRadius: '20px', fontSize: '13px', fontWeight: 'bold' },
  btnRow: { display: 'flex', gap: '10px', marginTop: '12px', flexWrap: 'wrap' },
  editBtn: { padding: '6px 16px', backgroundColor: '#2E86AB', color: 'white', borderRadius: '6px', textDecoration: 'none', fontSize: '14px' },
  rentedBtn: { padding: '6px 16px', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '14px' },
  deleteBtn: { padding: '6px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '14px' },
  loading: { textAlign: 'center', color: '#666', padding: '48px' },
  empty: { textAlign: 'center', padding: '48px' },
  addBtn: { display: 'inline-block', marginTop: '12px', backgroundColor: '#2E86AB', color: 'white', padding: '10px 24px', borderRadius: '8px', textDecoration: 'none' },
};

export default MyProperties;