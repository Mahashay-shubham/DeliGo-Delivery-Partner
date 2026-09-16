const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export async function apiClient(path, options = {}) {
  try {
    const token = localStorage.getItem('deligo_token')
    const response = await fetch(`${API_BASE_URL}${path}`, {
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
      },
      ...options,
    })

    if (!response.ok) {
      const contentType = response.headers.get('content-type') || ''
      const payload = contentType.includes('application/json') ? await response.json() : null
      const fieldErrors = payload?.errors ? Object.values(payload.errors).join(' ') : ''
      throw new Error(fieldErrors || payload?.message || `Request failed with status ${response.status}`)
    }

    if (response.status === 204) {
      return null
    }

    return response.json()
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error('Unable to reach the Deligo backend. Start the Spring Boot server and try again.', { cause: error })
    }
    throw error
  }
}
