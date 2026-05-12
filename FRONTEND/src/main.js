import './style.css'

// Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://atm-7pj3.onrender.com/api/atm';
console.log('Using API Base URL:', API_BASE_URL);

// State management
let state = {
  accountNumber: '',
  user: null,
  currentModal: '' // 'deposit' or 'withdraw'
};

// DOM Elements
const sections = {
  login: document.getElementById('login-section'),
  register: document.getElementById('register-section'),
  otp: document.getElementById('otp-section'),
  dashboard: document.getElementById('dashboard-section'),
  modal: document.getElementById('modal-section')
};

const elements = {
  accNumber: document.getElementById('acc-number'),
  pin: document.getElementById('pin'),
  loginBtn: document.getElementById('login-btn'),

  otpCode: document.getElementById('otp-code'),
  verifyBtn: document.getElementById('verify-btn'),

  userDisplay: document.getElementById('user-display'),
  balanceDisplay: document.getElementById('balance-display'),
  logoutBtn: document.getElementById('logout-btn'),

  showDeposit: document.getElementById('show-deposit'),
  showWithdraw: document.getElementById('show-withdraw'),

  modalTitle: document.getElementById('modal-title'),
  amountInput: document.getElementById('amount'),
  processBtn: document.getElementById('process-btn'),

  backToDash: document.getElementById('back-to-dash'),
  backToLogin: document.getElementById('back-to-login'),

  transactionsList: document.getElementById('transactions-list'),

  // Registration elements
  regName: document.getElementById('reg-name'),
  regEmail: document.getElementById('reg-email'),
  regAcc: document.getElementById('reg-acc'),
  regPin: document.getElementById('reg-pin'),
  registerBtn: document.getElementById('register-btn'),
  showRegister: document.getElementById('show-register'),
  showLogin: document.getElementById('show-login')
};

// Toast System
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;

  const icon = type === 'success' ? '✅' : '❌';
  toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 500);
  }, 4000);
}

// Helper: Show specific section
function showSection(sectionName) {
  Object.values(sections).forEach(s => s.classList.add('hidden'));
  sections[sectionName].classList.remove('hidden');
}

// Helper: Loading state
function setLoading(btn, isLoading) {
  if (isLoading) {
    btn.disabled = true;
    btn.dataset.originalText = btn.innerHTML;
    btn.innerHTML = `<span class="loader"></span> Processing...`;
  } else {
    btn.disabled = false;
    btn.innerHTML = btn.dataset.originalText || 'Confirm';
  }
}

// API Calls
async function handleLogin() {
  const accountNumber = elements.accNumber.value;
  const pin = elements.pin.value;

  if (!accountNumber || !pin) {
    showToast('Please fill all fields', 'error');
    return;
  }

  setLoading(elements.loginBtn, true);
  try {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accountNumber, pin })
    });

    if (response.ok) {
      const data = await response.json();
      state.accountNumber = accountNumber;
      // Note: user object will be fetched AFTER OTP verification
      
      showToast(data.message || 'Credentials verified! Check your email for OTP.');
      showSection('otp');
    } else {
      const error = await response.text();
      showToast(error || 'Invalid credentials', 'error');
    }
  } catch (err) {
    showToast('Backend connection failed. Please check if the server is running.', 'error');
  } finally {
    setLoading(elements.loginBtn, false);
  }
}

async function handleRegister() {
  const userName = elements.regName.value;
  const email = elements.regEmail.value;
  const accountNumber = elements.regAcc.value;
  const pin = elements.regPin.value;

  if (!userName || !email || !accountNumber || !pin) {
    showToast('Please fill all fields', 'error');
    return;
  }

  setLoading(elements.registerBtn, true);
  try {
    const response = await fetch(`${API_BASE_URL}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userName, email, accountNumber, pin })
    });

    const result = await response.text();
    if (response.ok) {
      showToast('Registration successful! You can now login.');
      setTimeout(() => showSection('login'), 1500);
    } else {
      showToast(result || 'Registration failed', 'error');
    }
  } catch (err) {
    showToast('Backend connection failed', 'error');
  } finally {
    setLoading(elements.registerBtn, false);
  }
}

async function sendOTP() {
  try {
    await fetch(`${API_BASE_URL}/otp/send`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accountNumber: state.accountNumber })
    });
  } catch (err) {
    console.error('Failed to send OTP', err);
  }
}

async function handleVerifyOTP() {
  const otp = elements.otpCode.value;
  if (!otp) return;

  setLoading(elements.verifyBtn, true);
  try {
    const response = await fetch(`${API_BASE_URL}/otp/verify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accountNumber: state.accountNumber, otp })
    });

    if (response.ok) {
      showToast('Identity verified successfully!');
      await fetchBalance();
      await fetchTransactions();
      showSection('dashboard');
    } else {
      showToast('Invalid or expired OTP', 'error');
    }
  } catch (err) {
    showToast('Verification failed', 'error');
  } finally {
    setLoading(elements.verifyBtn, false);
  }
}

