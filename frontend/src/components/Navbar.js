import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  const logout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <nav style={styles.nav}>
      <Link to="/" style={styles.logo}>🏠 ToLetBD</Link>
      <div style={styles.links}>
        <Link to="/properties" style={styles.link}>বাসা খুঁজুন</Link>
        {token ? (
          <>
            <Link to="/post-property" style={styles.link}>বাসা দিন</Link>
            <button onClick={logout} style={styles.btn}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login" style={styles.link}>Login</Link>
            <Link to="/register" style={styles.linkBtn}>Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '12px 24px',
    backgroundColor: '#2E86AB',
    color: 'white',
  },
  logo: {
    color: 'white',
    textDecoration: 'none',
    fontSize: '22px',
    fontWeight: 'bold',
  },
  links: {
    display: 'flex',
    gap: '16px',
    alignItems: 'center',
  },
  link: {
    color: 'white',
    textDecoration: 'none',
    fontSize: '15px',
  },
  linkBtn: {
    color: 'white',
    textDecoration: 'none',
    backgroundColor: '#F26419',
    padding: '6px 14px',
    borderRadius: '6px',
    fontSize: '15px',
  },
  btn: {
    backgroundColor: 'transparent',
    border: '1px solid white',
    color: 'white',
    padding: '6px 14px',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '15px',
  }
};

export default Navbar;