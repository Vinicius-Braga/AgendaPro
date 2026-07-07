// Estado em memória da sessão atual (recarregado a cada troca de aba/ação).
const state = {
  clients: [],
  services: [],
};

// ===================== Helpers de UI =====================

function showToast(message, type = "success") {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.className = `toast ${type}`;
  setTimeout(() => toast.classList.add("hidden"), 3500);
}

// Toda chamada de API passa por aqui: se der erro, mostra a mensagem que o
// GlobalExceptionHandler do backend já formatou (ApiError.message), incluindo
// os detalhes de validação por campo quando existem.
async function callApi(promise, successMessage) {
  try {
    const result = await promise;
    if (successMessage) showToast(successMessage, "success");
    return result;
  } catch (error) {
    const fieldErrors = error.fields
      ? " " + Object.values(error.fields).join(" ")
      : "";
    showToast(error.message + fieldErrors, "error");
    throw error;
  }
}

function formatDate(isoString) {
  if (!isoString) return "";
  const date = new Date(isoString);
  return date.toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatMoney(value) {
  return Number(value).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

const STATUS_LABELS = {
  SCHEDULED: "Agendado",
  IN_PROGRESS: "Em atendimento",
  DONE: "Finalizado",
  CANCELED: "Cancelado",
};

const PAYMENT_LABELS = {
  PENDING: "Pagamento pendente",
  PAID: "Pago",
};

function statusBadge(status) {
  return `<span class="badge badge-${status.toLowerCase()}">${STATUS_LABELS[status] || status}</span>`;
}

function paymentBadge(paymentStatus) {
  return `<span class="badge badge-${paymentStatus.toLowerCase()}">${PAYMENT_LABELS[paymentStatus] || paymentStatus}</span>`;
}

// ===================== Autenticação =====================

function isLoggedIn() {
  return Boolean(getToken());
}

function showAuthScreen() {
  document.getElementById("auth-screen").classList.remove("hidden");
  document.getElementById("app").classList.add("hidden");
}

function showApp() {
  document.getElementById("auth-screen").classList.add("hidden");
  document.getElementById("app").classList.remove("hidden");

  const payload = decodeJwtPayload(getToken());
  document.getElementById("user-email").textContent = payload?.sub || "";

  loadClients();
  loadServices();
  loadDayAgenda(todayIsoDate());
}

document.getElementById("login-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = new FormData(e.target);

  try {
    const { token } = await callApi(api.login(form.get("email"), form.get("password")));
    setToken(token);
    showApp();
  } catch {
    // erro já exibido pelo callApi
  }
});

document.getElementById("register-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = new FormData(e.target);

  try {
    await callApi(
      api.register(form.get("name"), form.get("email"), form.get("password")),
      "Conta criada! Agora faça login."
    );
    e.target.reset();
    switchAuthTab("login-form");
  } catch {
    // erro já exibido pelo callApi
  }
});

document.getElementById("logout-btn").addEventListener("click", () => {
  clearToken();
  showAuthScreen();
});

function switchAuthTab(formId) {
  document.querySelectorAll(".tab-btn").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.tab === formId);
  });
  document.getElementById("login-form").classList.toggle("hidden", formId !== "login-form");
  document.getElementById("register-form").classList.toggle("hidden", formId !== "register-form");
}

document.querySelectorAll(".tab-btn").forEach((btn) => {
  btn.addEventListener("click", () => switchAuthTab(btn.dataset.tab));
});

// ===================== Navegação entre telas do app =====================

document.querySelectorAll(".nav-tab").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".nav-tab").forEach((b) => b.classList.remove("active"));
    document.querySelectorAll(".view").forEach((v) => v.classList.add("hidden"));

    btn.classList.add("active");
    document.getElementById(btn.dataset.view).classList.remove("hidden");
  });
});

// ===================== Clientes =====================

async function loadClients() {
  try {
    state.clients = await callApi(api.listClients());
    renderClients();
    fillClientOptions();
  } catch {
    // erro já exibido
  }
}

function renderClients() {
  const container = document.getElementById("clients-list");

  if (state.clients.length === 0) {
    container.innerHTML = `<p class="empty-state">Nenhum cliente cadastrado ainda.</p>`;
    return;
  }

  container.innerHTML = state.clients
    .map(
      (client) => `
      <div class="list-item">
        <div class="list-item-main">
          <span class="list-item-title">${client.name}</span>
          <span class="list-item-sub">${client.phone || "sem telefone"}</span>
        </div>
        <div class="list-item-actions">
          <button class="btn btn-danger btn-sm" data-delete-client="${client.id}">Remover</button>
        </div>
      </div>`
    )
    .join("");
}

document.getElementById("client-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = new FormData(e.target);

  try {
    await callApi(api.createClient(form.get("name"), form.get("phone")), "Cliente adicionado!");
    e.target.reset();
    loadClients();
  } catch {
    // erro já exibido
  }
});

document.getElementById("clients-list").addEventListener("click", async (e) => {
  const id = e.target.dataset.deleteClient;
  if (!id) return;

  try {
    await callApi(api.deleteClient(id), "Cliente removido.");
    loadClients();
  } catch {
    // erro já exibido
  }
});

// ===================== Serviços =====================

async function loadServices() {
  try {
    state.services = await callApi(api.listServices());
    renderServices();
    fillServiceOptions();
  } catch {
    // erro já exibido
  }
}

function renderServices() {
  const container = document.getElementById("services-list");

  if (state.services.length === 0) {
    container.innerHTML = `<p class="empty-state">Nenhum serviço cadastrado ainda.</p>`;
    return;
  }

  container.innerHTML = state.services
    .map(
      (service) => `
      <div class="list-item">
        <div class="list-item-main">
          <span class="list-item-title">${service.name}</span>
          <span class="list-item-sub">${formatMoney(service.price)} · ${service.durationMinutes} min</span>
        </div>
      </div>`
    )
    .join("");
}

