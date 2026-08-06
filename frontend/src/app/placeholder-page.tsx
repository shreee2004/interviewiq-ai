/**
 * Temporary stand-in for every screen in docs/UI_WIREFRAMES.md until Phase 3
 * implements each one. Proves the routing shell end-to-end without faking
 * finished features — every route below renders this until it's replaced.
 */
export function PlaceholderPage({ title, description }: { title: string; description: string }) {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center gap-2 text-center">
      <h1 className="text-2xl font-semibold text-foreground">{title}</h1>
      <p className="max-w-md text-sm text-muted-foreground">{description}</p>
    </div>
  )
}
