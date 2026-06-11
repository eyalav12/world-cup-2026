export function ErrorBanner({ message }: { message: string }) {
  return (
    <div
      role="alert"
      className="rounded-xl border border-amber-400/30 bg-amber-500/10 px-4 py-3 text-sm text-amber-100"
    >
      {message}
    </div>
  );
}
