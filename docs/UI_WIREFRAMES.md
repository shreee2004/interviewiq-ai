# InterviewIQ AI — UI Wireframes & Component Hierarchy

Design language: Stripe/Linear/Vercel-style minimal enterprise UI — rounded
cards, soft shadows, generous whitespace, dark mode as a first-class citizen
(not an afterthought). Wireframes below are layout-level (ASCII), not visual
— visual design tokens (color, type scale, spacing) are defined separately
when Tailwind config is scaffolded.

## 1. Screen inventory

| # | Screen | Route |
|---|---|---|
| 1 | Landing | `/` |
| 2 | Login / Register | `/login`, `/register` |
| 3 | Dashboard | `/dashboard` |
| 4 | Resume | `/resume` |
| 5 | Interview Setup | `/interview/new` |
| 6 | Live Interview Session | `/interview/:id/session` |
| 7 | Coding Round | `/interview/:id/coding/:turnId` |
| 8 | System Design Round | `/interview/:id/system-design/:turnId` |
| 9 | Interview Report | `/interview/:id/report` |
| 10 | Analytics | `/analytics` |
| 11 | Learning Hub | `/learning` |
| 12 | Profile | `/profile` |
| 13 | Admin | `/admin/*` |

## 2. Key wireframes

### 2.1 Landing (`/`)

```
┌────────────────────────────────────────────────────────┐
│ Logo   Product  Pricing  FAQ            [Login] [Sign Up]│  ← sticky nav, glass on scroll
├────────────────────────────────────────────────────────┤
│         Animated gradient hero background                │
│     "Practice Smarter. Interview Better."                 │
│     Subheadline + [Start Free Interview →]                │
│     Interactive product illustration / mock UI            │
├────────────────────────────────────────────────────────┤
│  Stat strip:  50k+ interviews · 4.9★ · 30+ roles          │
├────────────────────────────────────────────────────────┤
│  Feature cards (3-col grid, icon + title + copy)          │
├────────────────────────────────────────────────────────┤
│  Product showcase (tabbed screenshots: Dashboard/         │
│  Interview/Report)                                        │
├────────────────────────────────────────────────────────┤
│  Testimonials carousel                                    │
├────────────────────────────────────────────────────────┤
│  Pricing (3-tier cards, "Most popular" highlight)          │
├────────────────────────────────────────────────────────┤
│  FAQ accordion                                             │
├────────────────────────────────────────────────────────┤
│  CTA banner  →  Footer (links, socials, legal)             │
└────────────────────────────────────────────────────────┘
```

### 2.2 Dashboard (`/dashboard`)

```
┌──────────┬─────────────────────────────────────────────┐
│          │  Topbar: search / cmd-k · theme toggle · avatar│
│  Sidebar │──────────────────────────────────────────────│
│  nav     │  "Welcome back, {name}"   [+ New Interview]    │
│          │──────────────────────────────────────────────│
│ Dashboard│ ┌─────────┬─────────┬─────────┬─────────┐     │
│ Resume   │ │Streak   │Avg Score│Total    │Resume   │     │  ← stat tiles
│ Interview│ │🔥 7 days│  78%    │Interviews│ Score  │     │
│ Reports  │ │         │         │  24     │  82     │     │
│ Analytics│ └─────────┴─────────┴─────────┴─────────┘     │
│ Learning │ ┌───────────────────┬────────────────────┐    │
│ Profile  │ │ Skill Radar Chart │ Weekly Progress     │    │
│          │ │                   │ (line/bar)          │    │
│          │ └───────────────────┴────────────────────┘    │
│          │ ┌───────────────────┬────────────────────┐    │
│          │ │ Recent Interviews │ Leaderboard /       │    │
│          │ │ (list, click→rpt) │ Achievements        │    │
│          │ └───────────────────┴────────────────────┘    │
└──────────┴─────────────────────────────────────────────┘
```

### 2.3 Interview Setup (`/interview/new`)

```
┌────────────────────────────────────────────────────────┐
│  Step indicator: (1) Role → (2) Company → (3) Format →   │
│                  (4) Difficulty → (5) Review              │
│                                                            │
│  Role grid (cards): Java Backend | Frontend | DevOps |    │
│  Data Science | Cybersecurity | Cloud | AI/ML | ...        │
│                                                            │
│  Company select (optional): Google, Amazon, Meta, ...     │
│  or "General"                                              │
│                                                            │
│  Interview type: Technical / Coding / System Design / HR / │
│  Mixed  ·  Difficulty: Easy/Medium/Hard  ·  Duration       │
│  Programming language (if coding involved)                 │
│                                                            │
│  Summary card + [Start Interview →]                         │
└────────────────────────────────────────────────────────┘
```

### 2.4 Live Interview Session (`/interview/:id/session`) — the core screen

```
┌────────────────────────────────────────────────────────┐
│  ⏱ 12:34 elapsed   Question 3 of ~8        [End Session] │
├───────────────────────────────┬──────────────────────────┤
│  AI interviewer panel          │  Live signal panel         │
│  (avatar / waveform when       │  · Confidence  ▓▓▓▓▓░░ 71  │
│  speaking)                     │  · Speaking pace  132 wpm  │
│                                 │  · Filler words   3        │
│  "Can you explain how you'd    │  · Response time  0:42     │
│  design a rate limiter for     │                            │
│  a public API?"                │  (placeholder: eye contact  │
│                                 │   score — webcam future)   │
├───────────────────────────────┴──────────────────────────┤
│  Answer input                                              │
│  [ 🎤 Voice ]  [ ⌨ Text ]     transcript / textarea         │
│  live transcript rendering while speaking                  │
│                                                              │
│                              [Submit Answer →]               │
├────────────────────────────────────────────────────────┤
│  Transcript timeline (collapsed strip, scroll up for history)│
└────────────────────────────────────────────────────────┘
```

