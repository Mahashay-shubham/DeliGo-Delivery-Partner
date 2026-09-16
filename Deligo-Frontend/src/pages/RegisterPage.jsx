import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/useAuth.js'

function RegisterPage() {
  const { register, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', phone: '', password: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (isAuthenticated) return <Navigate to="/dashboard" replace />

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await register(form)
      navigate('/dashboard', { replace: true })
    } catch (requestError) {
      setError(requestError.message || 'Unable to create your account. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="auth-page page-enter">
      <div className="auth-aside"><span className="brand-mark">D</span><p className="eyebrow">Send with confidence</p><h1>Your next delivery starts here.</h1><p>Create an account to arrange deliveries and track every milestone.</p><div className="trust-row"><span>●</span> Clear delivery updates</div></div>
      <form className="auth-card" onSubmit={handleSubmit}>
        <p className="eyebrow">Start delivering</p>
        <h1>Create account</h1>
        <p className="muted">A few details and you’re ready to send.</p>
        {error && <p className="form-error">{error}</p>}
        <label>Full name<input value={form.fullName} onChange={(event) => setForm({ ...form, fullName: event.target.value })} required maxLength="100" /></label>
        <label>Email<input type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} required /></label>
        <label>Phone <span className="optional">optional</span><input type="tel" value={form.phone} onChange={(event) => setForm({ ...form, phone: event.target.value })} /></label>
        <label>Password<input type="password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} required minLength="8" /></label>
        <button type="submit" disabled={submitting}>{submitting ? 'Creating account…' : 'Create account'}</button>
        <p className="auth-switch">Already have an account? <Link to="/login">Sign in</Link></p>
      </form>
    </section>
  )
}

export default RegisterPage
