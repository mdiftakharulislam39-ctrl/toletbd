const express = require('express');
const router = express.Router();
const Property = require('../models/Property');
const User = require('../models/User');
const jwt = require('jsonwebtoken');

// Admin middleware
const adminAuth = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) return res.status(401).json({ message: 'Login করুন' });
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    if (decoded.role !== 'admin') return res.status(403).json({ message: 'Admin only' });
    req.user = decoded;
    next();
  } catch {
    res.status(401).json({ message: 'Token invalid' });
  }
};

// সব pending properties
router.get('/properties', adminAuth, async (req, res) => {
  try {
    const properties = await Property.find().populate('owner_id', 'name phone email');
    res.json(properties);
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

// Property approve/reject
router.put('/properties/:id', adminAuth, async (req, res) => {
  try {
    const { status } = req.body;
    const property = await Property.findByIdAndUpdate(
      req.params.id,
      { status },
      { new: true }
    );
    res.json({ message: `Property ${status} হয়েছে!`, property });
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

// সব users
router.get('/users', adminAuth, async (req, res) => {
  try {
    const users = await User.find().select('-password');
    res.json(users);
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

module.exports = router;