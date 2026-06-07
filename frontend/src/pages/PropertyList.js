import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import PropertyCard from '../components/PropertyCard';

function PropertyList() {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    fetchProperties();
  }, [location.search]);

  const fetchProperties = async () => {
    try {
      const res = await axios.get(`http://localhost:5000/api/properties/all${location.search}`);
      setProperties(res.data);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>সব To-Let বিজ্ঞাপন</h2>

      {loading ? (
        <p style={styles.loading}>লোড হচ্ছে...</p>
      ) : properties.length === 0 ? (
        <p style={styles.empty}>কোনো বিজ্ঞাপন পাওয়া যায়নি</p>
      ) : (
        <div style={styles.grid}>
          {properties.map(property => (
            <PropertyCard key={property._id} property={property} />
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '32px 24px',
  },
  title: {
    fontSize: '24px',
    color: '#333',
    marginBottom: '24px',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
    gap: '24px',
  },
  loading: {
    textAlign: 'center',
    color: '#666',
    fontSize: '18px',
    padding: '48px',
  },
  empty: {
    textAlign: 'center',
    color: '#999',
    fontSize: '18px',
    padding: '48px',
  }
};

export default PropertyList;

