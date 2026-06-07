import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Home() {
  const [location, setLocation] = useState('');
  const [propertyType, setPropertyType] = useState('');
  const [tenantType, setTenantType] = useState('');
  const navigate = useNavigate();

  const handleSearch = () => {
    const params = new URLSearchParams();
    if (location) params.append('location', location);
    if (propertyType) params.append('property_type', propertyType);
    if (tenantType) params.append('tenant_type', tenantType);
    navigate(`/properties?${params.toString()}`);
  };

  return (
    <div>
      <div style={styles.hero}>
        <h1 style={styles.heroTitle}>আপনার স্বপ্নের বাসা খুঁজুন</h1>
        <p style={styles.heroSub}>সারা বাংলাদেশে হাজারো To-Let বিজ্ঞাপন</p>

        <div style={styles.searchBox}>
          <select style={styles.select} value={location} onChange={e => setLocation(e.target.value)}>
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

          <select style={styles.select} value={propertyType} onChange={e => setPropertyType(e.target.value)}>
            <option value="">Property Type</option>
            <option value="flat">Flat</option>
            <option value="room">Room</option>
            <option value="seat">Seat</option>
            <option value="hostel">Hostel</option>
          </select>

          <select style={styles.select} value={tenantType} onChange={e => setTenantType(e.target.value)}>
            <option value="">Tenant Type</option>
            <option value="family">Family</option>
            <option value="bachelor">Bachelor</option>
            <option value="any">Any</option>
          </select>

          <button style={styles.searchBtn} onClick={handleSearch}>
            🔍 খুঁজুন
          </button>
        </div>
      </div>

      <div style={styles.features}>
        <div style={styles.featureCard}>
          <div style={styles.featureIcon}>🏠</div>
          <h3>হাজারো বাসা</h3>
          <p>সারা ঢাকায় verified to-let listings</p>
        </div>
        <div style={styles.featureCard}>
          <div style={styles.featureIcon}>✅</div>
          <h3>Verified Posts</h3>
          <p>Admin approved সব বিজ্ঞাপন</p>
        </div>
        <div style={styles.featureCard}>
          <div style={styles.featureIcon}>📞</div>
          <h3>সরাসরি যোগাযোগ</h3>
          <p>Owner-এর সাথে সরাসরি কথা বলুন</p>
        </div>
      </div>
    </div>
  );
}

const styles = {
  hero: {
    backgroundColor: '#2E86AB',
    color: 'white',
    padding: '60px 24px',
    textAlign: 'center',
  },
  heroTitle: {
    fontSize: '36px',
    margin: '0 0 12px',
  },
  heroSub: {
    fontSize: '18px',
    margin: '0 0 32px',
    opacity: 0.9,
  },
  searchBox: {
    display: 'flex',
    gap: '12px',
    justifyContent: 'center',
    flexWrap: 'wrap',
    maxWidth: '800px',
    margin: '0 auto',
  },
  select: {
    padding: '12px 16px',
    borderRadius: '8px',
    border: 'none',
    fontSize: '15px',
    minWidth: '160px',
    cursor: 'pointer',
  },
  searchBtn: {
    padding: '12px 28px',
    backgroundColor: '#F26419',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
    cursor: 'pointer',
    fontWeight: 'bold',
  },
  features: {
    display: 'flex',
    gap: '24px',
    padding: '48px 24px',
    justifyContent: 'center',
    flexWrap: 'wrap',
    backgroundColor: '#f8f9fa',
  },
  featureCard: {
    backgroundColor: 'white',
    borderRadius: '10px',
    padding: '32px 24px',
    textAlign: 'center',
    width: '220px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
  },
  featureIcon: {
    fontSize: '40px',
    marginBottom: '12px',
  },
};

export default Home;