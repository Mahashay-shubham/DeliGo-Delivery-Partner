import { useAuth } from '../features/auth/useAuth.js'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiClient } from '../api/client.js'

function DashboardPage() {
  const { user } = useAuth()
  const [data, setData] = useState({ totalOrders: 0, pendingOrders: 0, deliveredOrders: 0, recentOrders: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  useEffect(() => { apiClient('/dashboard').then(setData).catch((requestError) => setError(requestError.message)).finally(() => setLoading(false)) }, [])
  if (user.role === 'ADMIN') return <AdminPageLink />
  return (
    <section className="dashboard page-enter">
      <div className="hero-panel">
        <div>
          <p className="eyebrow">Delivery command centre</p>
          <h1>Good to see you, {user.fullName.split(' ')[0]}.</h1>
          <p>Everything you need to send, follow and manage a delivery—without the busywork.</p>
        </div>
        <Link className="button-link button-light" to="/orders/create">+ New delivery</Link>
      </div>
      {error && <p className="form-error">{error}</p>}
      <div className="stat-grid">
        <article><span className="stat-icon">↗</span><span>Total deliveries</span><strong>{loading ? '—' : data.totalOrders}</strong></article>
        <article><span className="stat-icon amber">◷</span><span>Active now</span><strong>{loading ? '—' : data.pendingOrders}</strong></article>
        <article><span className="stat-icon green">✓</span><span>Delivered</span><strong>{loading ? '—' : data.deliveredOrders}</strong></article>
      </div>
      <section className="content-card recent-card">
        <div className="section-title"><div><p className="eyebrow">Activity</p><h2>{data.recentOrders.length ? 'Recent deliveries' : 'Ready when you are'}</h2></div><Link to="/orders">View all</Link></div>
        {data.recentOrders.map((order) => <Link className="recent-order" key={order.id} to={`/orders/${order.id}`}>{order.orderNumber} <span>{order.status}</span></Link>)}
        {!loading && !data.recentOrders.length && <div className="empty-state"><span>▣</span><p>Create your first delivery and its live status will appear here.</p><Link to="/orders/create">Create delivery</Link></div>}
      </section>
    </section>
  )
}

function AdminPageLink() { return <section className="content-card empty-state page-enter"><span>◈</span><h1>Administrator dashboard</h1><p>Manage customers, delivery partners and live operations from one place.</p><Link className="button-link" to="/admin">Open admin dashboard</Link></section> }

export default DashboardPage
