const express = require('express');
const authenticateToken = require('../middleware/authenticateToken'); 
const Location = require('../models/Location'); 

const router = express.Router();

router.get('/locations', async (req, res) => {
  try {
    const locations = await Location.find().sort({ createdAt: -1 }); 
    res.status(200).json({ locations });
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch locations' });
  }
});

router.post('/add-location', authenticateToken, async (req, res) => {
  const { latitude, longitude, description } = req.body;

  if (!latitude || !longitude || !description) {
    return res.status(400).json({ error: 'Latitude, longitude, and description are required' });
  }

  try {
    const newLocation = new Location({
      userId: req.user.id, 
      latitude,
      longitude,
      description,
    });
    await newLocation.save(); 
    res.status(201).json({ message: 'Location added successfully', location: newLocation });
  } catch (error) {
    res.status(500).json({ error: 'Failed to add location' });
  }
});

module.exports = router;
