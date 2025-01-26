const express = require('express');
const router = express.Router();

// Примерен маршрут за получаване на новини
router.get('/', (req, res) => {
  res.json({
    message: 'Feed route working!',
    posts: [
      { id: 1, content: 'Post 1', author: 'User A' },
      { id: 2, content: 'Post 2', author: 'User B' },
    ],
  });
});

// Примерен маршрут за добавяне на публикация
router.post('/', (req, res) => {
  const { content, author } = req.body;
  if (!content || !author) {
    return res.status(400).json({ error: 'Content and author are required' });
  }

  // Логика за запис в базата данни
  res.status(201).json({
    message: 'Post created successfully',
    post: { id: Date.now(), content, author },
  });
});

module.exports = router;
