import axiosInstance from '../../../shared/api/axiosInstance'
import type { Task } from '../../../shared/types'

interface CreateTaskRequest {
    title: string
    description: string
}

export const tasksApi = {

    getByProject: async (projectId: number): Promise<Task[]> => {
        const response = await axiosInstance.get<Task[]>(
            `/api/projects/${projectId}/tasks`
        )
        return response.data
    },

    create: async (projectId: number, data: CreateTaskRequest): Promise<Task> => {
        const response = await axiosInstance.post<Task>(
            `/api/projects/${projectId}/tasks`,
            data
        )
        return response.data
    },

    updateStatus: async (
        projectId: number,
        taskId: number,
        status: string
    ): Promise<Task> => {
        const response = await axiosInstance.patch<Task>(
            `/api/projects/${projectId}/tasks/${taskId}/status?status=${status}`
        )
        return response.data
    },

    updateTask: async (
        projectId: number,
        taskId: number,
        title: string,
        description: string
    ): Promise<Task> => {
        const response = await axiosInstance.put<Task>(
            `/api/projects/${projectId}/tasks/${taskId}`,
            { title, description }
        )
        return response.data
    },

    delete: async (projectId: number, taskId: number): Promise<void> => {
        await axiosInstance.delete(
            `/api/projects/${projectId}/tasks/${taskId}`
        )
    }
}