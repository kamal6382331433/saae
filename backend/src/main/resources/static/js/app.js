const API_BASE = '/api';

// State
let currentUser = JSON.parse(localStorage.getItem('user'));
let currentRole = currentUser ? currentUser.role : null;

// DOM Elements
const loginSection = document.getElementById('login-section');
const dashboardSection = document.getElementById('dashboard-section');
const loginForm = document.getElementById('login-form');
const loginError = document.getElementById('login-error');
const toastContainer = document.getElementById('toast-container');
const sidebarMenu = document.getElementById('sidebar-menu');
const viewTitle = document.getElementById('view-title');
const viewSubtitle = document.getElementById('view-subtitle');

// Initialization
document.addEventListener('DOMContentLoaded', () => {
    if (currentUser) {
        showDashboard();
    } else {
        showLogin();
    }

    loginForm.addEventListener('submit', handleLogin);

    // Admin: Add Student Form
    document.getElementById('add-student-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const student = {
            rollNumber: document.getElementById('student-roll').value,
            department: document.getElementById('student-dept').value,
            currentSemester: document.getElementById('student-sem').value,
            user: { id: parseInt(document.getElementById('student-user-id').value) }
        };

        try {
            const res = await fetch(`${API_BASE}/admin/students`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(student)
            });
            if (res.ok) {
                closeModal('add-student-modal');
                loadViewData('students');
                e.target.reset();
                showNotice('Student enrolled successfully!', 'success');
            }
        } catch (err) {
            showNotice('Failed to enroll student', 'error');
        }
    });

    // Admin: Add Faculty Form
    document.getElementById('add-faculty-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const faculty = {
            fullName: document.getElementById('faculty-name').value,
            username: document.getElementById('faculty-username').value,
            password: document.getElementById('faculty-password').value,
            email: document.getElementById('faculty-email').value
        };

        try {
            const res = await fetch(`${API_BASE}/admin/faculty`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(faculty)
            });
            if (res.ok) {
                closeModal('add-faculty-modal');
                loadViewData('faculty');
                e.target.reset();
                showNotice('Faculty member added successfully!', 'success');
            } else {
                showNotice('Failed to add faculty: User may already exist', 'error');
            }
        } catch (err) {
            showNotice('Connection error while adding faculty', 'error');
        }
    });

    // Admin: Edit Student Form
    document.getElementById('edit-student-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('edit-student-id').value;
        const data = {
            rollNumber: document.getElementById('edit-student-roll').value,
            department: document.getElementById('edit-student-dept').value,
            currentSemester: document.getElementById('edit-student-sem').value
        };
        try {
            const res = await fetch(`${API_BASE}/admin/students/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            if (res.ok) {
                closeModal('edit-student-modal');
                loadAdminStudents();
                showNotice('Student record updated', 'success');
            }
        } catch (err) { console.error(err); }
    });

    // Admin: Edit Faculty Form
    document.getElementById('edit-faculty-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('edit-faculty-id').value;
        const data = {
            fullName: document.getElementById('edit-faculty-name').value,
            email: document.getElementById('edit-faculty-email').value
        };
        try {
            const res = await fetch(`${API_BASE}/admin/users/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            if (res.ok) {
                closeModal('edit-faculty-modal');
                loadAdminFaculty();
                showNotice('Faculty profile updated', 'success');
            }
        } catch (err) { console.error(err); }
    });

    // Admin: Add Subject Form
    document.getElementById('add-subject-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const subject = {
            subjectCode: document.getElementById('subject-code').value,
            subjectName: document.getElementById('subject-name').value,
            department: document.getElementById('subject-dept').value,
            creditHours: parseInt(document.getElementById('subject-credits').value),
            semester: document.getElementById('subject-sem').value
        };

        try {
            const res = await fetch(`${API_BASE}/admin/subjects`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(subject)
            });
            if (res.ok) {
                closeModal('add-subject-modal');
                loadViewData('subjects');
                e.target.reset();
                showNotice('Subject added to curriculum', 'success');
            }
        } catch (err) {
            showNotice('Failed to add subject', 'error');
        }
    });

    // Advanced Student Search Form (Feature 1)
    const searchForm = document.getElementById('advanced-search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = e.target.querySelector('button[type="submit"]');
            btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Searching...';
            btn.disabled = true;

            const params = new URLSearchParams();
            const minMarks = document.getElementById('search-min-marks').value;
            const subject = document.getElementById('search-subject').value;
            const status = document.getElementById('search-status').value;
            const minAvg = document.getElementById('search-min-avg').value;
            const dept = document.getElementById('search-dept').value;
            const maxAvg = document.getElementById('search-max-avg').value;

            if (minMarks) params.append('minMarks', minMarks);
            if (subject) params.append('subject', subject);
            if (status) params.append('status', status);
            if (minAvg) params.append('minAvgPercent', minAvg);
            if (dept) params.append('department', dept);
            if (maxAvg) params.append('maxAvgPercent', maxAvg);

            try {
                const res = await fetch(`${API_BASE}/search/students?${params.toString()}`);
                const students = await res.json();

                const tbody = document.querySelector('#search-results-table tbody');
                tbody.innerHTML = '';

                if (students.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">No students found matching criteria.</td></tr>';
                } else {
                    students.forEach(s => {
                        const tr = document.createElement('tr');
                        tr.innerHTML = `
                            <td style="font-family: monospace; color: var(--text-muted);">${s.rollNumber || (s.student && s.student.rollNumber ? s.student.rollNumber : 'N/A')}</td>
                            <td style="font-weight: 600;">${s.fullName || (s.user && s.user.fullName ? s.user.fullName : 'Unknown')}</td>
                            <td><span class="role-badge badge-admin">${s.department || 'N/A'}</span></td>
                        `;
                        tbody.appendChild(tr);
                    });
                }
                const resContainer = document.getElementById('search-results-container');
                if (resContainer) resContainer.classList.remove('hidden');
                showNotice(`Found ${students.length} students matching criteria`, 'success');
            } catch (err) {
                showNotice('Search request failed', 'error');
            } finally {
                btn.innerHTML = '<i class="fa-solid fa-search"></i> Search';
                btn.disabled = false;
            }
        });
    }
});

