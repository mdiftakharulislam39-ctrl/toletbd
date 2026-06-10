import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

function PropertyDetail() {
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);
  const { id } = useParams();

  useEffect(() => {
    fetchProperty();
  }, [id]);

  const fetchProperty = async () => {
    try {
      const res = await axios.get(`https://toletbd-30a6.onrender.com/api/properties/${id}`);
      setProperty(res.data);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <p style={styles.loading}>লোড হচ্ছে...</p>;
  if (!property) return <p style={styles.loading}>পাওয়া যায়নি</p>;

  const whatsappUrl = "https://wa.me/88" + (property.owner_id ? property.owner_id.phone : '');

  return (
    <div style={styles.container}>
      <div style={styles.imgBox}>
        {property.images && property.images.length > 0 ? (
          <img src={property.images[0]} alt={property.title} style={styles.img} />
        ) : (
          <div style={styles.noImg}>📷 কোনো ছবি নেই</div>
        )}
      </div>

      <div style={styles.content}>
        <h1 style={styles.title}>{property.title}</h1>
        <p style={styles.location}>📍 {property.address}</p>

        <div style={styles.infoGrid}>
          <div style={styles.infoCard}>
            <p style={styles.infoLabel}>মাসিক ভাড়া</p>
            <p style={styles.infoValue}>৳{property.rent}</p>
          </div>
          <div style={styles.infoCard}>
            <p style={styles.infoLabel}>অগ্রিম</p>
            <p style={styles.infoValue}>৳{property.advance || 'নেই'}</p>
          </div>
          <div style={styles.infoCard}>
            <p style={styles.infoLabel}>ধরন</p>
            <p style={styles.infoValue}>{property.property_type}</p>
          </div>
          <div style={styles.infoCard}>
            <p style={styles.infoLabel}>Tenant</p>
            <p style={styles.infoValue}>{property.tenant_type}</p>
          </div>
          {property.bedrooms && (
            <div style={styles.infoCard}>
              <p style={styles.infoLabel}>Bedroom</p>
              <p style={styles.infoValue}>{property.bedrooms}টি</p>
            </div>
          )}
          {property.bathrooms && (
            <div style={styles.infoCard}>
              <p style={styles.infoLabel}>Bathroom</p>
              <p style={styles.infoValue}>{property.bathrooms}টি</p>
            </div>
          )}
        </div>

        {property.description && (
          <div style={styles.desc}>
            <h3>বিবরণ</h3>
            <p>{property.description}</p>
          </div>
        )}

        {property.facilities && property.facilities.length > 0 && (
          <div style={styles.facilitiesBox}>
            <h3>Facilities</h3>
            <div style={styles.facilitiesGrid}>
              {property.facilities.map(f => (
                <span key={f} style={styles.facilityTag}>
                  {f === 'AC' ? '❄️' : f === 'Lift' ? '🛗' : f === 'Parking' ? '🚗' :
                   f === 'Gas' ? '🔥' : f === 'WiFi' ? '📶' : f === 'CCTV' ? '📹' :
                   f === 'Generator' ? '⚡' : '💧'} {f}
                </span>
              ))}
            </div>
          </div>
        )}

        {property.owner_id && (
          <div style={styles.ownerBox}>
            <h3>Owner এর তথ্য</h3>
            <p>👤 নাম: {property.owner_id.name}</p>
            <p>📞 ফোন: {property.owner_id.phone}</p>
            <div style={styles.contactBtns}>
              <a href={"tel:" + property.owner_id.phone} style={styles.callBtn}>
                📞 এখনই Call করুন
              </a>
              <a href={whatsappUrl} target="_blank" rel="noopener noreferrer" style={styles.whatsappBtn}>
                💬 WhatsApp করুন
              </a>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  container: { maxWidth: '900px', margin: '32px auto', padding: '0 24px' },
  imgBox: { width: '100%', height: '400px', backgroundColor: '#f0f0f0', borderRadius: '12px', overflow: 'hidden', marginBottom: '24px' },
  img: { width: '100%', height: '100%', objectFit: 'cover' },
  noImg: { height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#999', fontSize: '20px' },
  content: { backgroundColor: 'white', borderRadius: '12px', padding: '32px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
  title: { fontSize: '26px', color: '#333', marginBottom: '8px' },
  location: { color: '#666', marginBottom: '24px', fontSize: '16px' },
  infoGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))', gap: '16px', marginBottom: '24px' },
  infoCard: { backgroundColor: '#f8f9fa', borderRadius: '8px', padding: '16px', textAlign: 'center' },
  infoLabel: { color: '#666', fontSize: '13px', margin: '0 0 4px' },
  infoValue: { color: '#2E86AB', fontWeight: 'bold', fontSize: '18px', margin: 0 },
  desc: { borderTop: '1px solid #eee', paddingTop: '20px', marginBottom: '24px' },
  facilitiesBox: { borderTop: '1px solid #eee', paddingTop: '20px', marginBottom: '24px' },
  facilitiesGrid: { display: 'flex', flexWrap: 'wrap', gap: '10px', marginTop: '12px' },
  facilityTag: { backgroundColor: '#e8f4f8', color: '#2E86AB', padding: '6px 14px', borderRadius: '20px', fontSize: '14px' },
  ownerBox: { backgroundColor: '#e8f4f8', borderRadius: '10px', padding: '20px' },
  contactBtns: { display: 'flex', gap: '10px', marginTop: '12px', flexWrap: 'wrap' },
  callBtn: { display: 'inline-block', backgroundColor: '#2E86AB', color: 'white', padding: '10px 24px', borderRadius: '8px', textDecoration: 'none', fontWeight: 'bold' },
  whatsappBtn: { display: 'inline-block', backgroundColor: '#25D366', color: 'white', padding: '10px 24px', borderRadius: '8px', textDecoration: 'none', fontWeight: 'bold' },
  loading: { textAlign: 'center', padding: '48px', color: '#666' },
};

export default PropertyDetail;