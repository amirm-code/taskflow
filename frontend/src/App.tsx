import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './features/auth/pages/LoginPage'
import RegisterPage from './features/auth/pages/RegisterPage'
import ProjectsPage from './features/projects/pages/ProjectsPage'
import PrivateRoute from './shared/components/PrivateRoute'
import ProjectDetailPage from './features/tasks/pages/ProjectDetailPage'

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/dashboard" element={
                <PrivateRoute>
                    <ProjectsPage />
                </PrivateRoute>
            } />
            <Route path="/projects/:id" element={
                <PrivateRoute>
                    <ProjectDetailPage />
                </PrivateRoute>
            } />
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
    )
}

export default App