// Navigation Logic
function showLogin() {
    loginSection.classList.remove('hidden');
    dashboardSection.classList.add('hidden');
}

function showDashboard() {
    loginSection.classList.add('hidden');
    dashboardSection.classList.remove('hidden');

    // Set Profile Info
    document.getElementById('user-name-display').innerText = currentUser.fullName || currentUser.username;
    document.getElementById('user-avatar-initials').innerText = (currentUser.fullName || currentUser.username)[0].toUpperCase();

    const roleBadge = document.getElementById('user-role-badge');
    roleBadge.innerText = currentUser.role;
    roleBadge.className = `role-badge badge-${currentUser.role.toLowerCase()}`;

    generateSidebar();
    showView('overview');
}

function generateSidebar() {
    sidebarMenu.innerHTML = '';
    const role = currentUser.role.toUpperCase();

    const commonLinks = [
        { id: 'overview', icon: 'fa-house', label: 'Overview' }
    ];

    let roleLinks = [];
    if (role === 'ADMIN') {
        roleLinks = [
            { id: 'students', icon: 'fa-user-graduate', label: 'Students' },
            { id: 'faculty', icon: 'fa-chalkboard-user', label: 'Faculty' },
            { id: 'subjects', icon: 'fa-book-open', label: 'Curriculum' },
            { id: 'search', icon: 'fa-magnifying-glass', label: 'Advanced Search' },
            { id: 'analysis', icon: 'fa-chart-pie', label: 'System Analytics' }
        ];
    } else if (role === 'FACULTY') {
        roleLinks = [
            { id: 'marks', icon: 'fa-pen-to-square', label: 'Assign Marks' },
            { id: 'subjects', icon: 'fa-book', label: 'My Courses' },
            { id: 'search', icon: 'fa-magnifying-glass', label: 'Advanced Search' },
            { id: 'analysis', icon: 'fa-magnifying-glass-chart', label: 'Student Trends' }
        ];
    } else if (role === 'STUDENT') {
        roleLinks = [
            { id: 'results', icon: 'fa-medal', label: 'Performance' },
            { id: 'subjects', icon: 'fa-graduation-cap', label: 'My Subjects' }
        ];
    }

    [...commonLinks, ...roleLinks].forEach(link => {
        const li = document.createElement('li');
        li.innerHTML = `
            <a href="#" class="menu-item" id="menu-${link.id}" onclick="showView('${link.id}')">
                <i class="fa-solid ${link.icon}"></i>
                <span>${link.label}</span>
            </a>
        `;
        sidebarMenu.appendChild(li);
    });
}

