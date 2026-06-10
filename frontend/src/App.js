import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import PropertyList from './pages/PropertyList';
import PropertyDetail from './pages/PropertyDetails';
import Login from './pages/Login';
import Register from './pages/Register';
import PostProperty from './pages/PostProperty';
import AdminPanel from './pages/AdminPanel';
import MyProperties from './pages/MyProperties';
import EditProperty from './pages/EditProperty';
import ProtectedRoute from './ProtectedRoute';

function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="/properties" element={
          <ProtectedRoute><PropertyList /></ProtectedRoute>
        } />
        <Route path="/properties/:id" element={
          <ProtectedRoute><PropertyDetail /></ProtectedRoute>
        } />
        <Route path="/post-property" element={
          <ProtectedRoute><PostProperty /></ProtectedRoute>
        } />
        <Route path="/my-properties" element={
          <ProtectedRoute><MyProperties /></ProtectedRoute>
        } />
        <Route path="/edit-property/:id" element={
          <ProtectedRoute><EditProperty /></ProtectedRoute>
        } />
        <Route path="/admin" element={
          <ProtectedRoute><AdminPanel /></ProtectedRoute>
        } />
      </Routes>
    </Router>
  );
}

export default App;