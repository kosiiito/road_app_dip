const express = require('express');
const router = express.Router();

// Примерен route за автентикация
router.post('/login', (req, res) => {
  const { email, password } = req.body;
  // Тук добави логиката за автентикация
  res.json({ message: 'Login successful', email });
});

module.exports = router;
