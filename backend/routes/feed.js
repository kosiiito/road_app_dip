const express = require('express');
const authenticateToken = require('../middleware/authenticateToken'); // Middleware за защита на маршрутите
const Upload = require('../models/Upload'); // Модел за снимки/видеа

const router = express.Router();

// GET: Извличане на всички постове
router.get('/', async (req, res) => {
  try {
    const posts = await Upload.find()
      .sort({ uploadedAt: -1 })
      .populate('userId', 'email'); // Включи имейла на автора
    res.status(200).json({ posts });
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch posts' });
  }
});

// POST: Качване на нов пост (примерно съдържание или файл)
router.post('/', authenticateToken, async (req, res) => {
  const { filePath, fileType } = req.body;

  if (!filePath || !fileType) {
    return res.status(400).json({ error: 'File path and file type are required' });
  }

  try {
    const newPost = new Upload({
      userId: req.user.id, // ID на потребителя от токена
      filePath,
      fileType,
    });
    await newPost.save(); // Запиши поста в базата
    res.status(201).json({ message: 'Post created successfully', post: newPost });
  } catch (error) {
    res.status(500).json({ error: 'Failed to create post' });
  }
});

module.exports = router;
