require('dotenv').config(); // Зарежда променливите от .env файла
const express = require('express'); // Импортиране на Express.js
const connectDB = require('./config/db'); // Връзка с базата данни
const cors = require('cors'); // Разрешава CORS заявки

const app = express(); // Създава Express приложение

// Свързване към MongoDB
connectDB();

// Middleware
app.use(cors()); // Активира CORS
app.use(express.json()); // Парсва JSON в заявките

// Рутери
app.use('/api/auth', require('./routes/auth')); // Рутер за автентикация
app.use('/api/uploads', require('./routes/uploads')); // Рутер за качване на файлове
app.use('/api/feed', require('./routes/feed')); // Рутер за новинарския поток
app.use('/api/map', require('./routes/map')); // Рутер за картографска функционалност

// Конфигуриране на порта
const PORT = process.env.PORT || 5000;

// Стартиране на сървъра
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
