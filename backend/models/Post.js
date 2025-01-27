const mongoose = require('mongoose');

const postSchema = new mongoose.Schema({
  image: { type: Buffer },  // Поле за съхранение на изображението като Binary Data
  description: { type: String, required: true },
  user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }
}, { timestamps: true });

module.exports = mongoose.model('Post', postSchema);