document.getElementById("service-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = new FormData(e.target);

  try {
    await callApi(
      api.createService(form.get("name"), Number(form.get("price")), Number(form.get("durationMinutes"))),
      "Serviço adicionado!"
    );
    e.target.reset();
    loadServices();
  } catch {
    // erro já exibido
  }
});

// ===================== Agendamentos =====================

function todayIsoDate() {
  return new Date().toISOString().slice(0, 10);
}

function fillClientOptions() {
  const select = document.querySelector('#appointment-form select[name="clientId"]');
  const currentValue = select.value;
  select.innerHTML =
    `<option value="" disabled selected>Cliente</option>` +
    state.clients.map((c) => `<option value="${c.id}">${c.name}</option>`).join("");
  select.value = currentValue;
}

function fillServiceOptions() {
  const select = document.querySelector('#appointment-form select[name="serviceId"]');
  const currentValue = select.value;
  select.innerHTML =
    `<option value="" disabled selected>Serviço</option>` +
    state.services.map((s) => `<option value="${s.id}">${s.name} — ${formatMoney(s.price)}</option>`).join("");
  select.value = currentValue;
}

// Quando o usuário escolhe um serviço, sugerimos o preço dele — mas o campo
// continua editável (o valor cobrado pode variar do preço-tabela).
document.querySelector('#appointment-form select[name="serviceId"]').addEventListener("change", (e) => {
  const service = state.services.find((s) => s.id === e.target.value);
  if (service) {
    document.querySelector('#appointment-form input[name="price"]').value = service.price;
  }
});

document.getElementById("appointment-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = new FormData(e.target);

  try {
    await callApi(
      api.createAppointment({
        clientId: form.get("clientId"),
        serviceId: form.get("serviceId"),
        dateTime: form.get("dateTime"),
        price: Number(form.get("price")),
        notes: form.get("notes") || null,
      }),
      "Agendamento criado!"
    );
    e.target.reset();
    loadDayAgenda(document.getElementById("agenda-date").value || todayIsoDate());
  } catch {
    // erro já exibido
  }
});

async function loadDayAgenda(date) {
  document.getElementById("agenda-date").value = date;
  try {
    const appointments = await callApi(api.listDayAgenda(date));
    renderAppointments(appointments, `Nenhum agendamento em ${date.split("-").reverse().join("/")}.`);
  } catch {
    // erro já exibido
  }
}

document.getElementById("btn-view-day").addEventListener("click", () => {
  const date = document.getElementById("agenda-date").value || todayIsoDate();
  loadDayAgenda(date);
});

document.getElementById("btn-view-pending").addEventListener("click", async () => {
  try {
    const appointments = await callApi(api.listPendingAppointments());
    renderAppointments(appointments, "Nenhum agendamento com pagamento pendente.");
  } catch {
    // erro já exibido
  }
});

function renderAppointments(appointments, emptyMessage) {
  const container = document.getElementById("appointments-list");

  if (appointments.length === 0) {
    container.innerHTML = `<p class="empty-state">${emptyMessage}</p>`;
    return;
  }

  container.innerHTML = appointments
    .map((appointment) => {
      const actions = [];

      if (appointment.status === "SCHEDULED") {
        actions.push(`<button class="btn btn-secondary btn-sm" data-action="start" data-id="${appointment.id}">Iniciar</button>`);
      }
      if (appointment.status === "IN_PROGRESS") {
        actions.push(`<button class="btn btn-secondary btn-sm" data-action="complete" data-id="${appointment.id}">Concluir</button>`);
      }
      if (appointment.status === "SCHEDULED" || appointment.status === "IN_PROGRESS") {
        actions.push(`<button class="btn btn-danger btn-sm" data-action="cancel" data-id="${appointment.id}">Cancelar</button>`);
      }
      if (appointment.paymentStatus === "PENDING") {
        actions.push(`<button class="btn btn-primary btn-sm" data-action="pay" data-id="${appointment.id}">Marcar como pago</button>`);
      }

      return `
      <div class="list-item">
        <div class="list-item-main">
          <span class="list-item-title">${appointment.client?.name || "Cliente"} · ${appointment.service?.name || "Serviço"}</span>
          <span class="list-item-sub">${formatDate(appointment.dateTime)} · ${formatMoney(appointment.price)}</span>
          <div>${statusBadge(appointment.status)} ${paymentBadge(appointment.paymentStatus)}</div>
        </div>
        <div class="list-item-actions">${actions.join("")}</div>
      </div>`;
    })
    .join("");
}

const APPOINTMENT_ACTIONS = {
  start: api.startAppointment,
  complete: api.completeAppointment,
  cancel: api.cancelAppointment,
  pay: api.payAppointment,
};

const APPOINTMENT_ACTION_MESSAGES = {
  start: "Atendimento iniciado.",
  complete: "Atendimento concluído.",
  cancel: "Agendamento cancelado.",
  pay: "Pagamento confirmado.",
};

document.getElementById("appointments-list").addEventListener("click", async (e) => {
  const action = e.target.dataset.action;
  const id = e.target.dataset.id;
  if (!action || !id) return;

  try {
    await callApi(APPOINTMENT_ACTIONS[action](id), APPOINTMENT_ACTION_MESSAGES[action]);
    loadDayAgenda(document.getElementById("agenda-date").value || todayIsoDate());
  } catch {
    // erro já exibido
  }
});

// ===================== Inicialização =====================

if (isLoggedIn()) {
  showApp();
} else {
  showAuthScreen();
}
