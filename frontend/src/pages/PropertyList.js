import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import PropertyCard from '../components/PropertyCard';

function PropertyList() {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [rentMin, setRentMin] = useState('');
  const [rentMax, setRentMax] = useState('');
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

  const filteredProperties = properties.filter(p => {
    const matchSearch = search === '' || p.title.toLowerCase().includes(search.toLowerCase());
    const matchMin = rentMin === '' || p.rent >= Number(rentMin);
    const matchMax = rentMax === '' || p.rent <= Number(rentMax);
    return matchSearch && matchMin && matchMax;
  });

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>সব To-Let বিজ্ঞাপন</h2>

      <div style={styles.filterBox}>
        <input
          style={styles.searchInput}
          placeholder="🔍 শিরোনাম দিয়ে খুঁজুন..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <input
          style={styles.rentInput}
          type="number"
          placeholder="সর্বনিম্ন ভাড়া (৳)"
          value={rentMin}
          onChange={e => setRentMin(e.target.value)}
        />
        <input
          style={styles.rentInput}
          type="number"
          placeholder="সর্বোচ্চ ভাড়া (৳)"
          value={rentMax}
          onChange={e => setRentMax(e.target.value)}
        />
      </div>

      {loading ? (
        <p style={styles.loading}>লোড হচ্ছে...</p>
      ) : filteredProperties.length === 0 ? (
        <p style={styles.empty}>কোনো বিজ্ঞাপন পাওয়া যায়নি</p>
      ) : (
        <div style={styles.grid}>
          {filteredProperties.map(property => (
            <PropertyCard key={property._id} property={property} />
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  container: { maxWidth: '1200px', margin: '0 auto', padding: '32px 24px' },
  title: { fontSize: '24px', color: '#333', marginBottom: '16px' },
  filterBox: { display: 'flex', gap: '12px', marginBottom: '24px', flexWrap: 'wrap' },
  searchInput: { flex: 2, padding: '10px 14px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '15px', minWidth: '200px' },
  rentInput: { flex: 1, padding: '10px 14px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '15px', minWidth: '150px' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' },
  loading: { textAlign: 'center', color: '#666', fontSize: '18px', padding: '48px' },
  empty: { textAlign: 'center', color: '#999', fontSize: '18px', padding: '48px' },
};

export default PropertyList;