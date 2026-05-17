import { Badge } from '../../../components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card'
import { Button } from '../../../components/ui/button'
import type { Task } from '../../../shared/types'
import EditTaskDialog from './EditTaskDialog'

interface KanbanColumnProps {
    title: string
    status: 'TODO' | 'IN_PROGRESS' | 'DONE'
    tasks: Task[]
    onStatusChange: (taskId: number, status: string) => void
    onEdit: (taskId: number, title: string, description: string) => Promise<void>
    onDelete: (taskId: number) => void
}

const statusColors = {
    TODO: 'bg-gray-100',
    IN_PROGRESS: 'bg-blue-50',
    DONE: 'bg-green-50'
}

const nextStatus: Record<string, string> = {
    TODO: 'IN_PROGRESS',
    IN_PROGRESS: 'DONE',
    DONE: 'DONE'
}

const nextStatusLabel: Record<string, string> = {
    TODO: '→ En cours',
    IN_PROGRESS: '→ Terminé',
    DONE: ''
}

const previousStatus = {
    TODO: null,
    IN_PROGRESS: 'TODO',
    DONE: 'IN_PROGRESS'
}

const previousStatusLabel = {
    TODO: null,
    IN_PROGRESS: '← À faire',
    DONE: '← En cours'
}

const KanbanColumn = ({ title, status, tasks, onStatusChange, onEdit, onDelete }: KanbanColumnProps) => {
    return (
        <div className={`rounded-lg p-4 ${statusColors[status]} min-h-64`}>
            <div className="flex items-center gap-2 mb-4">
                <h3 className="font-semibold text-sm uppercase tracking-wide">
                    {title}
                </h3>
                <Badge variant="secondary">{tasks.length}</Badge>
            </div>

            <div className="flex flex-col gap-3">
                {tasks.map(task => (
                    <Card key={task.id} className="shadow-sm">
                        <CardHeader className="pb-2 pt-3 px-3">
                            <CardTitle className="text-sm font-medium">
                                {task.title}
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="px-3 pb-3">
                            {task.description && (
                                <p className="text-xs text-gray-500 mb-2">
                                    {task.description}
                                </p>
                            )}
                            {task.assigneeName && (
                                <p className="text-xs text-blue-500 mb-2">
                                    👤 {task.assigneeName}
                                </p>
                            )}
                            <div className="flex gap-1 mt-2">
                                {/* Bouton édition */}
                                <EditTaskDialog
                                    task={task}
                                    onEdit={onEdit}
                                />
                                {/* Bouton retour — si pas TODO */}
                                {previousStatus[status] && (
                                    <Button
                                        size="sm"
                                        variant="ghost"
                                        className="text-xs h-7"
                                        onClick={() => onStatusChange(task.id, previousStatus[status]!)}
                                    >
                                        {previousStatusLabel[status]}
                                    </Button>
                                )}
                                {/* Bouton avancer — si pas DONE */}
                                {nextStatus[status] !== status && (
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        className="text-xs h-7"
                                        onClick={() => onStatusChange(task.id, nextStatus[status])}
                                    >
                                        {nextStatusLabel[status]}
                                    </Button>
                                )}
                                <Button
                                    size="sm"
                                    variant="destructive"
                                    className="text-xs h-7"
                                    onClick={() => onDelete(task.id)}
                                >
                                    ✕
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                ))}

                {tasks.length === 0 && (
                    <p className="text-xs text-gray-400 text-center py-4">
                        Aucune tâche
                    </p>
                )}
            </div>
        </div>
    )
}

export default KanbanColumn