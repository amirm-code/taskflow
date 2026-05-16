// Types partagés dans toute l'app

export interface User {
    id: number
    name: string
    email: string
    role: 'ADMIN' | 'USER'
}

export interface AuthResponse {
    token: string
    name: string
    email: string
    role: string
}

export interface Project {
    id: number
    name: string
    description: string
    ownerName: string
    ownerEmail: string
    memberCount: number
    taskCount: number
    createdAt: string
}

export interface Task {
    id: number
    title: string
    description: string
    status: 'TODO' | 'IN_PROGRESS' | 'DONE'
    projectId: number
    projectName: string
    assigneeName: string | null
    assigneeEmail: string | null
    createdAt: string
}

export interface PageResponse<T> {
    content: T[]
    currentPage: number
    pageSize: number
    totalElements: number
    totalPages: number
    first: boolean
    last: boolean
}

export interface ApiError {
    status: number
    error: string
    message: string
    timestamp: string
}