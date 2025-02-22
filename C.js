// File: package.json
{
  "name": "railway-station-info",
  "version": "1.0.0",
  "description": "Sri Lankan Railway Station Information System",
  "main": "server.js",
  "scripts": {
    "start": "node server.js",
    "client": "cd client && npm start",
    "dev": "concurrently \"npm run start\" \"npm run client\""
  },
  "dependencies": {
    "express": "^4.17.1",
    "cors": "^2.8.5"
  },
  "devDependencies": {
    "concurrently": "^6.2.0"
  }
}

// File: server.js
const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

// Main line stations data (Colombo Fort to Badulla)
const stations = [
  { 
    name: "Colombo Fort",
    elevation: 5,
    distance: 0
  },
  {
    name: "Maradana",
    elevation: 7,
    distance: 1.8
  },
  {
    name: "Kandy",
    elevation: 500,
    distance: 121
  },
  {
    name: "Peradeniya",
    elevation: 488,
    distance: 114
  },
  {
    name: "Gampola",
    elevation: 536,
    distance: 134
  },
  {
    name: "Nawalapitiya",
    elevation: 628,
    distance: 147
  },
  {
    name: "Hatton",
    elevation: 1271,
    distance: 179
  },
  {
    name: "Nanu Oya",
    elevation: 1890,
    distance: 219
  },
  {
    name: "Badulla",
    elevation: 680,
    distance: 292
  }
];

app.get('/api/stations', (req, res) => {
  res.json(stations);
});

app.get('/api/station/:name', (req, res) => {
  const stationName = req.params.name.toLowerCase();
  const station = stations.find(s => s.name.toLowerCase() === stationName);
  
  if (station) {
    res.json(station);
  } else {
    res.status(404).json({ error: 'Station not found' });
  }
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

// File: client/package.json
{
  "name": "client",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "@testing-library/jest-dom": "^5.11.4",
    "@testing-library/react": "^11.1.0",
    "@testing-library/user-event": "^12.1.10",
    "react": "^17.0.2",
    "react-dom": "^17.0.2",
    "react-scripts": "4.0.3",
    "axios": "^0.21.1",
    "tailwindcss": "^2.2.19"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "proxy": "http://localhost:5000"
}

// File: client/src/App.js
import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [station, setStation] = useState('');
  const [stationInfo, setStationInfo] = useState(null);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.get(`/api/station/${station}`);
      setStationInfo(response.data);
      setError('');
    } catch (err) {
      setStationInfo(null);
      setError('Station not found. Please check the station name.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-blue-800 text-white p-4">
        <div className="container mx-auto">
          <h1 className="text-3xl font-bold">Sri Lanka Railways</h1>
          <p className="text-sm mt-1">Station Information System</p>
        </div>
      </nav>

      <main className="container mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-lg p-6 max-w-2xl mx-auto">
          <div className="mb-8">
            <h2 className="text-2xl font-semibold mb-4">Welcome to Sri Lanka Railways</h2>
            <p className="text-gray-600">
              Discover information about railway stations along the Main Line from Colombo Fort to Badulla.
              Enter a station name to find its elevation and distance from Colombo Fort.
            </p>
          </div>

          <form onSubmit={handleSearch} className="mb-6">
            <div className="flex gap-4">
              <input
                type="text"
                value={station}
                onChange={(e) => setStation(e.target.value)}
                placeholder="Enter station name"
                className="flex-1 p-2 border border-gray-300 rounded"
                required
              />
              <button
                type="submit"
                className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
              >
                Search
              </button>
            </div>
          </form>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          {stationInfo && (
            <div className="bg-green-50 border border-green-200 rounded p-6">
              <h3 className="text-xl font-semibold mb-4">{stationInfo.name}</h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-gray-600">Elevation</p>
                  <p className="text-2xl font-bold">{stationInfo.elevation} m</p>
                </div>
                <div>
                  <p className="text-gray-600">Distance from Colombo Fort</p>
                  <p className="text-2xl font-bold">{stationInfo.distance} km</p>
                </div>
              </div>
            </div>
          )}

          <div className="mt-8 bg-gray-50 p-4 rounded">
            <h3 className="text-lg font-semibold mb-2">Quick Facts</h3>
            <ul className="text-gray-600 space-y-2">
              <li>• The Main Line runs from Colombo Fort to Badulla</li>
              <li>• Total distance: 292 km</li>
              <li>• Highest elevation: 1,890m (Nanu Oya)</li>
              <li>• Built during British Colonial period (1864-1927)</li>
            </ul>
          </div>
        </div>
      </main>

      <footer className="bg-gray-800 text-white mt-12 py-6">
        <div className="container mx-auto px-4 text-center">
          <p>© 2025 Sri Lanka Railways. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}

export default App;

// File: client/src/index.css
@tailwind base;
@tailwind components;
@tailwind utilities;

// File: client/public/index.html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="theme-color" content="#000000" />
    <meta
      name="description"
      content="Sri Lanka Railways Station Information System"
    />
    <title>Sri Lanka Railways Info</title>
  </head>
  <body>
    <noscript>You need to enable JavaScript to run this app.</noscript>
    <div id="root"></div>
  </body>
</html>
