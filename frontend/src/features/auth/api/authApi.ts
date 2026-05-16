import axiosInstance from '../../../shared/api/axiosInstance'
import type { AuthResponse } from '../../../shared/types'

interface RegisterRequest {
    name: string
    email: string
    password: string
}

interface LoginRequest {
    email: string
    password: string
}

export const authApi = {

    register: async (data: RegisterRequest): Promise<AuthResponse> => {
        const response = await axiosInstance.post<AuthResponse>('/api/auth/register', data)
        return response.data
    },

    login: async (data: LoginRequest): Promise<AuthResponse> => {
        const response = await axiosInstance.post<AuthResponse>('/api/auth/login', data)
        return response.data
    }
}