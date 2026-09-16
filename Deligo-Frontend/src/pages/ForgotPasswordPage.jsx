import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authApi } from '../features/auth/authApi.js'

function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  async function submit(event) {
    event.preventDefault(); setError(''); setSubmitting(true)
    try { setMessage((await authApi.forgotPassword(email)).message) } catch (requestError) { setError(requestError.message || 'Unable to send a reset link. Please try again.') } finally { setSubmitting(false) }
  }
  return <section className="auth-page page-enter"><div className="auth-aside"><span className="brand-mark">D</span><p className="eyebrow">Account recovery</p><h1>Get back to delivery.</h1><p>We’ll send a secure, time-limited link to reset your password.</p></div><form className="auth-card" onSubmit={submit}><p className="eyebrow">Forgot password</p><h1>Reset your password</h1><p className="muted">Enter the email used for your DeliGo account.</p>{message && <p className="success">{message}</p>}{error && <p className="form-error">{error}</p>}<label>Email address<input type="email" placeholder="you@example.com" value={email} onChange={(event) => setEmail(event.target.value)} required /></label><button type="submit" disabled={submitting}>{submitting ? 'Sending…' : 'Send reset link'}</button><p className="auth-switch"><Link to="/login">← Back to sign in</Link></p></form></section>
}
export default ForgotPasswordPage
