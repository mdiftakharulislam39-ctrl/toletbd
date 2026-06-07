import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function PostProperty() {
  const [form, setForm] = useState({
    title: '', description: '', location: '', address: '',
    rent: '', advance: '', property_type: 'flat',
    bedrooms: '', bathrooms: '', tenant_type: 'any'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }
    try {
      await axios.post('http://localhost:5000/api/properties/add', form, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setSuccess('Property add হয়েছে! Admin approve করলে দেখা যাবে।');
      setTimeout(() => navigate('/properties'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Error হয়েছে');
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>🏠 বাসা ভাড়ার বিজ্ঞাপন দিন</h2>
        {error && <p style={styles.error}>{error}</p>}
        {success && <p style={styles.success}>{success}</p>}

        <input style={styles.input} name="title" placeholder="বিজ্ঞাপনের শিরোনাম" value={form.title} onChange={handleChange} />
        
        <select style={styles.input} name="location" value={form.location} onChange={handleChange}>
          <option value="">এলাকা বেছে নিন</option>
          <option value="Mirpur">Mirpur</option>
          <option value="Uttara">Uttara</option>
          <option value="Dhanmondi">Dhanmondi</option>
          <option value="Bashundhara">Bashundhara</option>
          <option value="Banani">Banani</option>
          <option value="Mohammadpur">Mohammadpur</option>
          <option value="Gulshan">Gulshan</option>
          <option value="Khilgaon">Khilgaon</option>
        </select>

        <input style={styles.input} name="address" placeholder="সম্পূর্ণ ঠিকানা" value={form.address} onChange={handleChange} />
        
        <div style={styles.row}>
          <input style={styles.inputHalf} name="rent" type="number" placeholder="মাসিক ভাড়া (৳)" value={form.rent} onChange={handleChange} />
          <input style={styles.inputHalf} name="advance" type="number" placeholder="অগ্রিম (৳)" value={form.advance} onChange={handleChange} />
        </div>

        <div style={styles.row}>
          <select style={styles.inputHalf} name="property_type" value={form.property_type} onChange={handleChange}>
            <option value="flat">Flat</option>
            <option value="room">Room</option>
            <option value="seat">Seat</option>
            <option value="hostel">Hostel</option>
          </select>
          <select style={styles.inputHalf} name="tenant_type" value={form.tenant_type} onChange={handleChange}>
            <option value="any">যেকোনো</option>
            <option value="family">Family</option>
            <option value="bachelor">Bachelor</option>
          </select>
        </div>

        <div style={styles.row}>
          <input style={styles.inputHalf} name="bedrooms" type="number" placeholder="Bedroom সংখ্যা" value={form.bedrooms} onChange={handleChange} />
          <input style={styles.inputHalf} name="bathrooms" type="number" placeholder="Bathroom সংখ্যা" value={form.bathrooms} onChange={handleChange} />
        </div>

        <textarea style={styles.textarea} name="description" placeholder="বিস্তারিত বিবরণ লিখুন..." value={form.description} onChange={handleChange} />

        <button style={styles.btn} onClick={handleSubmit}>
          বিজ্ঞাপন দিন
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '80vh',
    backgroundColor: '#f8f9fa',
    padding: '32px 24px',
  },
  card: {
    backgroundColor: 'white',
    padding: '40px',
    borderRadius: '12px',
    boxShadow: '0 4px 16px rgba(0,0,0,0.1)',
    maxWidth: '600px',
    margin: '0 auto',
  },
  title: {
    textAlign: 'center',
    marginBottom: '24px',
    color: '#2E86AB',
  },
  input: {
    width: '100%',
    padding: '12px',
    marginBottom: '16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '15px',
    boxSizing: 'border-box',
  },
  row: {
    display: 'flex',
    gap: '12px',
    marginBottom: '0px',
  },
  inputHalf: {
    width: '50%',
    padding: '12px',
    marginBottom: '16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '15px',
    boxSizing: 'border-box',
  },
  textarea: {
    width: '100%',
    padding: '12px',
    marginBottom: '16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '15px',
    boxSizing: 'border-box',
    height: '120px',
    resize: 'vertical',
  },
  btn: {
    width: '100%',
    padding: '12px',
    backgroundColor: '#2E86AB',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
    cursor: 'pointer',
    fontWeight: 'bold',
  },
  error: {
    color: 'red',
    textAlign: 'center',
    marginBottom: '12px',
  },
  success: {
    color: 'green',
    textAlign: 'center',
    marginBottom: '12px',
  },
};

export default PostProperty;