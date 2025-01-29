const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');  
const router = express.Router();
const authenticateToken = require('../middleware/authenticateToken'); 
const Post = require('../models/Post');  

const storage = multer.memoryStorage(); 
const upload = multer({ storage: storage });

router.post('/', authenticateToken, upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ message: 'No file uploaded' });
  }

  const imageBuffer = req.file.buffer; 

  const newPost = new Post({
    image: imageBuffer, 
    description: req.body.description,
    user: req.user.id 
  });

  newPost.save()
    .then(post => res.json({
      message: 'File uploaded successfully',
      post
    }))
    .catch(err => res.status(500).json({ error: 'Error saving the post' }));
});

module.exports = router;