function showView(viewId) {
    // 1. Hide all views
    document.querySelectorAll('.view-segment').forEach(el => el.classList.add('hidden'));
    document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));

    // 2. Show selected
    const viewEl = document.getElementById(`view-${viewId}`);
    if (viewEl) viewEl.classList.remove('hidden');

    const menuEl = document.getElementById(`menu-${viewId}`);
    if (menuEl) menuEl.classList.add('active');

    // 3. Update Titles & Load Data
    updateViewMetadata(viewId);
    loadViewData(viewId);
}

function updateViewMetadata(viewId) {
    const titles = {
        'overview': { t: 'Dashboard Overview', s: 'Quick stats and latest announcements' },
        'students': { t: 'Student Directory', s: 'Manage enrolled student profiles' },
        'faculty': { t: 'Faculty Hub', s: 'Manage academic staff and permissions' },
        'subjects': { t: 'Subject Repository', s: 'View and manage academic curriculum' },
        'marks': { t: 'Examination Portal', s: 'Entry and modification of student scores' },
        'results': { t: 'My Academic Performance', s: 'View and track your semester progress' },
        'search': { t: 'Advanced Student Search', s: 'Find students based on complex performance criteria' },
        'analysis': { t: 'Advanced Analytics', s: 'Deep dive into patterns and performance' }
    };
    viewTitle.innerText = titles[viewId].t;
    viewSubtitle.innerText = titles[viewId].s;
}

// Data Loading
async function loadViewData(viewId) {
    switch (viewId) {
        case 'overview': loadOverviewStats(); break;
        case 'students': loadAdminStudents(); break;
        case 'faculty': loadAdminFaculty(); break;
        case 'subjects': loadSubjectsByRole(); break;
        case 'marks': loadFacultyMarksPortal(); break;
        case 'results': loadStudentPerformance(); break;
        case 'search': break;
        case 'analysis': loadCrossRoleAnalysis(); break;
    }
}

async function loadOverviewStats() {
    const grid = document.getElementById('stats-grid');
    grid.innerHTML = '<div class="card animate-fade-in"><p>Loading intelligence...</p></div>';

    // Simulate API delay and role-specific stats
    setTimeout(() => {
        if (currentUser.role === 'ADMIN') {
            grid.innerHTML = `
                <div class="card stats-card" style="border-top: 4px solid #8b5cf6;"><h3>1,240</h3><p class="text-muted">Total Students</p></div>
                <div class="card stats-card" style="border-top: 4px solid #ec4899;"><h3>84</h3><p class="text-muted">Faculty Members</p></div>
                <div class="card stats-card" style="border-top: 4px solid #3b82f6;"><h3>15</h3><p class="text-muted">Departments</p></div>
            `;
        } else if (currentUser.role === 'FACULTY') {
            grid.innerHTML = `
                <div class="card stats-card" style="border-top: 4px solid #10b981;"><h3>240</h3><p class="text-muted">My Students</p></div>
                <div class="card stats-card" style="border-top: 4px solid #f59e0b;"><h3>3</h3><p class="text-muted">Active Courses</p></div>
                <div class="card stats-card" style="border-top: 4px solid #6366f1;"><h3>89%</h3><p class="text-muted">Avg Attendance</p></div>
            `;
        } else {
            grid.innerHTML = `
                <div class="card stats-card" style="border-top: 4px solid #10b981;"><h3>92%</h3><p class="text-muted">Attendance</p></div>
                <div class="card stats-card" style="border-top: 4px solid #3b82f6;"><h3>18</h3><p class="text-muted">Credits Pending</p></div>
                <div class="card stats-card" style="border-top: 4px solid #f59e0b;"><h3>5</h3><p class="text-muted">Active Courses</p></div>
            `;
        }
    }, 400);
}

