import { useCallback, useEffect, useState } from 'react'
import { adminApi } from '../features/admin/adminApi.js'
import { orderApi } from '../features/orders/orderApi.js'

function AdminPage() {
  const [data, setData] = useState(null)
  const [users, setUsers] = useState([])
  const [orders, setOrders] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const [dashboard, userList, orderList] = await Promise.all([
        adminApi.dashboard(),
        adminApi.users(),
        orderApi.list(),
      ])

      setData(dashboard)
      setUsers(userList)
      setOrders(orderList)
    } catch (e) {
      setError(e?.message || 'Unable to load admin data')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    let cancelled = false

    async function loadAdminData() {
      setLoading(true)
      setError('')

      try {
        const [dashboard, userList, orderList] = await Promise.all([
          adminApi.dashboard(),
          adminApi.users(),
          orderApi.list(),
        ])

        if (!cancelled) {
          setData(dashboard)
          setUsers(userList)
          setOrders(orderList)
        }
      } catch (e) {
        if (!cancelled) {
          setError(e?.message || 'Unable to load admin data')
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadAdminData()

    return () => {
      cancelled = true
    }
  }, [])

  async function role(id, selectedRole) {
    try {
      setError('')
      await adminApi.updateRole(id, selectedRole)
      await load()
    } catch (e) {
      setError(e?.message || 'Unable to update user role')
    }
  }

  async function assign(id, partnerId) {
    if (!partnerId) return

    try {
      setError('')
      await orderApi.assign(id, partnerId)
      await load()
    } catch (e) {
      setError(e?.message || 'Unable to assign delivery partner')
    }
  }

  const partners = users.filter(
    (user) => user.role === 'DELIVERY_PARTNER'
  )

  return (
    <section>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Administration</p>
          <h1>Operations dashboard</h1>
        </div>

        <button type="button" onClick={load} disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh data'}
        </button>
      </div>

      {error && <p className="form-error">{error}</p>}

      {data && (
        <div className="stat-grid">
          <article>
            <span>Users</span>
            <strong>{data.totalUsers}</strong>
          </article>

          <article>
            <span>Orders</span>
            <strong>{data.totalOrders}</strong>
          </article>

          <article>
            <span>In transit</span>
            <strong>{data.inTransitOrders}</strong>
          </article>
        </div>
      )}

      <h2>Orders</h2>

      <div className="user-list">
        {orders.length > 0 ? (
          orders.map((order) => (
            <article key={order.id}>
              <div>
                <strong>{order.orderNumber}</strong>
                <p>
                  {order.status} · {order.customerName}
                </p>
              </div>

              <select
                value={order.deliveryPartnerId || ''}
                onChange={(e) => assign(order.id, e.target.value)}
              >
                <option value="">Assign partner</option>

                {partners.map((partner) => (
                  <option value={partner.id} key={partner.id}>
                    {partner.fullName}
                  </option>
                ))}
              </select>
            </article>
          ))
        ) : (
          <p>No orders found.</p>
        )}
      </div>

      <h2>Users</h2>

      <div className="user-list">
        {users.length > 0 ? (
          users.map((user) => (
            <article key={user.id}>
              <div>
                <strong>{user.fullName}</strong>
                <p>{user.email}</p>
              </div>

              <select
                value={user.role}
                onChange={(e) => role(user.id, e.target.value)}
              >
                {['CUSTOMER', 'DELIVERY_PARTNER', 'ADMIN'].map(
                  (userRole) => (
                    <option value={userRole} key={userRole}>
                      {userRole}
                    </option>
                  )
                )}
              </select>
            </article>
          ))
        ) : (
          <p>No users found.</p>
        )}
      </div>
    </section>
  )
}

export default AdminPage