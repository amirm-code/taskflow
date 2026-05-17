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

interface CreateProjectDialogProps {
    onCreate: (name: string, description: string) => Promise<void>
}

const CreateProjectDialog = ({ onCreate }: CreateProjectDialogProps) => {
    const [open, setOpen] = useState(false)
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)



    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)
        setError(null)
        try {
            await onCreate(name, description)
            setOpen(false)
            setName('')
            setDescription('')
        } catch {
            setError('Le nom doit faire au moins 2 caractères')
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>+ Nouveau projet</Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Créer un projet</DialogTitle>
                    {error && (
                        <p className="text-sm text-red-500 mt-2">{error}</p>
                    )}
                </DialogHeader>
                <form onSubmit={handleSubmit} className="flex flex-col gap-4 mt-4">
                    <div className="flex flex-col gap-2">
                        <Label htmlFor="name">Nom du projet</Label>
                        <Input
                            id="name"
                            value={name}
                            onChange={e => setName(e.target.value)}
                            placeholder="Mon projet"
                            required
                            minLength={2}
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor="description">Description</Label>
                        <Input
                            id="description"
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

export default CreateProjectDialog