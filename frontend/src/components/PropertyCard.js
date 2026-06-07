import React from 'react';
import { Link } from 'react-router-dom';

function PropertyCard({ property }) {
  return (
    <div style={styles.card}>
      <div style={styles.imgBox}>
        {property.images && property.images.length > 0 ? (
          <img src={property.images[0]} alt={property.title} style={styles.img} />
        ) : (
          <div style={styles.noImg}>📷 কোনো ছবি নেই</div>
        )}
      </div>
      <div style={styles.info}>
        <h3 style={styles.title}>{property.title}</h3>
        <p style={styles.location}>📍 {property.location}</p>
        <p style={styles.rent}>💰 ৳{property.rent}/মাস</p>
        <div style={styles.tags}>
          <span style={styles.tag}>{property.property_type}</span>
          <span style={styles.tag}>{property.tenant_type}</span>
          {property.bedrooms && <span style={styles.tag}>{property.bedrooms} bed</span>}
        </div>
        <Link to={`/properties/${property._id}`} style={styles.btn}>
          বিস্তারিত দেখুন
        </Link>
      </div>
    </div>
  );
}

const styles = {
  card: {
    backgroundColor: 'white',
    borderRadius: '10px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    overflow: 'hidden',
    transition: 'transform 0.2s',
  },
  imgBox: {
    height: '180px',
    backgroundColor: '#f0f0f0',
    overflow: 'hidden',
  },
  img: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
  noImg: {
    height: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: '#999',
    fontSize: '16px',
  },
  info: {
    padding: '16px',
  },
  title: {
    margin: '0 0 8px',
    fontSize: '16px',
    color: '#333',
  },
  location: {
    margin: '0 0 4px',
    color: '#666',
    fontSize: '14px',
  },
  rent: {
    margin: '0 0 10px',
    color: '#2E86AB',
    fontWeight: 'bold',
    fontSize: '18px',
  },
  tags: {
    display: 'flex',
    gap: '8px',
    flexWrap: 'wrap',
    marginBottom: '12px',
  },
  tag: {
    backgroundColor: '#e8f4f8',
    color: '#2E86AB',
    padding: '3px 10px',
    borderRadius: '20px',
    fontSize: '12px',
  },
  btn: {
    display: 'block',
    textAlign: 'center',
    backgroundColor: '#2E86AB',
    color: 'white',
    padding: '8px',
    borderRadius: '6px',
    textDecoration: 'none',
    fontSize: '14px',
  }
};

export default PropertyCard;