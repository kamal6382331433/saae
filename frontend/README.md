# SAAE Frontend

## Overview
Static web frontend for the **Students Academic Analysis Engine** (SAAE).  
Built with vanilla HTML, CSS, and JavaScript — no build tools required.

## File Structure
```
frontend/
├── index.html          # Main single-page application
├── css/
│   └── style.css       # Complete stylesheet (glassmorphism dark theme)
└── js/
    └── app.js          # Application logic (API calls, routing, UI)
```

## Features
- **Login System** — Role-based authentication (Admin, Faculty, Student)
- **Admin Portal** — Manage students, faculty, subjects, mock data
- **Faculty Portal** — Assign marks, view courses, advanced search
- **Student Portal** — View results, performance radar chart, study tips
- **Analytics Dashboard** — Top performers, at-risk students, department stats

## API Base URL
All API calls use the relative path `/api` by default.  
To change it, edit `const API_BASE` in `js/app.js` (line 1).

## External Dependencies (CDN)
- [Font Awesome 6.4.0](https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css)
- [Chart.js 4.4.1](https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js)
- [Google Fonts](https://fonts.googleapis.com/) — Inter & Outfit

## How to Run
### Option 1: Served by Spring Boot backend
Place contents into `backend/src/main/resources/static/` and run the backend.

### Option 2: Standalone with a simple HTTP server
```bash
# Python
python -m http.server 3000

# Node.js
npx serve .
```
> ⚠️ API calls will fail unless a backend is running at the same origin or CORS is configured.
