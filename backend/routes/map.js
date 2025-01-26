const express = require('express');
const router = express.Router();

// Примерен маршрут за получаване на координати
router.get('/locations', (req, res) => {
  res.json({
    message: 'Map route working!',
    locations: [
      { id: 1, name: 'Location A', lat: 42.6977, lng: 23.3219 },
      { id: 2, name: 'Location B', lat: 43.2141, lng: 27.9147 },
    ],
  });
});

// Примерен маршрут за добавяне на нова локация
router.post('/add-location', (req, res) => {
  const { name, lat, lng } = req.body;
  if (!name || !lat || !lng) {
    return res.status(400).json({ error: 'Name, latitude, and longitude are required' });
  }

  // Логика за запис в базата данни
  res.status(201).json({
    message: 'Location added successfully',
    location: { id: Date.now(), name, lat, lng },
  });
});

module.exports = router;
