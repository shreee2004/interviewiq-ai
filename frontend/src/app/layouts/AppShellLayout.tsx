import { NavLink, Outlet } from 'react-router'
import { cn } from '@/lib/utils'

/** Authenticated shell: sidebar + topbar + routed content — docs/UI_WIREFRAMES.md §2.2. */
const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/resume', label: 'Resume' },
  { to: '/interview/new', label: 'Interview' },
  { to: '/analytics', label: 'Analytics' },
  { to: '/learning', label: 'Learning' },
  { to: '/profile', label: 'Profile' },
]

export function AppShellLayout() {
  return (
    <div className="flex min-h-screen">
      <aside className="hidden w-56 shrink-0 border-r border-border bg-card md:block">
        <div className="flex h-14 items-center border-b border-border px-4 text-sm font-semibold">
          InterviewIQ AI
        </div>
        <nav className="flex flex-col gap-0.5 p-2">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  'rounded-md px-3 py-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground',
                  isActive && 'bg-muted font-medium text-foreground',
                )
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <div className="flex flex-1 flex-col">
        <header className="flex h-14 items-center justify-between border-b border-border px-4">
          <div className="text-sm text-muted-foreground">⌘K to search</div>
          <div className="h-8 w-8 rounded-full bg-muted" aria-label="User menu" />
        </header>
        <main className="flex-1 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
