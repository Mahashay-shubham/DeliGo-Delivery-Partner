import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../features/auth/useAuth.js'

function AppLayout({ children }) {
  const { isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <Link className="brand" to={isAuthenticated ? '/dashboard' : '/login'}><span className="brand-mark">D</span><span>DeliGo</span></Link>
        {isAuthenticated && <nav className="main-nav"><NavLink to="/dashboard">Overview</NavLink><NavLink to="/orders">Deliveries</NavLink><NavLink to="/profile">Profile</NavLink><button className="link-button" onClick={handleLogout}>Log out</button></nav>}
      </header>
      <main className="app-content">{children}</main>
    </div>
  )
}

export default AppLayout
