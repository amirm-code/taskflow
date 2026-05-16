import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useRegister } from '../hooks/useRegister'

const RegisterPage = () => {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const { handleRegister, loading, error } = useRegister()

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        await handleRegister(name, email, password)
    }

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>TaskFlow</h1>
                <h2 style={styles.subtitle}>Inscription</h2>

                {error && <div style={styles.error}>{error}</div>}

                <form onSubmit={onSubmit} style={styles.form}>
                    <input
                        type="text"
                        placeholder="Nom complet"
                        value={name}
                        onChange={e => setName(e.target.value)}
                        style={styles.input}
                        required
                    />
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        style={styles.input}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Mot de passe (8 caractères min)"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        style={styles.input}
                        required
                        minLength={8}
                    />
                    <button
                        type="submit"
                        disabled={loading}
                        style={styles.button}>
                        {loading ? 'Inscription...' : 'S\'inscrire'}
                    </button>
                </form>

                <p style={styles.link}>
                    Déjà un compte ? <Link to="/login">Se connecter</Link>
                </p>
            </div>
        </div>
    )
}

const styles = {
    container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f5f5f5' },
    card: { background: 'white', padding: '2rem', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px' },
    title: { textAlign: 'center' as const, color: '#2563eb', margin: '0 0 0.5rem' },
    subtitle: { textAlign: 'center' as const, margin: '0 0 1.5rem', color: '#666' },
    error: { background: '#fee2e2', color: '#dc2626', padding: '0.75rem', borderRadius: '4px', marginBottom: '1rem', fontSize: '14px' },
    form: { display: 'flex', flexDirection: 'column' as const, gap: '1rem' },
    input: { padding: '0.75rem', border: '1px solid #ddd', borderRadius: '4px', fontSize: '14px' },
    button: { padding: '0.75rem', background: '#2563eb', color: 'white', border: 'none', borderRadius: '4px', fontSize: '14px', cursor: 'pointer' },
    link: { textAlign: 'center' as const, marginTop: '1rem', fontSize: '14px', color: '#666' }
}

export default RegisterPage