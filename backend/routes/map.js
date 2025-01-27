const express = require('express');
const authenticateToken = require('../middleware/authenticateToken'); // Middleware за защита на маршрутите
const Location = require('../models/Location'); // Модел за локации

const router = express.Router();

// GET: Извличане на всички локации
router.get('/locations', async (req, res) => {
  try {
    const locations = await Location.find().sort({ createdAt: -1 }); // Вземи всички локации от базата
    res.status(200).json({ locations });
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch locations' });
  }
});

// POST: Добавяне на нова локация
router.post('/add-location', authenticateToken, async (req, res) => {
  const { latitude, longitude, description } = req.body;

  if (!latitude || !longitude || !description) {
    return res.status(400).json({ error: 'Latitude, longitude, and description are required' });
  }

  try {
    const newLocation = new Location({
      userId: req.user.id, // ID на потребителя от токена
      latitude,
      longitude,
      description,
    });
    await newLocation.save(); // Запиши новата локация в базата
    res.status(201).json({ message: 'Location added successfully', location: newLocation });
  } catch (error) {
    res.status(500).json({ error: 'Failed to add location' });
  }
});

module.exports = router;
