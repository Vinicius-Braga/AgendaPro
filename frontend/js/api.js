// Camada de comunicação com a API. Isolada em um arquivo próprio para que,
// se um dia a base URL ou a forma de autenticar mudar, só se mexa aqui.

const API_BASE_URL = "http://localhost:8080";

function getToken() {
  return localStorage.getItem("token");
}

function setToken(token) {
  localStorage.setItem("token", token);
}

function clearToken() {
  localStorage.removeItem("token");
}

// Decodifica o payload do JWT só para exibir informação (ex.: e-mail) na UI.
// NÃO valida assinatura — isso é responsabilidade exclusiva do backend.
function decodeJwtPayload(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

// Wrapper único para toda chamada HTTP. Centraliza:
// - header de autenticação
// - parse do corpo de erro padronizado (ApiError do GlobalExceptionHandler)
async function request(path, { method = "GET", body } = {}) {
  const headers = { "Content-Type": "application/json" };

  const token = getToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  // 204 No Content ou corpo vazio (ex.: DELETE) não tem JSON para parsear.
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    // ApiError: { status, message, timestamp, fields }
    const message = data?.message || "Erro inesperado na API";
    const error = new Error(message);
    error.status = response.status;
    error.fields = data?.fields;
    throw error;
  }

  return data;
}

const api = {
  // ---- Auth ----
  register: (name, email, password) =>
    request("/auth/register", { method: "POST", body: { name, email, password } }),

  login: (email, password) =>
    request("/auth/login", { method: "POST", body: { email, password } }),

  // ---- Clients ----
  listClients: () => request("/clients"),
  createClient: (name, phone) =>
    request("/clients", { method: "POST", body: { name, phone } }),
  deleteClient: (id) => request(`/clients/${id}`, { method: "DELETE" }),

  // ---- Services ----
  listServices: () => request("/services"),
  createService: (name, price, durationMinutes) =>
    request("/services", { method: "POST", body: { name, price, durationMinutes } }),

  // ---- Appointments ----
  listDayAgenda: (date) => request(`/appointments/day?date=${date}`),
  listPendingAppointments: () => request("/appointments/pending"),
  createAppointment: ({ clientId, serviceId, dateTime, price, notes }) =>
    request("/appointments", {
      method: "POST",
      body: { clientId, serviceId, dateTime, price, notes },
    }),
  startAppointment: (id) => request(`/appointments/${id}/start`, { method: "PATCH" }),
  completeAppointment: (id) => request(`/appointments/${id}/complete`, { method: "PATCH" }),
  cancelAppointment: (id) => request(`/appointments/${id}/cancel`, { method: "PATCH" }),
  payAppointment: (id) => request(`/appointments/${id}/pay`, { method: "PATCH" }),
};
