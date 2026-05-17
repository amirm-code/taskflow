import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../shared/context/AuthContext'
import { useProjects } from '../hooks/useProjects'
import ProjectCard from '../components/ProjectCard'
import CreateProjectDialog from '../components/CreateProjectDialog'
import { Button } from '../../../components/ui/button'
import { Separator } from '../../../components/ui/separator'

const ProjectsPage = () => {
    const { user, logout } = useAuth()
    const navigate = useNavigate()
    const { projects, loading, error, createProject, deleteProject } = useProjects()

    const handleCreate = async (name: string, description: string) => {
        await createProject(name, description)
    }

    const handleDelete = async (id: number) => {
        if (confirm('Supprimer ce projet ?')) {
            await deleteProject(id)
        }
    }

    const handleProjectClick = (id: number) => {
        navigate(`/projects/${id}`)
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navbar */}
            <nav className="bg-white border-b px-6 py-4 flex items-center justify-between">
                <h1 className="text-xl font-bold text-blue-600">TaskFlow</h1>
                <div className="flex items-center gap-4">
                    <span className="text-sm text-gray-600">
                        Bonjour, {user?.name}
                    </span>
                    <Button variant="outline" size="sm" onClick={logout}>
                        Déconnexion
                    </Button>
                </div>
            </nav>

            {/* Content */}
            <main className="max-w-6xl mx-auto px-6 py-8">
                <div className="flex items-center justify-between mb-6">
                    <div>
                        <h2 className="text-2xl font-bold">Mes projets</h2>
                        <p className="text-gray-500 text-sm mt-1">
                            {projects.length} projet{projects.length > 1 ? 's' : ''}
                        </p>
                    </div>
                    <CreateProjectDialog onCreate={handleCreate} />
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

                {!loading && projects.length === 0 && (
                    <div className="text-center py-12">
                        <p className="text-gray-500 mb-4">
                            Aucun projet pour l'instant
                        </p>
                        <CreateProjectDialog onCreate={handleCreate} />
                    </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {projects.map(project => (
                        <ProjectCard
                            key={project.id}
                            project={project}
                            onDelete={handleDelete}
                            onClick={handleProjectClick}
                        />
                    ))}
                </div>
            </main>
        </div>
    )
}

export default ProjectsPage