async function fetchBalance() {
  try {
    const response = await fetch(`${API_BASE_URL}/balance?accountNumber=${state.accountNumber}`);
    if (response.ok) {
      const data = await response.json();
      elements.balanceDisplay.innerText = `$${parseFloat(data.balance).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
      elements.userDisplay.innerText = `Account: ${data.accountNumber}`;
    }
  } catch (err) {
    console.error('Failed to fetch balance', err);
  }
}

async function fetchTransactions() {
  try {
    const response = await fetch(`${API_BASE_URL}/transactions?accountNumber=${state.accountNumber}`);
    if (response.ok) {
      const transactions = await response.json();
      if (transactions.length === 0) {
        elements.transactionsList.innerHTML = '<p class="empty-msg">No recent transactions</p>';
        return;
      }

      elements.transactionsList.innerHTML = transactions.map(t => `
        <div class="transaction-item">
          <div class="tx-info">
            <span class="tx-type ${t.transactionType.toLowerCase()}">${t.transactionType}</span>
            <span class="tx-date">${new Date(t.transactionTime).toLocaleString()}</span>
          </div>
          <div class="tx-amount ${t.transactionType === 'DEPOSIT' ? 'plus' : 'minus'}">
            ${t.transactionType === 'DEPOSIT' ? '+' : '-'}$${parseFloat(t.amount).toLocaleString()}
          </div>
        </div>
      `).join('');
    }
  } catch (err) {
    console.error('Failed to fetch transactions', err);
  }
}

async function handleTransaction() {
  const amount = parseFloat(elements.amountInput.value);
  if (isNaN(amount) || amount <= 0) {
    showToast('Enter a valid amount', 'error');
    return;
  }

  const endpoint = state.currentModal === 'deposit' ? 'deposit' : 'withdraw';
  setLoading(elements.processBtn, true);

  try {
    const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accountNumber: state.accountNumber, amount })
    });

    const result = await response.text();
    if (response.ok) {
      showToast(result);
      await fetchBalance();
      await fetchTransactions();
      setTimeout(() => showSection('dashboard'), 1500);
    } else {
      showToast(result, 'error');
    }
  } catch (err) {
    showToast('Transaction failed', 'error');
  } finally {
    setLoading(elements.processBtn, false);
  }
}

// Event Listeners
elements.loginBtn.addEventListener('click', handleLogin);
elements.verifyBtn.addEventListener('click', handleVerifyOTP);

elements.showDeposit.addEventListener('click', () => {
  state.currentModal = 'deposit';
  elements.modalTitle.innerText = 'Deposit Funds';
  elements.amountInput.value = '';
  showSection('modal');
});

elements.showWithdraw.addEventListener('click', () => {
  state.currentModal = 'withdraw';
  elements.modalTitle.innerText = 'Withdraw Cash';
  elements.amountInput.value = '';
  showSection('modal');
});

elements.backToDash.addEventListener('click', () => showSection('dashboard'));
elements.backToLogin.addEventListener('click', () => showSection('login'));

elements.showRegister.addEventListener('click', () => showSection('register'));
elements.showLogin.addEventListener('click', () => showSection('login'));
elements.registerBtn.addEventListener('click', handleRegister);

elements.processBtn.addEventListener('click', handleTransaction);

elements.logoutBtn.addEventListener('click', () => {
  state.user = null;
  state.accountNumber = '';
  elements.accNumber.value = '';
  elements.pin.value = '';
  showSection('login');
  showToast('Logged out safely');
});

// Allow Enter key to submit
[elements.accNumber, elements.pin].forEach(el => {
  el.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleLogin(); });
});
elements.otpCode.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleVerifyOTP(); });
elements.amountInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleTransaction(); });
