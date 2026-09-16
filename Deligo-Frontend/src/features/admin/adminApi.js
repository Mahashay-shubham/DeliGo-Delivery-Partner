import { apiClient } from '../../api/client.js'
export const adminApi = {
 dashboard: () => apiClient('/admin/dashboard'),
 users: () => apiClient('/admin/users'),
 updateRole: (id, role) => apiClient(`/admin/users/${id}/role`, { method: 'PATCH', body: JSON.stringify({ role }) }),
}
