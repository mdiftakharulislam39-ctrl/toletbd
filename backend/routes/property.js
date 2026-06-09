const express = require('express');
const router = express.Router();
const Property = require('../models/Property');
const jwt = require('jsonwebtoken');
const cloudinary = require('../cloudinary');
const multer = require('multer');

const storage = multer.memoryStorage();
const upload = multer({ storage });

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

const uploadToCloudinary = (buffer) => {
  return new Promise((resolve, reject) => {
    cloudinary.uploader.upload_stream(
      { folder: 'toletbd' },
      (error, result) => {
        if (error) reject(error);
        else resolve(result.secure_url);
      }
    ).end(buffer);
  });
};

router.post('/add', auth, upload.array('images', 5), async (req, res) => {
  try {
    let imageUrls = [];
    if (req.files && req.files.length > 0) {
      for (const file of req.files) {
        const url = await uploadToCloudinary(file.buffer);
        imageUrls.push(url);
      }
    }
    const property = new Property({
      ...req.body,
      images: imageUrls,
      owner_id: req.user.userId
    });
    await property.save();
    res.status(201).json({ message: 'Property add হয়েছে!', property });
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

router.put('/edit/:id', auth, upload.array('images', 5), async (req, res) => {
  try {
    const property = await Property.findById(req.params.id);
    if (!property) return res.status(404).json({ message: 'পাওয়া যায়নি' });
    if (property.owner_id.toString() !== req.user.userId) {
      return res.status(403).json({ message: 'আপনি এই property edit করতে পারবেন না' });
    }
    let imageUrls = property.images;
    if (req.files && req.files.length > 0) {
      imageUrls = [];
      for (const file of req.files) {
        const url = await uploadToCloudinary(file.buffer);
        imageUrls.push(url);
      }
    }
    const updated = await Property.findByIdAndUpdate(
      req.params.id,
      { ...req.body, images: imageUrls, status: 'pending' },
      { new: true }
    );
    res.json({ message: 'Property update হয়েছে!', property: updated });
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

router.delete('/delete/:id', auth, async (req, res) => {
  try {
    const property = await Property.findById(req.params.id);
    if (!property) return res.status(404).json({ message: 'পাওয়া যায়নি' });
    if (property.owner_id.toString() !== req.user.userId) {
      return res.status(403).json({ message: 'আপনি এই property delete করতে পারবেন না' });
    }
    await Property.findByIdAndDelete(req.params.id);
    res.json({ message: 'Property delete হয়েছে!' });
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

// ভাড়া হয়ে গেছে mark করুন
router.put('/rented/:id', auth, async (req, res) => {
  try {
    const property = await Property.findById(req.params.id);
    if (!property) return res.status(404).json({ message: 'পাওয়া যায়নি' });
    if (property.owner_id.toString() !== req.user.userId) {
      return res.status(403).json({ message: 'Permission নেই' });
    }
    const updated = await Property.findByIdAndUpdate(
      req.params.id,
      { status: 'rented' },
      { new: true }
    );
    res.json({ message: 'ভাড়া হয়ে গেছে mark করা হয়েছে!', property: updated });
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

// আমার properties
router.get('/my', auth, async (req, res) => {
  try {
    const properties = await Property.find({ owner_id: req.user.userId });
    res.json(properties);
  } catch (err) {
    res.status(500).json({ message: 'Error', error: err.message });
  }
});

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