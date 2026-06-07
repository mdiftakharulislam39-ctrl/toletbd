const express = require('express');
const router = express.Router();
const Property = require('../models/Property');
const jwt = require('jsonwebtoken');

// Middleware — login check
const auth = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) return res.status(401).json({ message: 'Login করুন' });
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch {
    res.status(401).json({ message: 'Token invalid' });
  }
};

// Property add করুন (owner only)
router.post('/add', auth, async (req, res) => {
  try {
    const property = new Property({
      ...req.body,
      owner_id: req.user.userId
    });
    await property.save();
    res.status(201).json({ message: 'Property add হয়েছে!', property });
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

// সব property দেখুন (approved)
router.get('/all', async (req, res) => {
  try {
    const { location, rent_min, rent_max, property_type, tenant_type } = req.query;
    let filter = { status: 'approved' };

    if (location) filter.location = { $regex: location, $options: 'i' };
    if (property_type) filter.property_type = property_type;
    if (tenant_type) filter.tenant_type = tenant_type;
    if (rent_min || rent_max) {
      filter.rent = {};
      if (rent_min) filter.rent.$gte = Number(rent_min);
      if (rent_max) filter.rent.$lte = Number(rent_max);
    }

    const properties = await Property.find(filter).populate('owner_id', 'name phone');
    res.json(properties);
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

// একটা property details
router.get('/:id', async (req, res) => {
  try {
    const property = await Property.findById(req.params.id).populate('owner_id', 'name phone email');
    if (!property) return res.status(404).json({ message: 'পাওয়া যায়নি' });
    res.json(property);
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

module.exports = router;