import { createBrowserRouter } from 'react-router'
import { PublicLayout } from '@/app/layouts/PublicLayout'
import { AppShellLayout } from '@/app/layouts/AppShellLayout'
import { PlaceholderPage } from '@/app/placeholder-page'
import { NotFoundPage } from '@/app/not-found-page'
import { ErrorPage } from '@/app/error-page'

// Route table mirrors docs/UI_WIREFRAMES.md §1. Every screen is a placeholder
// until Phase 3 implements it — the shell (layouts, nav, providers) is what
// this phase proves out.
export const router = createBrowserRouter([
  {
    element: <PublicLayout />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: '/',
        element: (
          <PlaceholderPage
            title="Practice Smarter. Interview Better."
            description="Landing page — hero, features, testimonials, pricing."
          />
        ),
      },
      {
        path: '/login',
        element: <PlaceholderPage title="Log in" description="Email + Google OAuth login." />,
      },
      {
        path: '/register',
        element: <PlaceholderPage title="Sign up" description="Create your InterviewIQ AI account." />,
      },
    ],
  },
  {
    element: <AppShellLayout />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: '/dashboard',
        element: (
          <PlaceholderPage
            title="Dashboard"
            description="Streak, scores, skill radar, recent interviews, leaderboard."
          />
        ),
      },
      {
        path: '/resume',
        element: (
          <PlaceholderPage title="Resume" description="Upload, parse, and get AI analysis of your resume." />
        ),
      },
      {
        path: '/interview/new',
        element: (
          <PlaceholderPage
            title="Start an interview"
            description="Pick a role, company, difficulty, and format."
          />
        ),
      },
      {
        path: '/interview/:id/session',
        element: (
          <PlaceholderPage title="Live interview" description="The real-time question/answer flow." />
        ),
      },
      {
        path: '/interview/:id/coding/:turnId',
        element: <PlaceholderPage title="Coding round" description="Monaco editor + test results." />,
      },
      {
        path: '/interview/:id/system-design/:turnId',
        element: (
          <PlaceholderPage title="System design round" description="Whiteboard + AI architecture review." />
        ),
      },
      {
        path: '/interview/:id/report',
        element: (
          <PlaceholderPage title="Interview report" description="Scores, AI summary, skill gap roadmap." />
        ),
      },
      {
        path: '/analytics',
        element: (
          <PlaceholderPage title="Analytics" description="Trends, heatmap, skill progress over time." />
        ),
      },
      {
        path: '/learning',
        element: (
          <PlaceholderPage title="Learning hub" description="AI-recommended resources for your skill gaps." />
        ),
      },
      {
        path: '/profile',
        element: (
          <PlaceholderPage title="Profile" description="Personal info, achievements, preferences." />
        ),
      },
      {
        path: '/admin',
        element: (
          <PlaceholderPage title="Admin" description="User management, API usage, feature flags." />
        ),
      },
    ],
  },
  {
    path: '*',
    element: <NotFoundPage />,
  },
])
