const mongoose = require('mongoose');

const propertySchema = new mongoose.Schema({
  title: { type: String, required: true },
  description: { type: String },
  location: { type: String, required: true },
  address: { type: String, required: true },
  rent: { type: Number, required: true },
  advance: { type: Number },
  property_type: { 
    type: String, 
    enum: ['flat', 'room', 'seat', 'hostel'], 
    required: true 
  },
  bedrooms: { type: Number },
  bathrooms: { type: Number },
  tenant_type: { 
    type: String, 
    enum: ['bachelor', 'family', 'any'], 
    default: 'any' 
  },
  images: [{ type: String }],
  owner_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  status: { 
  type: String, 
  enum: ['pending', 'approved', 'rejected', 'rented'], 
  default: 'pending' 
},
}, { timestamps: true });

module.exports = mongoose.model('Property', propertySchema);