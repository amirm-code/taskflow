import { useState, useEffect } from 'react'
import { tasksApi } from '../api/tasksApi'
import type { Task } from '../../../shared/types'

export const useTasks = (projectId: number) => {
    const [tasks, setTasks] = useState<Task[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const fetchTasks = async () => {
        try {
            setLoading(true)
            const data = await tasksApi.getByProject(projectId)
            setTasks(data)
        } catch {
            setError('Impossible de charger les tâches')
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchTasks()
    }, [projectId])

    const createTask = async (title: string, description: string) => {
        const newTask = await tasksApi.create(projectId, { title, description })
        setTasks(prev => [...prev, newTask])
        return newTask
    }

    const updateStatus = async (taskId: number, status: string) => {
        const updated = await tasksApi.updateStatus(projectId, taskId, status)
        setTasks(prev => prev.map(t => t.id === taskId ? updated : t))
    }

    const editTask = async (taskId: number, title: string, description: string) => {
        const updated = await tasksApi.updateTask(projectId, taskId, title, description)
        setTasks(prev => prev.map(t => t.id === taskId ? updated : t))
    }

    const deleteTask = async (taskId: number) => {
        await tasksApi.delete(projectId, taskId)
        setTasks(prev => prev.filter(t => t.id !== taskId))
    }

    return { tasks, loading, error, createTask, updateStatus, editTask, deleteTask }
}