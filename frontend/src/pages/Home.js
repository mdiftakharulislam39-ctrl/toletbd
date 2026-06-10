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
        <p style={styles.heroSub}>ঢাকার সেরা To-Let বিজ্ঞাপন</p>

        <div style={styles.searchBox}>
          <select style={styles.select} value={location} onChange={e => setLocation(e.target.value)}>
            <option value="">এলাকা বেছে নিন</option>
            <optgroup label="মিরপুর">
              <option value="Mirpur 1">Mirpur 1</option>
              <option value="Mirpur 2">Mirpur 2</option>
              <option value="Mirpur 6">Mirpur 6</option>
              <option value="Mirpur 7">Mirpur 7</option>
              <option value="Mirpur 10">Mirpur 10</option>
              <option value="Mirpur 11">Mirpur 11</option>
              <option value="Mirpur 12">Mirpur 12</option>
              <option value="Mirpur 13">Mirpur 13</option>
              <option value="Mirpur 14">Mirpur 14</option>
              <option value="Shewrapara">Shewrapara</option>
              <option value="Kazipara">Kazipara</option>
              <option value="Pallabi">Pallabi</option>
            </optgroup>
            <optgroup label="উত্তরা">
              <option value="Uttara Sector 1">Uttara Sector 1</option>
              <option value="Uttara Sector 3">Uttara Sector 3</option>
              <option value="Uttara Sector 4">Uttara Sector 4</option>
              <option value="Uttara Sector 6">Uttara Sector 6</option>
              <option value="Uttara Sector 7">Uttara Sector 7</option>
              <option value="Uttara Sector 10">Uttara Sector 10</option>
              <option value="Uttara Sector 11">Uttara Sector 11</option>
              <option value="Uttara Sector 12">Uttara Sector 12</option>
              <option value="Abdullahpur">Abdullahpur</option>
              <option value="Azampur">Azampur</option>
            </optgroup>
            <optgroup label="ধানমন্ডি">
              <option value="Dhanmondi">Dhanmondi</option>
              <option value="Dhanmondi 15">Dhanmondi 15</option>
              <option value="Dhanmondi 32">Dhanmondi 32</option>
              <option value="Kalabagan">Kalabagan</option>
              <option value="Shyamoli">Shyamoli</option>
              <option value="Adabor">Adabor</option>
            </optgroup>
            <optgroup label="গুলশান/বনানী">
              <option value="Gulshan 1">Gulshan 1</option>
              <option value="Gulshan 2">Gulshan 2</option>
              <option value="Banani">Banani</option>
              <option value="Niketon">Niketon</option>
              <option value="Baridhara">Baridhara</option>
              <option value="Bashundhara R/A">Bashundhara R/A</option>
            </optgroup>
            <optgroup label="রামপুরা/বাড্ডা">
              <option value="Rampura">Rampura</option>
              <option value="Banasree">Banasree</option>
              <option value="Badda">Badda</option>
              <option value="Khilgaon">Khilgaon</option>
              <option value="Malibag">Malibag</option>
              <option value="Mugda">Mugda</option>
              <option value="Goran">Goran</option>
            </optgroup>
            <optgroup label="মোহাম্মদপুর">
              <option value="Mohammadpur">Mohammadpur</option>
              <option value="Bosila">Bosila</option>
              <option value="Rayerbazar">Rayerbazar</option>
            </optgroup>
            <optgroup label="যাত্রাবাড়ী/ডেমরা">
              <option value="Jatrabari">Jatrabari</option>
              <option value="Demra">Demra</option>
              <option value="Postogola">Postogola</option>
              <option value="Shyampur">Shyampur</option>
            </optgroup>
            <optgroup label="অন্যান্য">
              <option value="Farmgate">Farmgate</option>
              <option value="Tejgaon">Tejgaon</option>
              <option value="Moghbazar">Moghbazar</option>
              <option value="Eskaton">Eskaton</option>
              <option value="Panthapath">Panthapath</option>
              <option value="Kawran Bazar">Kawran Bazar</option>
              <option value="Agargaon">Agargaon</option>
              <option value="Sher-E-Bangla Nagar">Sher-E-Bangla Nagar</option>
              <option value="Gazipur">Gazipur</option>
              <option value="Savar">Savar</option>
              <option value="Keraniganj">Keraniganj</option>
              <option value="Narayanganj">Narayanganj</option>
            </optgroup>
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