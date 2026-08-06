import { Link, useRouteError } from 'react-router'

export function ErrorPage() {
  const error = useRouteError()
  const message = error instanceof Error ? error.message : 'Something went wrong.'

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 text-center">
      <p className="text-sm font-medium text-destructive">Error</p>
      <h1 className="text-2xl font-semibold text-foreground">This page hit a problem</h1>
      <p className="max-w-md text-sm text-muted-foreground">{message}</p>
      <Link to="/" className="text-sm text-primary underline-offset-4 hover:underline">
        Back to home
      </Link>
    </div>
  )
}
