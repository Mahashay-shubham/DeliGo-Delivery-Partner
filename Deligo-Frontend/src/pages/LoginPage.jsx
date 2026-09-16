import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/useAuth.js'

function LoginPage() {
  const { login, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (isAuthenticated) return <Navigate to="/dashboard" replace />

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await login(form)
      navigate(location.state?.from?.pathname || '/dashboard', { replace: true })
    } catch (requestError) {
      setError(requestError.message || 'Unable to sign in. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="auth-page page-enter">
      <div className="auth-aside"><span className="brand-mark">D</span><p className="eyebrow">DeliGo dispatch</p><h1>Delivery, in perfect flow.</h1><p>One calm place for every package, route and delivery update.</p><div className="trust-row"><span>●</span> Live order visibility</div></div>
      <form className="auth-card" onSubmit={handleSubmit}>
        <p className="eyebrow">Welcome back</p>
        <h1>Sign in</h1>
        <p className="muted">Use your DeliGo account to continue.</p>
        {error && <p className="form-error">{error}</p>}
        <label>Email address<input type="email" placeholder="you@example.com" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} required /></label>
        <label>Password<input type="password" placeholder="Your password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} required /></label>
        <Link className="forgot-link" to="/forgot-password">Forgot password?</Link>
        <button type="submit" disabled={submitting}>{submitting ? 'Signing in…' : 'Sign in'}</button>
        <p className="auth-switch">New to Deligo? <Link to="/register">Create an account</Link></p>
      </form>
    </section>
  )
}

export default LoginPage