async function loadAdminStudents() {
    try {
        const res = await fetch(`${API_BASE}/admin/students`);
        const students = await res.json();
        const tbody = document.querySelector('#students-table tbody');
        tbody.innerHTML = '';
        students.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${s.id}</td>
                <td style="font-family: monospace;">${s.rollNumber}</td>
                <td style="font-weight: 600;">${s.user.fullName}</td>
                <td><span class="role-badge badge-admin">${s.department}</span></td>
                <td>Sem ${s.currentSemester}</td>
                <td>
                    <div style="display: flex; gap: 0.5rem;">
                        <button class="btn btn-outline" style="padding: 0.4rem; color: var(--primary); border-color: var(--primary);" onclick="openEditStudentModal(${s.id}, '${s.rollNumber}', '${s.department}', '${s.currentSemester}')">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button class="btn btn-outline" style="padding: 0.4rem; color: #ef4444; border-color: #ef4444;" onclick="deleteStudent(${s.id})">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) { console.error(err); }
}

async function loadAdminFaculty() {
    try {
        const res = await fetch(`${API_BASE}/admin/faculty`);
        const faculty = await res.json();
        const tbody = document.querySelector('#faculty-table tbody');
        tbody.innerHTML = '';
        faculty.forEach(f => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${f.id}</td>
                <td style="font-weight: 600;">${f.fullName}</td>
                <td>${f.username}</td>
                <td>${f.email || 'N/A'}</td>
                <td>
                    <div style="display: flex; gap: 0.5rem;">
                        <button class="btn btn-outline" style="padding: 0.4rem; color: var(--primary); border-color: var(--primary);" onclick="openEditFacultyModal(${f.id}, '${f.fullName}', '${f.email || ''}')">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button class="btn btn-outline" style="padding: 0.4rem; color: #ef4444; border-color: #ef4444;" onclick="deleteUser(${f.id}, 'faculty')">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) { console.error(err); }
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student record?')) return;
    try {
        const res = await fetch(`${API_BASE}/admin/students/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showNotice('Student deleted', 'success');
            loadAdminStudents();
        }
    } catch (err) { showNotice('Delete failed', 'error'); }
}

async function deleteUser(id, type) {
    if (!confirm(`Are you sure you want to delete this ${type}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/admin/users/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showNotice(`${type.charAt(0).toUpperCase() + type.slice(1)} deleted`, 'success');
            if (type === 'faculty') loadAdminFaculty();
            else loadViewData('users');
        }
    } catch (err) { showNotice('Delete failed', 'error'); }
}

async function generateMockData() {
    try {
        showNotice('Generating databases, adding users and marks...', 'info');
        const res = await fetch(`${API_BASE}/setup/mock-data`, { method: 'POST' });
        const result = await res.json();
        if (result.success) {
            showNotice(result.message, 'success');
            loadViewData('students'); // Refresh list
        } else {
            showNotice(result.message, 'error');
        }
    } catch (err) {
        showNotice('Failed to generate mock data', 'error');
    }
}

async function loadSubjectsByRole() {
    const endpoint = currentUser.role === 'ADMIN' ? 'admin' : 'faculty';
    try {
        const res = await fetch(`${API_BASE}/${endpoint.toLowerCase()}/subjects`);
        const subjects = await res.json();
        const tbody = document.querySelector('#subjects-table tbody');
        tbody.innerHTML = '';
        subjects.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td style="font-weight: 600; color: var(--primary);">${s.subjectCode}</td>
                <td>${s.subjectName}</td>
                <td>${s.creditHours}</td>
                <td>Sem ${s.semester}</td>
                <td><span class="role-badge badge-faculty">${s.department}</span></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) { console.error(err); }
}

async function loadFacultyMarksPortal() {
    try {
        // Load Subjects for dropdown
        const subRes = await fetch(`${API_BASE}/admin/subjects`); // Faculty can see all subjects for now
        const subjects = await subRes.json();
        const select = document.getElementById('select-subject');
        select.innerHTML = '<option value="">Select Subject to Grade</option>';
        subjects.forEach(s => {
            select.innerHTML += `<option value="${s.id}">${s.subjectName} (${s.subjectCode})</option>`;
        });

        const res = await fetch(`${API_BASE}/faculty/students`);
        const students = await res.json();
        const tbody = document.querySelector('#marks-entry-table tbody');
        tbody.innerHTML = '';
        students.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div style="display:flex; align-items:center; gap: 1rem;">
                        <div class="user-avatar" style="width:35px; height:35px; font-size:0.9rem;">${s.user.fullName[0].toUpperCase()}</div>
                        <div style="font-weight: 600;">${s.user.fullName}</div>
                    </div>
                </td>
                <td style="color:var(--text-muted); font-family: monospace;">${s.rollNumber}</td>
                <td>
                    <div style="display:flex; align-items:center; gap:0.5rem;">
                        <input type="number" id="mark-input-${s.id}" class="form-input" style="width: 80px; padding: 0.5rem;" placeholder="--">
                        <span class="text-muted">/ 100</span>
                    </div>
                </td>
                <td><button class="btn btn-outline" style="padding: 0.5rem 1rem;" onclick="submitMark(${s.id})"><i class="fa-solid fa-check"></i> Post Mark</button></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) { console.error(err); }
}

async function submitMark(studentId) {
    const markValue = document.getElementById(`mark-input-${studentId}`).value;
    const subjectId = document.getElementById('select-subject').value;

    if (!subjectId) {
        showNotice('Please select a subject first', 'error');
        return;
    }

    const markData = {
        student: { id: studentId },
        subject: { id: parseInt(subjectId) },
        marksObtained: parseFloat(markValue),
        totalMarks: 100 // Default for now
    };

    try {
        const res = await fetch(`${API_BASE}/faculty/marks`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(markData)
        });
        if (res.ok) {
            showNotice('Mark updated successfully', 'success');
        }
    } catch (err) {
        showNotice('Failed to update mark', 'error');
    }
}

