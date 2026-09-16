import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { orderApi } from '../features/orders/orderApi.js'

const initial = { pickupAddress: '', deliveryAddress: '', packageDescription: '', packageWeight: '', receiverName: '', receiverPhone: '', deliveryNotes: '' }
function CreateOrderPage() {
 const [form,setForm]=useState(initial); const [error,setError]=useState(''); const [saving,setSaving]=useState(false); const navigate=useNavigate()
 async function submit(event){event.preventDefault();setSaving(true);setError('');try { const order=await orderApi.create({...form,packageWeight:Number(form.packageWeight)}); navigate(`/orders/${order.id}`,{state:{message:'Delivery created successfully.'}}) } catch(e){setError(e.message)} finally {setSaving(false)} }
 return <section className="form-page"><div className="page-heading"><div><p className="eyebrow">New delivery</p><h1>Create an order</h1></div></div><form className="delivery-form" onSubmit={submit}>{error&&<p className="form-error">{error}</p>}<Field label="Pickup address" field="pickupAddress" form={form} setForm={setForm} textarea/><Field label="Delivery address" field="deliveryAddress" form={form} setForm={setForm} textarea/><Field label="Package description" field="packageDescription" form={form} setForm={setForm} textarea/><Field label="Package weight (kg)" field="packageWeight" type="number" form={form} setForm={setForm}/><Field label="Receiver name" field="receiverName" form={form} setForm={setForm}/><Field label="Receiver phone" field="receiverPhone" type="tel" form={form} setForm={setForm}/><Field label="Delivery notes" field="deliveryNotes" form={form} setForm={setForm} textarea required={false}/><button disabled={saving}>{saving?'Creating…':'Create delivery'}</button></form></section>
}
function Field({label,field,form,setForm,textarea=false,type='text',required=true}) { const props={value:form[field],onChange:e=>setForm({...form,[field]:e.target.value}),required}; return <label>{label}{textarea?<textarea {...props}/>:<input type={type} min={type==='number'?'0.01':undefined} step={type==='number'?'0.01':undefined} {...props}/>}</label> }
export default CreateOrderPage
