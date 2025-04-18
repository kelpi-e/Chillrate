const users = [
  { name: 'Стасян', relax: 75, updated: '17.04.2025, 10:23', team: 'Команда A' },
  { name: 'Димас', relax: 50, updated: '17.04.2025, 10:19', team: 'Команда A' },
  { name: 'Артем', relax: 20, updated: '17.04.2025, 10:19', team: 'Команда Б' },
  { name: 'Пашок', relax: 90, updated: '17.04.2025, 10:19', team: 'Команда Б' }
];

const teamSelect = document.getElementById('teamSelect');
const searchInput = document.getElementById('searchInput');

function render() {
  const tbody = document.getElementById('userTable');
  const cards = document.getElementById('userCards');
  tbody.innerHTML = '';
  cards.innerHTML = '';
  const selectedTeam = teamSelect.value;
  const filtered = users
    .filter(u => (selectedTeam === 'Все команды' || u.team === selectedTeam))
    .filter(u => u.name.toLowerCase().includes(searchInput.value.toLowerCase()));
  let sum = 0;
  filtered.forEach(u => {
    sum += u.relax;
    // Таблица
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${u.name}</td>
      <td>${u.team}</td>
      <td><div class="progress"><div class="progress-bar" style="width:${u.relax}%;">${u.relax}%</div></div></td>
      <td>${u.updated}</td>
      <td><a href="#">Открыть</a></td>
    `;
    tbody.appendChild(tr);
    // Карточки
    const card = document.createElement('div'); card.className = 'card';
    card.innerHTML = `
      <div class="card-header">
        <div>
          <span>${u.name}</span>
          <span class="team-label">${u.team}</span>
        </div>
        <span>${u.updated}</span>
      </div>
      <div class="progress"><div class="progress-bar" style="width:${u.relax}%;">${u.relax}%</div></div>
      <a href="#">Детали</a>
    `;
    cards.appendChild(card);
  });
  const avg = filtered.length ? (sum / filtered.length).toFixed(0) + '%' : '—';
  document.getElementById('avgRelax').textContent = avg;
  document.getElementById('totalUsers').textContent = filtered.length;
}

teamSelect.addEventListener('change', render);
searchInput.addEventListener('input', render);
render();