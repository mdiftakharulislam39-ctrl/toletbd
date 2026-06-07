import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function Register() {
  const [form, setForm] = useState({
    name: '', phone: '', email: '', password: '', role: 'user'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    try {
      await axios.post('http://localhost:5000/api/auth/register', form);
      setSuccess('Registration সফল! Login করুন।');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>📝 Register</h2>
        {error && <p style={styles.error}>{error}</p>}
        {success && <p style={styles.success}>{success}</p>}

        <input
          style={styles.input}
          type="text"
          name="name"
          placeholder="আপনার নাম"
          value={form.name}
          onChange={handleChange}
        />
        <input
          style={styles.input}
          type="text"
          name="phone"
          placeholder="ফোন নম্বর"
          value={form.phone}
          onChange={handleChange}
        />
        <input
          style={styles.input}
          type="email"
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
        />
        <input
          style={styles.input}
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
        />
        <select
          style={styles.input}
          name="role"
          value={form.role}
          onChange={handleChange}
        >
          <option value="user">আমি বাসা খুঁজছি</option>
          <option value="owner">আমি বাসা দিতে চাই</option>
        </select>

        <button style={styles.btn} onClick={handleSubmit}>
          Register
        </button>
        <p style={styles.login}>
          আগে থেকে account আছে? <Link to="/login">Login করুন</Link>
        </p>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '80vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f8f9fa',
  },
  card: {
    backgroundColor: 'white',
    padding: '40px',
    borderRadius: '12px',
    boxShadow: '0 4px 16px rgba(0,0,0,0.1)',
    width: '100%',
    maxWidth: '400px',
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
  login: {
    textAlign: 'center',
    marginTop: '16px',
    color: '#666',
  }
};

export default Register;


