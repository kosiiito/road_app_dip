const express = require('express');
const router = express.Router();

// Примерен route
router.post('/upload', (req, res) => {
  res.json({ message: 'Upload route' });
});

module.exports = router;