Post-answer, an inline `EVALUATION` toast/panel briefly shows the per-answer
scores before the next question streams in — keeps the "real interviewer"
feel without breaking flow.

### 2.5 Interview Report (`/interview/:id/report`)

```
┌────────────────────────────────────────────────────────┐
│  Overall Score  84/100        Company Readiness  Strong  │
│  [Download PDF]  [Share]                                  │
├───────────────────────────────┬──────────────────────────┤
│  Radar chart (Technical /      │  Score breakdown bars      │
│  Communication / Behavioral /  │  (Technical, Comm,         │
│  Confidence / Problem Solving) │  Behavioral, Confidence)   │
├───────────────────────────────┴──────────────────────────┤
│  AI Summary (prose)                                        │
├────────────────────────────────────────────────────────┤
│  Question-by-question feedback (expandable list)           │
├────────────────────────────────────────────────────────┤
│  Skill gap analysis   →   Recommended roadmap → Learning Hub│
└────────────────────────────────────────────────────────┘
```

## 3. Frontend component hierarchy

Mirrors the `features/` folder from `docs/ARCHITECTURE.md` §4.

```
<App>
├── <AppProviders>            (QueryClientProvider, ThemeProvider, AuthProvider, Toaster)
│   └── <RouterProvider>
│       ├── <PublicLayout>
│       │   ├── <LandingPage>
│       │   │   ├── <Hero> / <FeatureGrid> / <ProductShowcase>
│       │   │   ├── <TestimonialCarousel> / <PricingSection> / <FaqAccordion>
│       │   │   └── <Footer>
│       │   └── <AuthPages>
│       │       ├── <LoginForm> (react-hook-form + zod)
│       │       ├── <RegisterForm>
│       │       └── <GoogleOAuthButton>
│       │
│       └── <AppShellLayout>            (authenticated)
│           ├── <Sidebar> (nav, collapsible)
│           ├── <Topbar> (<CommandPalette>, <ThemeToggle>, <UserMenu>)
│           │
│           ├── <DashboardPage>
│           │   ├── <StatTile> × n
│           │   ├── <SkillRadarChart> (Recharts)
│           │   ├── <WeeklyProgressChart>
│           │   ├── <RecentInterviewsList>
│           │   └── <LeaderboardCard>
│           │
│           ├── <ResumePage>
│           │   ├── <ResumeUploadDropzone>
│           │   ├── <ParsedResumeView> (skills/education/experience/projects)
│           │   └── <ResumeAnalysisPanel> (score, ATS, suggestions)
│           │
│           ├── <InterviewSetupWizard>
│           │   ├── <RoleStep> / <CompanyStep> / <FormatStep> / <ReviewStep>
│           │
│           ├── <InterviewSessionPage>
│           │   ├── <InterviewerPanel> (question display, avatar/waveform)
│           │   ├── <LiveSignalPanel> (confidence/pace/filler/response-time)
│           │   ├── <AnswerInput>
│           │   │   ├── <VoiceCapture> (Web Speech API wrapper)
│           │   │   └── <TextAnswerField>
│           │   ├── <TranscriptTimeline>
│           │   └── useInterviewSocket() hook — WS connection + message reducer
│           │
│           ├── <CodingRoundPage>
│           │   ├── <MonacoEditorPane>
│           │   ├── <TestCaseResultsPanel>
│           │   └── <AiHintPanel>
│           │
│           ├── <SystemDesignRoundPage>
│           │   ├── <WhiteboardCanvas> (draw/upload)
│           │   └── <AiArchitectureReviewPanel>
│           │
│           ├── <InterviewReportPage>
│           │   ├── <ScoreRadarChart> / <ScoreBreakdownBars>
│           │   ├── <AiSummaryCard>
│           │   ├── <QuestionFeedbackList>
│           │   └── <SkillGapRoadmap>
│           │
│           ├── <AnalyticsPage>
│           │   ├── <TrendLineChart> / <ActivityHeatmap> / <SkillTrendChart>
│           │
│           ├── <LearningHubPage>
│           │   └── <RecommendationCard> × n (grouped by skill gap)
│           │
│           ├── <ProfilePage>
│           │   ├── <ProfileForm> / <PreferencesForm> / <AchievementsGrid>
│           │
│           └── <AdminLayout>
│               ├── <UserManagementTable>
│               ├── <ApiUsageDashboard>
│               └── <FeatureFlagsPanel>
```

Shared design-system primitives (shadcn-based, in `components/`):
`<Button>`, `<Card>`, `<Dialog>`, `<Sheet>`, `<Skeleton>`, `<Badge>`,
`<Tooltip>`, `<Tabs>`, `<Toast>`, `<DataTable>`, `<EmptyState>`,
`<CommandPalette>` (cmdk-based, `⌘K`).

## 4. Interaction notes

- **Command palette (`⌘K`)**: jump to any screen, start a new interview,
  toggle theme — implemented once in `app/`, not per-feature.
- **Loading**: every data-fetching screen has a matching `<Skeleton>`
  layout, not a spinner — perceived-performance matters more here than
  anywhere else in the app.
- **Empty states**: dashboard/analytics/reports all need a designed empty
  state for first-time users (no interviews yet) with a CTA into setup —
  never a bare "no data" message.
- **Dark/light**: every chart, badge, and gradient must be re-checked in
  both themes — charts especially (Recharts theme tokens, not hardcoded hex).
