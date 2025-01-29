const express = require('express');
const authenticateToken = require('../middleware/authenticateToken'); 
const Post = require('../models/Post'); 

const router = express.Router();

router.get('/', async (req, res) => {
  try {
    const posts = await Post.find()
      .sort({ createdAt: -1 }) 
      .populate('user', 'email'); 
    res.status(200).json({ posts });
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch posts' });
  }
});

router.post('/', authenticateToken, async (req, res) => {
  const { image, description } = req.body;

  if (!image || !description) {
    return res.status(400).json({ error: 'Image and description are required' });
  }

  try {
    const newPost = new Post({
      user: req.user.id, 
      image: Buffer.from(image, 'base64'),
      description,
    });
    await newPost.save(); 
    res.status(201).json({ message: 'Post created successfully', post: newPost });
  } catch (error) {
    res.status(500).json({ error: 'Failed to create post' });
  }
});

module.exports = router;
