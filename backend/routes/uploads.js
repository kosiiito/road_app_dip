const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');  // За да можем да четем файловете
const router = express.Router();
const authenticateToken = require('../middleware/authenticateToken');  // Проверка на JWT токена
const Post = require('../models/Post');  // Модел за постове

// Настройване на multer за качване на файлове
const storage = multer.memoryStorage(); // Използваме memoryStorage вместо diskStorage
const upload = multer({ storage: storage });

// Рутер за ъплоуд на снимка
router.post('/', authenticateToken, upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ message: 'No file uploaded' });
  }

  // Преобразуваме файла в Buffer
  const imageBuffer = req.file.buffer; 

  // Записване на файла в базата данни
  const newPost = new Post({
    image: imageBuffer, // Записваме самия файл като Binary Data
    description: req.body.description, // Описание, ако има такова
    user: req.user.id // Потребителят, който качва (взимаме от JWT токена)
  });

  newPost.save()
    .then(post => res.json({
      message: 'File uploaded successfully',
      post
    }))
    .catch(err => res.status(500).json({ error: 'Error saving the post' }));
});

module.exports = router;
