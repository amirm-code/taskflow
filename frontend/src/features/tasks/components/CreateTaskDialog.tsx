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

interface CreateTaskDialogProps {
    onCreate: (title: string, description: string) => Promise<void>
}

const CreateTaskDialog = ({ onCreate }: CreateTaskDialogProps) => {
    const [open, setOpen] = useState(false)
    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)
        setError(null)
        try {
            await onCreate(title, description)
            setOpen(false)
            setTitle('')
            setDescription('')
        } catch {
            setError('Le titre doit faire au moins 2 caractères')
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button size="sm">+ Nouvelle tâche</Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Créer une tâche</DialogTitle>
                </DialogHeader>
                {error && <p className="text-sm text-red-500">{error}</p>}
                <form onSubmit={handleSubmit} className="flex flex-col gap-4 mt-2">
                    <div className="flex flex-col gap-2">
                        <Label htmlFor="title">Titre</Label>
                        <Input
                            id="title"
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            placeholder="Titre de la tâche"
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
                            placeholder="Description optionnelle"
                        />
                    </div>
                    <Button type="submit" disabled={loading}>
                        {loading ? 'Création...' : 'Créer'}
                    </Button>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default CreateTaskDialog