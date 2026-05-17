import { useState } from 'react'
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from '../../../components/ui/dialog'
import { Button } from '../../../components/ui/button'
import { Input } from '../../../components/ui/input'
import { Label } from '../../../components/ui/label'
import type { Task } from '../../../shared/types'

interface EditTaskDialogProps {
    task: Task
    onEdit: (taskId: number, title: string, description: string) => Promise<void>
}

const EditTaskDialog = ({ task, onEdit }: EditTaskDialogProps) => {
    const [open, setOpen] = useState(false)
    const [title, setTitle] = useState(task.title)
    const [description, setDescription] = useState(task.description || '')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)
        setError(null)
        try {
            await onEdit(task.id, title, description)
            setOpen(false)
        } catch {
            setError('Erreur lors de la modification')
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button size="sm" variant="ghost" className="text-xs h-7">
                    ✏️
                </Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Modifier la tâche</DialogTitle>
                </DialogHeader>
                {error && <p className="text-sm text-red-500">{error}</p>}
                <form onSubmit={handleSubmit} className="flex flex-col gap-4 mt-2">
                    <div className="flex flex-col gap-2">
                        <Label htmlFor="title">Titre</Label>
                        <Input
                            id="title"
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            required
                            minLength={2}
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor="desc">Description</Label>
                        <Input
                            id="desc"
                            value={description}
                            onChange={e => setDescription(e.target.value)}
                        />
                    </div>
                    <Button type="submit" disabled={loading}>
                        {loading ? 'Modification...' : 'Modifier'}
                    </Button>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default EditTaskDialog