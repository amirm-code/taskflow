import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card'
import { Badge } from '../../../components/ui/badge'
import { Button } from '../../../components/ui/button'
import type { Project } from '../../../shared/types'

interface ProjectCardProps {
    project: Project
    onDelete: (id: number) => void
    onClick: (id: number) => void
}

const ProjectCard = ({ project, onDelete, onClick }: ProjectCardProps) => {
    return (
        <Card
            className="cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => onClick(project.id)}
        >
            <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg">{project.name}</CardTitle>
                <Button
                    variant="destructive"
                    size="sm"
                    onClick={(e) => {
                        e.stopPropagation() // ← évite de déclencher onClick du card
                        onDelete(project.id)
                    }}
                >
                    Supprimer
                </Button>
            </CardHeader>
            <CardContent>
                <p className="text-sm text-gray-500 mb-3">
                    {project.description || 'Aucune description'}
                </p>
                <div className="flex gap-2">
                    <Badge variant="secondary">
                        {project.taskCount} tâches
                    </Badge>
                    <Badge variant="outline">
                        {project.memberCount} membres
                    </Badge>
                </div>
                <p className="text-xs text-gray-400 mt-2">
                    Owner : {project.ownerName}
                </p>
            </CardContent>
        </Card>
    )
}

export default ProjectCard