import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../../../shared/context/AuthContext'
import { useTasks } from '../../tasks/hooks/useTasks'
import KanbanColumn from '../../tasks/components/KanbanColumn'
import CreateTaskDialog from '../../tasks/components/CreateTaskDialog'
import { Button } from '../../../components/ui/button'
import { Separator } from '../../../components/ui/separator'
import { useMemo } from 'react'

const ProjectDetailPage = () => {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const { user, logout } = useAuth()
    const projectId = parseInt(id || '0')

    const { tasks, loading, error, createTask, updateStatus, editTask,  deleteTask } = useTasks(projectId)

    const todoTasks = useMemo(
        () => tasks.filter(t => t.status === 'TODO'),
        [tasks]
    )
    const inProgressTasks = useMemo(
        () => tasks.filter(t => t.status === 'IN_PROGRESS'),
        [tasks]
    )
    const doneTasks = useMemo(
        () => tasks.filter(t => t.status === 'DONE'),
        [tasks]
    )


    const handleCreate = async (title: string, description: string) => {
        await createTask(title, description)
    }

    const handleStatusChange = async (taskId: number, status: string) => {
        await updateStatus(taskId, status)
    }

    const handleEdit = async (taskId: number, title: string, description: string) => {
    await editTask(taskId, title, description)
}

    const handleDelete = async (taskId: number) => {
        if (confirm('Supprimer cette tâche ?')) {
            await deleteTask(taskId)
        }
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navbar */}
            <nav className="bg-white border-b px-6 py-4 flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => navigate('/dashboard')}
                    >
                        ← Projets
                    </Button>
                    <h1 className="text-xl font-bold text-blue-600">TaskFlow</h1>
                </div>
                <div className="flex items-center gap-4">
                    <span className="text-sm text-gray-600">{user?.name}</span>
                    <Button variant="outline" size="sm" onClick={logout}>
                        Déconnexion
                    </Button>
                </div>
            </nav>

            {/* Content */}
            <main className="max-w-7xl mx-auto px-6 py-8">
                <div className="flex items-center justify-between mb-6">
                    <div>
                        <h2 className="text-2xl font-bold">
                            Projet #{projectId}
                        </h2>
                        <p className="text-gray-500 text-sm mt-1">
                            {tasks.length} tâche{tasks.length > 1 ? 's' : ''}
                        </p>
                    </div>
                    <CreateTaskDialog onCreate={handleCreate} />
                </div>

                <Separator className="mb-6" />

                {loading && (
                    <div className="text-center py-12 text-gray-500">
                        Chargement...
                    </div>
                )}

                {error && (
                    <div className="bg-red-50 text-red-600 p-4 rounded-lg">
                        {error}
                    </div>
                )}

                {!loading && (
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <KanbanColumn
                            title="À faire"
                            status="TODO"
                            tasks={todoTasks}
                            onStatusChange={handleStatusChange}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                        />
                        <KanbanColumn
                            title="En cours"
                            status="IN_PROGRESS"
                            tasks={inProgressTasks}
                            onStatusChange={handleStatusChange}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                        />
                        <KanbanColumn
                            title="Terminé"
                            status="DONE"
                            tasks={doneTasks}
                            onStatusChange={handleStatusChange}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                        />
                    </div>
                )}
            </main>
        </div>
    )
}

export default ProjectDetailPage