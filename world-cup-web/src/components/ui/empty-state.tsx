export function EmptyState({
  title,
  description,
}: {
  title: string;
  description?: string;
}) {
  return (
    <div className="rounded-2xl border border-dashed border-white/15 bg-white/5 px-6 py-12 text-center">
      <p className="text-lg font-medium text-white">{title}</p>
      {description ? (
        <p className="mt-2 text-sm text-emerald-100/60">{description}</p>
      ) : null}
    </div>
  );
}
