import { apiClient } from '../../api/client.js'

export const orderApi = {
  create: (order) => apiClient('/orders', { method: 'POST', body: JSON.stringify(order) }),
  list: ({ status = '', search = '' } = {}) => apiClient(`/orders?${new URLSearchParams({ ...(status && { status }), ...(search && { search }) })}`),
  get: (id) => apiClient(`/orders/${id}`),
  updateStatus: (id, status) => apiClient(`/orders/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
  assign: (id, deliveryPartnerId) => apiClient(`/admin/orders/${id}/assign`, { method: 'PATCH', body: JSON.stringify({ deliveryPartnerId }) }),
}
