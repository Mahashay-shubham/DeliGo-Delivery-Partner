import { useState } from 'react'
import { authApi } from './authApi.js'
import { AuthContext } from './authContext.js'

const TOKEN_KEY = 'deligo_token'
const USER_KEY = 'deligo_user'

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY))
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)

  async function authenticate(action, payload) {
    const response = await action(payload)
    localStorage.setItem(TOKEN_KEY, response.token)
    localStorage.setItem(USER_KEY, JSON.stringify(response.user))
    setUser(response.user)
  }

  async function login(credentials) {
    await authenticate(authApi.login, credentials)
  }

  async function register(details) {
    await authenticate(authApi.register, details)
  }

  async function logout() {
    try {
      await authApi.logout()
    } catch {
      // A local logout must still succeed when the server is unreachable.
    }
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }

  const value = { user, login, register, logout, isAuthenticated: Boolean(user) }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
