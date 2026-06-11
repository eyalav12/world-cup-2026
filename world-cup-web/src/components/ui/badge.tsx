import { cn } from "@/lib/utils";

const variants = {
  default: "bg-white/10 text-emerald-100",
  live: "bg-red-500/20 text-red-300 ring-1 ring-red-400/40",
  upcoming: "bg-emerald-500/20 text-emerald-200",
  finished: "bg-zinc-500/20 text-zinc-300",
} as const;

export function Badge({
  variant = "default",
  className,
  children,
}: {
  variant?: keyof typeof variants;
  className?: string;
  children: React.ReactNode;
}) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
        variants[variant],
        className,
      )}
    >
      {children}
    </span>
  );
}
