import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './features/auth/pages/LoginPage'
import RegisterPage from './features/auth/pages/RegisterPage'
import PrivateRoute from './shared/components/PrivateRoute'

// Dashboard temporaire — on le construira après
const Dashboard = () => {
    return <div style={{ padding: '2rem' }}><h1>Dashboard TaskFlow</h1></div>
}

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/dashboard" element={
                <PrivateRoute>
                    <Dashboard />
                </PrivateRoute>
            } />
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
    )
}

export default App