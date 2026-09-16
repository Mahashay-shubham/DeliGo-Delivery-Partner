import { apiClient } from '../../api/client.js'

export const authApi = {
  login: (credentials) => apiClient('/auth/login', { method: 'POST', body: JSON.stringify(credentials) }),
  register: (details) => apiClient('/auth/register', { method: 'POST', body: JSON.stringify(details) }),
  logout: () => apiClient('/auth/logout', { method: 'POST' }),
  forgotPassword: (email) => apiClient('/auth/forgot-password', { method: 'POST', body: JSON.stringify({ email }) }),
  resetPassword: (token, newPassword) => apiClient('/auth/reset-password', { method: 'POST', body: JSON.stringify({ token, newPassword }) }),
}
