import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function AdminPanel() {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const token = localStorage.getItem('token');
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  useEffect(() => {
    if (!token || user.role !== 'admin') {
      navigate('/login');
      return;
    }
    fetchProperties();
  }, []);

  const fetchProperties = async () => {
    try {
      const res = await axios.get('http://localhost:5000/api/admin/properties', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setProperties(res.data);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id, status) => {
    try {
      await axios.put(`http://localhost:5000/api/admin/properties/${id}`,
        { status },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      fetchProperties();
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>⚙️ Admin Panel</h2>

      {loading ? (
        <p style={styles.loading}>লোড হচ্ছে...</p>
      ) : (
        <div>
          <h3 style={styles.subtitle}>সব Property ({properties.length}টি)</h3>
          {properties.map(p => (
            <div key={p._id} style={styles.card}>
              <div style={styles.cardTop}>
                <div>
                  <h4 style={styles.propTitle}>{p.title}</h4>
                  <p style={styles.propInfo}>📍 {p.location} — ৳{p.rent}/মাস</p>
                  <p style={styles.propInfo}>👤 {p.owner_id?.name} | 📞 {p.owner_id?.phone}</p>
                </div>
                <span style={{
                  ...styles.badge,
                  backgroundColor: p.status === 'approved' ? '#d4edda' :
                    p.status === 'rejected' ? '#f8d7da' : '#fff3cd',
                  color: p.status === 'approved' ? '#155724' :
                    p.status === 'rejected' ? '#721c24' : '#856404',
                }}>
                  {p.status}
                </span>
              </div>
              <div style={styles.btnRow}>
                <button
                  style={styles.approveBtn}
                  onClick={() => updateStatus(p._id, 'approved')}
                >
                  ✅ Approve
                </button>
                <button
                  style={styles.rejectBtn}
                  onClick={() => updateStatus(p._id, 'rejected')}
                >
                  ❌ Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '900px',
    margin: '0 auto',
    padding: '32px 24px',
  },
  title: {
    fontSize: '28px',
    color: '#333',
    marginBottom: '24px',
  },
  subtitle: {
    fontSize: '18px',
    color: '#666',
    marginBottom: '16px',
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '10px',
    padding: '20px',
    marginBottom: '16px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
  },
  cardTop: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '12px',
  },
  propTitle: {
    margin: '0 0 6px',
    fontSize: '17px',
    color: '#333',
  },
  propInfo: {
    margin: '0 0 4px',
    color: '#666',
    fontSize: '14px',
  },
  badge: {
    padding: '4px 12px',
    borderRadius: '20px',
    fontSize: '13px',
    fontWeight: 'bold',
  },
  btnRow: {
    display: 'flex',
    gap: '12px',
  },
  approveBtn: {
    padding: '8px 20px',
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  rejectBtn: {
    padding: '8px 20px',
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  loading: {
    textAlign: 'center',
    color: '#666',
    padding: '48px',
  },
};

export default AdminPanel;