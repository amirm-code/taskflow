import axiosInstance from '../../../shared/api/axiosInstance'
import type { Project } from '../../../shared/types'

interface CreateProjectRequest {
    name: string
    description: string
}

export const projectsApi = {

    getAll: async (): Promise<Project[]> => {
        const response = await axiosInstance.get<Project[]>('/api/projects')
        return response.data
    },

    create: async (data: CreateProjectRequest): Promise<Project> => {
        const response = await axiosInstance.post<Project>('/api/projects', data)
        return response.data
    },

    delete: async (id: number): Promise<void> => {
        await axiosInstance.delete(`/api/projects/${id}`)
    }
}