let studentRadarChart = null;

async function loadStudentPerformance() {
    const studentId = currentUser.id;
    try {
        const profRes = await fetch(`${API_BASE}/student/profile/${currentUser.id}`);
        if (!profRes.ok) throw new Error('Student profile not found');
        const student = await profRes.json();

        const res = await fetch(`${API_BASE}/student/results/${student.id}`);
        const results = await res.json();
        const tbody = document.querySelector('#student-results-table tbody');
        tbody.innerHTML = '';

        let totalMarks = 0;
        let subjectsList = [];
        let scoresList = [];
        let lowestScore = 100;
        let lowestSubject = '';

        results.forEach(m => {
            const perc = (m.marksObtained / m.totalMarks) * 100;
            const grade = perc >= 90 ? 'A+' : perc >= 80 ? 'A' : perc >= 70 ? 'B' : perc >= 60 ? 'C' : 'F';
            const gradeClass = perc >= 80 ? 'badge-student' : (perc >= 60 ? 'badge-faculty' : 'badge-admin');

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div style="font-weight: 600;">${m.subject.subjectName}</div>
                    <div class="text-muted" style="font-size: 0.8rem;">${m.subject.subjectCode}</div>
                </td>
                <td>${m.subject.creditHours}</td>
                <td>
                    <div style="display:flex; align-items:center; gap: 0.5rem;">
                        <span style="font-weight:600;">${m.marksObtained}</span> / ${m.totalMarks}
                        <div style="width: 50px; height: 4px; background: rgba(255,255,255,0.1); border-radius: 2px;">
                            <div style="width: ${perc}%; height: 100%; background: var(--primary); border-radius: 2px;"></div>
                        </div>
                    </div>
                </td>
                <td><span class="role-badge ${gradeClass}">${grade}</span></td>
            `;
            tbody.appendChild(tr);

            subjectsList.push(m.subject.subjectName);
            scoresList.push(perc);
            totalMarks += perc;

            if (perc < lowestScore) {
                lowestScore = perc;
                lowestSubject = m.subject.subjectName;
            }
        });

        // Check if no results
        if (results.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">No results published yet.</td></tr>';
            return;
        }

        const avg = totalMarks / results.length;
        document.getElementById('student-gpa').innerText = (avg / 10).toFixed(1);
        document.getElementById('student-credits').innerText = results.reduce((acc, curr) => acc + curr.subject.creditHours, 0);

        // Render Radar Chart
        const ctx = document.getElementById('student-radar-chart').getContext('2d');
        if (studentRadarChart) studentRadarChart.destroy();
        studentRadarChart = new Chart(ctx, {
            type: 'radar',
            data: {
                labels: subjectsList.length > 0 ? subjectsList : ['N/A'],
                datasets: [{
                    label: 'Performance %',
                    data: scoresList.length > 0 ? scoresList : [0],
                    backgroundColor: 'rgba(99, 102, 241, 0.2)',
                    borderColor: '#6366f1',
                    pointBackgroundColor: '#ec4899',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: '#ec4899'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    r: {
                        angleLines: { color: 'rgba(255, 255, 255, 0.1)' },
                        grid: { color: 'rgba(255, 255, 255, 0.1)' },
                        pointLabels: { color: '#94a3b8', font: { family: 'Outfit' } },
                        ticks: { display: false, min: 0, max: 100 }
                    }
                },
                plugins: { legend: { display: false } }
            }
        });

        // Mock Low Attendance check (e.g. 1 in 5 chance to show warning for demo purposes)
        const warnAtt = document.getElementById('attendance-warning');
        if (Math.random() < 0.3) {
            warnAtt.classList.remove('hidden');
        } else {
            warnAtt.classList.add('hidden');
        }

        // Generate Study Tips
        const tipsContainer = document.getElementById('personalized-tips');
        tipsContainer.innerHTML = '';

        let tips = [
            `Focus on consistent practice for <b>${lowestSubject || 'your core subjects'}</b>.`,
            `Your average is ${avg.toFixed(1)}%. Aim for ${(avg + 5).toFixed(1)}% next semester.`,
            `Join peer study groups for subjects you find challenging.`
        ];

        if (avg >= 85) {
            tips = [
                `Excellent performance! Consider mentoring your peers in <b>${subjectsList[0]}</b>.`,
                `Start preparing for advanced placement or competitive exams.`,
                `Maintain a balanced schedule to avoid burnout.`
            ];
        }

        tips.forEach((t, i) => {
            tipsContainer.innerHTML += `
                <div class="notice-item" style="border:none; background: rgba(255, 255, 255, 0.03); border-radius: var(--radius); padding: 0.75rem 1rem; align-items:center; gap:1rem; display:flex;">
                    <div style="background: var(--primary); width:24px; height:24px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:0.75rem; flex-shrink:0;">${i + 1}</div>
                    <span style="font-size:0.9rem;">${t}</span>
                </div>
            `;
        });

        // Feature 2: Performance Trend
        try {
            const trendRes = await fetch(`${API_BASE}/analytics/performance-trend/${student.id}`);
            if (trendRes.ok) {
                const trendData = await trendRes.json();
                document.getElementById('student-trend').innerText = trendData.trend || 'Not enough data';
            }
        } catch (err) { console.error('Error loading trend', err); }

    } catch (err) {
        console.error(err);
        document.querySelector('#student-results-table tbody').innerHTML = '<tr><td colspan="4" style="text-align:center;">Failed to load results for your profile.</td></tr>';
    }
}

async function loadCrossRoleAnalysis() {
    try {
        // Feature 4: Top performers
        const topRes = await fetch(`${API_BASE}/analytics/top-performers`);
        const topList = await topRes.json();
        const topEl = document.getElementById('top-performers-list');
        topEl.innerHTML = topList.length === 0 ? '<li class="text-muted">No data available</li>' : topList.map((s, i) => `
            <li class="notice-item" style="border-left-color: #fbbf24;">
                <div class="user-avatar" style="width: 30px; height: 30px; font-size: 0.8rem; background: #fbbf24; color: white;">#${i + 1}</div>
                <div>
                    <h4 style="margin:0">${s.fullName || 'Unknown'}</h4>
                    <p class="text-muted" style="font-size: 0.85rem;">Avg: ${s.averagePercent.toFixed(1)}% | Dept: ${s.department}</p>
                </div>
            </li>
        `).join('');

        // Feature 5: At Risk
        const riskRes = await fetch(`${API_BASE}/analytics/risk-students`);
        const riskList = await riskRes.json();
        const riskEl = document.getElementById('risk-students-list');
        riskEl.innerHTML = riskList.length === 0 ? '<li class="text-muted">No students currently at risk.</li>' : riskList.map(s => `
            <li class="notice-item" style="border-left-color: #ef4444;">
                <div class="notice-icon" style="background: #ef4444; color: white;"><i class="fa-solid fa-triangle-exclamation"></i></div>
                <div>
                    <h4 style="margin:0">${s.fullName || 'Unknown'}</h4>
                    <p class="text-muted" style="font-size: 0.85rem;">Avg: ${(s.averagePercent || 0).toFixed(1)}% | Reasons: ${(s.riskReasons || []).join(', ')}</p>
                </div>
            </li>
        `).join('');

        // Feature 6: Dept Performance
        const deptRes = await fetch(`${API_BASE}/analytics/department-performance`);
        const deptList = await deptRes.json();
        const deptEl = document.getElementById('dept-performance-list');
        deptEl.innerHTML = deptList.length === 0 ? '<li class="text-muted">No department data.</li>' : deptList.map(d => `
            <li style="display: flex; justify-content: space-between; padding: 0.75rem 0; border-bottom: 1px solid var(--border);">
                <span style="font-weight: 600;">${d.department}</span>
                <span class="role-badge badge-student">${d.averagePercent.toFixed(1)}% Avg</span>
            </li>
        `).join('');

        // Feature 3: Weak Subjects
        const weakRes = await fetch(`${API_BASE}/analytics/weak-subjects`);
        const weakList = await weakRes.json();
        const weakEl = document.getElementById('weak-subjects-list');
        weakEl.innerHTML = weakList.length === 0 ? '<li class="text-muted">No subject data.</li>' : weakList.map(w => `
            <li style="display: flex; justify-content: space-between; padding: 0.75rem 0; border-bottom: 1px solid var(--border);">
                <span>${w.subjectName}</span>
                <span style="color: #ef4444; font-weight: 600;">${w.averagePercent.toFixed(1)}%</span>
            </li>
        `).join('');

    } catch (err) {
        console.error('Failed to load analytics', err);
        showNotice('Failed to load deep analytics', 'error');
    }
}

// Auth Handlers
async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    const btn = e.target.querySelector('button');
    btn.innerText = '🛡️ Authenticating...';
    btn.disabled = true;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            const user = await res.json();
            currentUser = user;
            localStorage.setItem('user', JSON.stringify(user));
            showDashboard();
            showNotice(`Welcome back, ${user.fullName}!`, 'success');
        } else {
            const msg = await res.text();
            showNotice(msg || 'Authentication failed', 'error');
        }
    } catch (err) {
        showNotice('Connection to secure server failed', 'error');
    } finally {
        btn.innerText = 'Sign In';
        btn.disabled = false;
    }
}

function logout() {
    localStorage.removeItem('user');
    currentUser = null;
    showLogin();
    showNotice('You have been securely logged out', 'info');
}

// Modal & Utils
function openModal(id) { document.getElementById(id).classList.add('active'); }
function closeModal(id) { document.getElementById(id).classList.remove('active'); }

function showNotice(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icon = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-circle-exclamation' : 'fa-circle-info');
    toast.innerHTML = `<i class="fa-solid ${icon}"></i><span>${message}</span>`;
    toastContainer.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
}
// Helper functions for Edit Modals
function openEditStudentModal(id, roll, dept, sem) {
    document.getElementById('edit-student-id').value = id;
    document.getElementById('edit-student-roll').value = roll;
    document.getElementById('edit-student-dept').value = dept;
    document.getElementById('edit-student-sem').value = sem;
    openModal('edit-student-modal');
}

function openEditFacultyModal(id, name, email) {
    document.getElementById('edit-faculty-id').value = id;
    document.getElementById('edit-faculty-name').value = name;
    document.getElementById('edit-faculty-email').value = email;
    openModal('edit-faculty-modal');
}
