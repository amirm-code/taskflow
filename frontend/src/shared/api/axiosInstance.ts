import axios from 'axios'

// Instance Axios configurée pour TaskFlow
const axiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    // baseURL → préfixe automatique pour toutes les requêtes
    // VITE_API_URL → variable d'env pour changer l'URL en prod (AWS)
    headers: {
        'Content-Type': 'application/json'
    }
})

// Intercepteur de requête — ajoute le token JWT automatiquement
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
            // → chaque requête aura automatiquement le header JWT
            // → plus besoin de l'ajouter manuellement partout
        }
        return config
    },
    (error) => Promise.reject(error)
)

// Intercepteur de réponse — gère les erreurs globalement
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Token expiré ou invalide → déconnecter
            localStorage.removeItem('token')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export default axiosInstance