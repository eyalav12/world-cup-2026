import type { ReactNode } from "react";
import { formatPredictionPct } from "@/lib/predictions";
import { cn } from "@/lib/utils";

export function OutcomeBar({
  label,
  pct,
  icon,
  compact = false,
  className,
}: {
  label: string;
  pct: number;
  icon?: ReactNode;
  compact?: boolean;
  className?: string;
}) {
  const clamped = Math.min(100, Math.max(0, pct));

  return (
    <div className={cn("flex items-center gap-3", className)}>
      {icon ? <div className="shrink-0">{icon}</div> : null}
      <div className="min-w-0 flex-1">
        <div className="mb-1 flex items-center justify-between gap-2">
          <span
            className={cn(
              "truncate font-medium text-white",
              compact ? "text-sm" : "text-base",
            )}
          >
            {label}
          </span>
          <span className="shrink-0 tabular-nums text-sm font-semibold text-emerald-300">
            {formatPredictionPct(clamped)}
          </span>
        </div>
        <div className="h-2 overflow-hidden rounded-full bg-white/10">
          <div
            className="h-full rounded-full bg-emerald-500 transition-all"
            style={{ width: `${clamped}%` }}
          />
        </div>
      </div>
    </div>
  );
}
