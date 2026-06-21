import { useEffect, useMemo, useState } from "react";

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

function App() {
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [authMode, setAuthMode] = useState("login");
  const [fullName, setFullName] = useState("Ratan Kumar");
  const [email, setEmail] = useState("ratan@example.com");
  const [password, setPassword] = useState("password123");
  const [doctors, setDoctors] = useState([]);
  const [slots, setSlots] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [logs, setLogs] = useState([]);
  const [selectedDate, setSelectedDate] = useState("2026-07-01");
  const [selectedDoctor, setSelectedDoctor] = useState("");
  const [selectedAppointmentId, setSelectedAppointmentId] = useState(null);
  const [status, setStatus] = useState("Ready");
  const [busy, setBusy] = useState(false);

  const authHeaders = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`
  };

  const stats = useMemo(() => {
    const booked = appointments.filter((item) => item.status === "BOOKED").length;
    const sent = appointments.filter((item) => item.processingStatus === "NOTIFICATION_SENT").length;
    return { booked, sent, total: appointments.length, slots: slots.length };
  }, [appointments, slots]);

  async function request(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, options);
    const data = await response.json().catch(() => null);

    if (!response.ok) {
      throw new Error(data?.messages?.join(", ") || "Request failed");
    }

    return data;
  }

  async function run(label, task) {
    setBusy(true);
    setStatus(label);

    try {
      await task();
    } catch (error) {
      setStatus(error.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleAuth(event) {
    event.preventDefault();

    await run(authMode === "login" ? "Logging in..." : "Creating account...", async () => {
      const body = authMode === "login" ? { email, password } : { fullName, email, password };

      const data = await request(`/api/auth/${authMode}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      localStorage.setItem("token", data.token);
      setToken(data.token);
      setStatus(data.message);
    });
  }

  async function loadDoctors() {
    await run("Fetching doctors...", async () => {
      const data = await request("/api/doctors", { headers: authHeaders });
      setDoctors(data);
      setStatus("Doctors loaded");
    });
  }

  async function loadSlots() {
    await run("Fetching available slots...", async () => {
      const query = new URLSearchParams();
      if (selectedDate) query.set("date", selectedDate);
      if (selectedDoctor) query.set("doctorId", selectedDoctor);

      const data = await request(`/api/slots/available?${query.toString()}`, {
        headers: authHeaders
      });

      setSlots(data);
      setStatus(`${data.length} available slots found`);
    });
  }

  async function bookSlot(slotId) {
    await run("Booking appointment...", async () => {
      await request("/api/appointments", {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({ slotId })
      });

      setStatus("Appointment booked. Notification event is being processed.");
      await refreshAppointmentsAndSlots();
    });
  }

  async function refreshAppointmentsAndSlots() {
    const appointmentsData = await request("/api/appointments/my", { headers: authHeaders });
    setAppointments(appointmentsData);

    const query = new URLSearchParams();
    if (selectedDate) query.set("date", selectedDate);
    if (selectedDoctor) query.set("doctorId", selectedDoctor);

    const slotsData = await request(`/api/slots/available?${query.toString()}`, {
      headers: authHeaders
    });
    setSlots(slotsData);
  }

  async function loadAppointments() {
    await run("Fetching appointment history...", async () => {
      const data = await request("/api/appointments/my", { headers: authHeaders });
      setAppointments(data);
      setStatus("Appointment history loaded");
    });
  }

  async function cancelAppointment(id) {
    await run("Cancelling appointment...", async () => {
      await request(`/api/appointments/${id}/cancel`, {
        method: "PATCH",
        headers: authHeaders,
        body: JSON.stringify({ reason: "Cancelled from UI" })
      });

      setStatus("Appointment cancelled. Notification event is being processed.");
      await loadAppointments();
    });
  }

  async function loadLogs(id) {
    await run("Fetching appointment logs...", async () => {
      const data = await request(`/api/appointments/${id}/logs`, { headers: authHeaders });
      setSelectedAppointmentId(id);
      setLogs(data);
      setStatus("Logs loaded");
    });
  }

  function logout() {
    localStorage.removeItem("token");
    setToken("");
    setDoctors([]);
    setSlots([]);
    setAppointments([]);
    setLogs([]);
    setSelectedAppointmentId(null);
    setStatus("Logged out");
  }

  useEffect(() => {
    if (token) {
      loadDoctors();
      loadAppointments();
    }
  }, [token]);

  return (
    <main className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">Healthcare appointment platform</p>
          <h1>MyKare Appointments</h1>
          <p className="hero-copy">
            Book doctor slots, track appointment history, and watch notification processing status from one workflow.
          </p>
        </div>

        {token ? (
          <button className="ghost-button" onClick={logout}>Logout</button>
        ) : (
          <span className="pill">Secure JWT login</span>
        )}
      </header>

      <section className={`status ${busy ? "loading" : ""}`}>
        <span>{busy ? "Working" : "Status"}</span>
        <strong>{status}</strong>
      </section>

      {!token ? (
        <section className="auth-layout">
          <div className="auth-copy">
            <h2>Start with a patient account</h2>
            <p>
              Register once, receive a JWT token, then use protected APIs for doctors, slots, booking, history, and logs.
            </p>
            <div className="mini-steps">
              <span>Register</span>
              <span>Login</span>
              <span>Book</span>
              <span>Track</span>
            </div>
          </div>

          <form className="auth-card" onSubmit={handleAuth}>
            <div className="tabs">
              <button type="button" className={authMode === "login" ? "active" : ""} onClick={() => setAuthMode("login")}>
                Login
              </button>
              <button type="button" className={authMode === "register" ? "active" : ""} onClick={() => setAuthMode("register")}>
                Register
              </button>
            </div>

            {authMode === "register" && (
              <label>
                Full name
                <input value={fullName} onChange={(e) => setFullName(e.target.value)} />
              </label>
            )}

            <label>
              Email
              <input value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>

            <label>
              Password
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            </label>

            <button type="submit" disabled={busy}>
              {authMode === "login" ? "Login" : "Create account"}
            </button>
          </form>
        </section>
      ) : (
        <>
          <section className="stats">
            <Stat label="Available slots" value={stats.slots} />
            <Stat label="Total appointments" value={stats.total} />
            <Stat label="Active bookings" value={stats.booked} />
            <Stat label="Notifications sent" value={stats.sent} />
          </section>

          <section className="dashboard">
            <div className="panel slots-panel">
              <div className="panel-header">
                <div>
                  <p className="eyebrow">Discovery</p>
                  <h2>Available Slots</h2>
                </div>
                <button onClick={loadSlots} disabled={busy}>Fetch Slots</button>
              </div>

              <div className="filters">
                <label>
                  Date
                  <input type="date" value={selectedDate} onChange={(e) => setSelectedDate(e.target.value)} />
                </label>

                <label>
                  Doctor
                  <select value={selectedDoctor} onChange={(e) => setSelectedDoctor(e.target.value)}>
                    <option value="">All doctors</option>
                    {doctors.map((doctor) => (
                      <option key={doctor.id} value={doctor.id}>{doctor.fullName}</option>
                    ))}
                  </select>
                </label>
              </div>

              <div className="list">
                {slots.length === 0 ? (
                  <EmptyState title="No slots loaded" text="Choose a date and fetch available slots." />
                ) : (
                  slots.map((slot) => (
                    <article key={slot.slotId} className="slot-card">
                      <div>
                        <strong>{slot.doctorName}</strong>
                        <span>{slot.specialization}</span>
                      </div>
                      <div className="time-block">
                        <span>{formatDate(slot.slotStart)}</span>
                        <strong>{formatTime(slot.slotStart)} - {formatTime(slot.slotEnd)}</strong>
                      </div>
                      <button onClick={() => bookSlot(slot.slotId)} disabled={busy}>Book</button>
                    </article>
                  ))
                )}
              </div>
            </div>

            <div className="panel">
              <div className="panel-header">
                <div>
                  <p className="eyebrow">History</p>
                  <h2>My Appointments</h2>
                </div>
                <button onClick={loadAppointments} disabled={busy}>Refresh</button>
              </div>

              <div className="list">
                {appointments.length === 0 ? (
                  <EmptyState title="No appointments yet" text="Book a slot to see your appointment history." />
                ) : (
                  appointments.map((appointment) => (
                    <article key={appointment.appointmentId} className="appointment-card">
                      <div>
                        <strong>{appointment.doctorName}</strong>
                        <span>{formatDate(appointment.slotStart)} at {formatTime(appointment.slotStart)}</span>
                      </div>

                      <span className={`badge ${appointment.processingStatus.toLowerCase()}`}>
                        {appointment.status} / {appointment.processingStatus}
                      </span>

                      <div className="actions">
                        <button onClick={() => loadLogs(appointment.appointmentId)}>Logs</button>
                        {appointment.status === "BOOKED" && (
                          <button className="danger-button" onClick={() => cancelAppointment(appointment.appointmentId)}>
                            Cancel
                          </button>
                        )}
                      </div>
                    </article>
                  ))
                )}
              </div>
            </div>

            <div className="panel logs-panel">
              <div className="panel-header">
                <div>
                  <p className="eyebrow">Processing</p>
                  <h2>Appointment Logs</h2>
                </div>
                {selectedAppointmentId && <span className="pill">Appointment #{selectedAppointmentId}</span>}
              </div>

              <div className="timeline">
                {logs.length === 0 ? (
                  <EmptyState title="No logs selected" text="Open logs from any appointment to inspect its lifecycle." />
                ) : (
                  logs.map((log) => (
                    <article key={log.id} className="timeline-item">
                      <span className="timeline-dot" />
                      <div>
                        <strong>{log.eventType}</strong>
                        <p>{log.message}</p>
                        <small>{formatDate(log.createdAt)}</small>
                      </div>
                    </article>
                  ))
                )}
              </div>
            </div>
          </section>
        </>
      )}
    </main>
  );
}

function Stat({ label, value }) {
  return (
    <article className="stat-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function EmptyState({ title, text }) {
  return (
    <div className="empty-state">
      <strong>{title}</strong>
      <span>{text}</span>
    </div>
  );
}

function formatDate(value) {
  return new Date(value).toLocaleDateString(undefined, {
    day: "2-digit",
    month: "short",
    year: "numeric"
  });
}

function formatTime(value) {
  return new Date(value).toLocaleTimeString(undefined, {
    hour: "2-digit",
    minute: "2-digit"
  });
}

export default App;