import './style.css'
import api from './api'

// State management
let state = {
  accountNumber: '',
  userName: '',
  token: '', // JWT token
  user: null,
  currentModal: '' // 'deposit', 'withdraw', or 'transfer'
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
  resendOtp: document.getElementById('resend-otp'),

  userDisplay: document.getElementById('user-display'),
  balanceDisplay: document.getElementById('balance-display'),
  logoutBtn: document.getElementById('logout-btn'),

  showDeposit: document.getElementById('show-deposit'),
  showWithdraw: document.getElementById('show-withdraw'),
  showTransfer: document.getElementById('show-transfer'),

  modalTitle: document.getElementById('modal-title'),
  recipientGroup: document.getElementById('recipient-group'),
  recipientAcc: document.getElementById('recipient-acc'),
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

// Validation Helpers
function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validatePin(pin) {
  return /^\d{4}$/.test(pin);
}

function validateAccountNumber(acc) {
  return /^\d{10}$/.test(acc);
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
    const response = await api.post('/login', { accountNumber, pin });
    state.accountNumber = accountNumber;
    showToast(response.data.message || 'Credentials verified! Check your email for OTP.');
    showSection('otp');
  } catch (err) {
    showToast(err.message || 'Invalid credentials', 'error');
  } finally {
    setLoading(elements.loginBtn, false);
  }
}

async function handleRegister() {
  const userName = elements.regName.value.trim();
  const email = elements.regEmail.value.trim();
  const accountNumber = elements.regAcc.value.trim();
  const pin = elements.regPin.value.trim();

  // Input Validation
  if (!userName || !email || !accountNumber || !pin) {
    showToast('Please fill all fields', 'error');
    return;
  }
  if (!validateEmail(email)) {
    showToast('Please enter a valid email address', 'error');
    return;
  }
  if (!validateAccountNumber(accountNumber)) {
    showToast('Account number must be exactly 10 digits', 'error');
    return;
  }
  if (!validatePin(pin)) {
    showToast('PIN must be exactly 4 digits', 'error');
    return;
  }

  setLoading(elements.registerBtn, true);
  try {
    await api.post('/register', { userName, email, accountNumber, pin });
    showToast('Registration successful! You can now login.');
    setTimeout(() => showSection('login'), 1500);
  } catch (err) {
    showToast(err.message || 'Registration failed', 'error');
  } finally {
    setLoading(elements.registerBtn, false);
  }
}

async function handleResendOTP() {
  if (!state.accountNumber) return;
  
  try {
    await api.post('/otp/send', { accountNumber: state.accountNumber });
    showToast('New OTP sent to your email');
  } catch (err) {
    showToast(err.message || 'Failed to resend OTP', 'error');
  }
}

async function handleVerifyOTP() {
  const otp = elements.otpCode.value;
  if (!otp) return;

  setLoading(elements.verifyBtn, true);
  try {
    const response = await api.post('/otp/verify', { accountNumber: state.accountNumber, otp });
    const token = response.data.token;
    state.token = token;
    localStorage.setItem('atm_token', token); // Save for Axios interceptor
    
    showToast('Identity verified successfully!');
    await fetchBalance();
    await fetchTransactions();
    showSection('dashboard');
  } catch (err) {
    showToast(err.message || 'Invalid or expired OTP', 'error');
  } finally {
    setLoading(elements.verifyBtn, false);
  }
}

async function fetchBalance() {
  try {
    const response = await api.get(`/balance?accountNumber=${state.accountNumber}`);
    const data = response.data;
    state.userName = data.userName;
    elements.balanceDisplay.innerText = `$${parseFloat(data.balance).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
    elements.userDisplay.innerHTML = `Welcome, <strong>${data.userName}</strong> <br> <span style="font-size: 0.8rem; opacity: 0.8">Acc: ${data.accountNumber}</span>`;
  } catch (err) {
    if (err.status === 401) {
      showToast('Session expired. Please login again.', 'error');
      handleLogout();
    }
  }
}

async function fetchTransactions() {
  try {
    const response = await api.get(`/transactions?accountNumber=${state.accountNumber}`);
    const transactions = response.data;
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
  } catch (err) {
    console.error('Failed to fetch transactions', err);
  }
}

async function handleTransaction() {
  const endpoint = state.currentModal;
  const amount = parseFloat(elements.amountInput.value);
  
  if (isNaN(amount) || amount <= 0) {
    showToast('Enter a valid amount', 'error');
    return;
  }

  setLoading(elements.processBtn, true);

  try {
    let payload = { accountNumber: state.accountNumber, amount };
    let url = `/${endpoint}`;

    if (endpoint === 'transfer') {
      const recipientAcc = elements.recipientAcc.value.trim();
      if (!recipientAcc || recipientAcc.length !== 10) {
        showToast('Enter a valid 10-digit recipient account', 'error');
        setLoading(elements.processBtn, false);
        return;
      }
      payload = {
        fromAccountNumber: state.accountNumber,
        toAccountNumber: recipientAcc,
        amount
      };
    }

    const message = await api.post(url, payload);
    showToast(message.data);
    await fetchBalance();
    await fetchTransactions();
    setTimeout(() => showSection('dashboard'), 1500);
  } catch (err) {
    showToast(err.message || 'Transaction failed', 'error');
  } finally {
    setLoading(elements.processBtn, false);
  }
}

function handleLogout() {
  state.user = null;
  state.accountNumber = '';
  state.token = '';
  localStorage.removeItem('atm_token');
  elements.accNumber.value = '';
  elements.pin.value = '';
  showSection('login');
  showToast('Logged out safely');
}

// Event Listeners
elements.loginBtn.addEventListener('click', handleLogin);
elements.verifyBtn.addEventListener('click', handleVerifyOTP);

elements.showDeposit.addEventListener('click', () => {
  state.currentModal = 'deposit';
  elements.modalTitle.innerText = 'Deposit Funds';
  elements.recipientGroup.classList.add('hidden');
  elements.amountInput.value = '';
  showSection('modal');
});

elements.showWithdraw.addEventListener('click', () => {
  state.currentModal = 'withdraw';
  elements.modalTitle.innerText = 'Withdraw Cash';
  elements.recipientGroup.classList.add('hidden');
  elements.amountInput.value = '';
  showSection('modal');
});

elements.showTransfer.addEventListener('click', () => {
  state.currentModal = 'transfer';
  elements.modalTitle.innerText = 'Transfer Funds';
  elements.recipientGroup.classList.remove('hidden');
  elements.recipientAcc.value = '';
  elements.amountInput.value = '';
  showSection('modal');
});

elements.backToDash.addEventListener('click', () => showSection('dashboard'));
elements.backToLogin.addEventListener('click', () => showSection('login'));

elements.showRegister.addEventListener('click', () => showSection('register'));
elements.showLogin.addEventListener('click', () => showSection('login'));
elements.registerBtn.addEventListener('click', handleRegister);
elements.resendOtp.addEventListener('click', handleResendOTP);

elements.processBtn.addEventListener('click', handleTransaction);
elements.logoutBtn.addEventListener('click', handleLogout);

// Allow Enter key to submit
[elements.accNumber, elements.pin].forEach(el => {
  el.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleLogin(); });
});
elements.otpCode.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleVerifyOTP(); });
elements.amountInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleTransaction(); });
