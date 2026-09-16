import { apiClient } from '../../api/client.js'
export const profileApi = { me: () => apiClient('/users/me'), update: (profile) => apiClient('/users/me', { method: 'PUT', body: JSON.stringify(profile) }) }
