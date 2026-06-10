import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

function EditProperty() {
  const { id } = useParams();
  const [form, setForm] = useState({
    title: '', description: '', location: '', address: '',
    rent: '', advance: '', property_type: 'flat',
    bedrooms: '', bathrooms: '', tenant_type: 'any'
  });
  const [images, setImages] = useState([]);
  const [previews, setPreviews] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProperty();
  }, [id]);

  const fetchProperty = async () => {
    try {
      const res = await axios.get(`http://localhost:5000/api/properties/${id}`);
      const p = res.data;
      setForm({
        title: p.title || '',
        description: p.description || '',
        location: p.location || '',
        address: p.address || '',
        rent: p.rent || '',
        advance: p.advance || '',
        property_type: p.property_type || 'flat',
        bedrooms: p.bedrooms || '',
        bathrooms: p.bathrooms || '',
        tenant_type: p.tenant_type || 'any'
      });
      if (p.images) setPreviews(p.images);
    } catch (err) {
      console.log(err);
    }
  };

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleImages = e => {
    const files = Array.from(e.target.files);
    setImages(files);
    const previewUrls = files.map(f => URL.createObjectURL(f));
    setPreviews(previewUrls);
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem('token');
    if (!token) { navigate('/login'); return; }
    setLoading(true);
    try {
      const formData = new FormData();
      Object.keys(form).forEach(key => formData.append(key, form[key]));
      images.forEach(img => formData.append('images', img));

      await axios.put(`http://localhost:5000/api/properties/edit/${id}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data'
        }
      });
      setSuccess('Property update হয়েছে! Admin আবার approve করলে দেখা যাবে।');
      setTimeout(() => navigate('/my-properties'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Error হয়েছে');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>✏️ Property Edit করুন</h2>
        {error && <p style={styles.error}>{error}</p>}
        {success && <p style={styles.success}>{success}</p>}

        <input style={styles.input} name="title" placeholder="বিজ্ঞাপনের শিরোনাম" value={form.title} onChange={handleChange} />

        <select style={styles.input} name="location" value={form.location} onChange={handleChange}>
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

        <textarea style={styles.textarea} name="description" placeholder="বিস্তারিত বিবরণ..." value={form.description} onChange={handleChange} />

        <div style={styles.imageBox}>
          <label style={styles.imageLabel}>
            📷 নতুন ছবি আপলোড করুন
            <input type="file" multiple accept="image/*" onChange={handleImages} style={{ display: 'none' }} />
          </label>
          {previews.length > 0 && (
            <div style={styles.previews}>
              {previews.map((p, i) => (
                <img key={i} src={p} alt="" style={styles.preview} />
              ))}
            </div>
          )}
        </div>

        <button style={styles.btn} onClick={handleSubmit} disabled={loading}>
          {loading ? 'Update হচ্ছে...' : 'Update করুন'}
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: { minHeight: '80vh', backgroundColor: '#f8f9fa', padding: '32px 24px' },
  card: { backgroundColor: 'white', padding: '40px', borderRadius: '12px', boxShadow: '0 4px 16px rgba(0,0,0,0.1)', maxWidth: '600px', margin: '0 auto' },
  title: { textAlign: 'center', marginBottom: '24px', color: '#2E86AB' },
  input: { width: '100%', padding: '12px', marginBottom: '16px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '15px', boxSizing: 'border-box' },
  row: { display: 'flex', gap: '12px' },
  inputHalf: { width: '50%', padding: '12px', marginBottom: '16px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '15px', boxSizing: 'border-box' },
  textarea: { width: '100%', padding: '12px', marginBottom: '16px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '15px', boxSizing: 'border-box', height: '120px', resize: 'vertical' },
  imageBox: { marginBottom: '16px' },
  imageLabel: { display: 'block', padding: '12px', backgroundColor: '#f0f8ff', border: '2px dashed #2E86AB', borderRadius: '8px', textAlign: 'center', cursor: 'pointer', color: '#2E86AB', fontSize: '15px' },
  previews: { display: 'flex', gap: '8px', flexWrap: 'wrap', marginTop: '12px' },
  preview: { width: '80px', height: '80px', objectFit: 'cover', borderRadius: '6px' },
  btn: { width: '100%', padding: '12px', backgroundColor: '#2E86AB', color: 'white', border: 'none', borderRadius: '8px', fontSize: '16px', cursor: 'pointer', fontWeight: 'bold' },
  error: { color: 'red', textAlign: 'center', marginBottom: '12px' },
  success: { color: 'green', textAlign: 'center', marginBottom: '12px' },
};

export default EditProperty;