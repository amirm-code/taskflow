import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../shared/context/AuthContext'
import { authApi } from '../api/authApi'
import type { User } from '../../../shared/types'

export const useRegister = () => {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleRegister = async (name: string, email: string, password: string) => {
        setLoading(true)
        setError(null)
        try {
            const response = await authApi.register({ name, email, password })
            const user: User = {
                id: 0,
                name: response.name,
                email: response.email,
                role: response.role as 'ADMIN' | 'USER'
            }
            login(response.token, user)
            navigate('/dashboard')
        } catch {
            setError('Erreur lors de l\'inscription')
        } finally {
            setLoading(false)
        }
    }

    return { handleRegister, loading, error }
}