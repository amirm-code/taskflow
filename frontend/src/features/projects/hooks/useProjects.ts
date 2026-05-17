import { useState, useEffect } from 'react'
import { projectsApi } from '../api/projectsApi'
import type { Project } from '../../../shared/types'

export const useProjects = () => {
    const [projects, setProjects] = useState<Project[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const fetchProjects = async () => {
        try {
            setLoading(true)
            const data = await projectsApi.getAll()
            setProjects(data)
        } catch {
            setError('Impossible de charger les projets')
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchProjects()
    }, [])

    const createProject = async (name: string, description: string) => {
        const newProject = await projectsApi.create({ name, description })
        setProjects(prev => [...prev, newProject])
        return newProject
    }

    const deleteProject = async (id: number) => {
        await projectsApi.delete(id)
        setProjects(prev => prev.filter(p => p.id !== id))
    }

    return { projects, loading, error, createProject, deleteProject, refetch: fetchProjects }
}