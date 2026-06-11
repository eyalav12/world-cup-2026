"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";

const TABS = [
  { href: "/", label: "Home" },
  { href: "/matches", label: "Matches" },
  { href: "/groups", label: "Groups" },
  { href: "/markets", label: "Markets" },
  { href: "/teams", label: "Teams" },
] as const;

export function MobileBottomNav() {
  const pathname = usePathname();

  return (
    <nav
      aria-label="Primary"
      className="fixed inset-x-0 bottom-0 z-40 border-t border-white/10 bg-[#0b1f14]/95 backdrop-blur-md lg:hidden"
      style={{ paddingBottom: "env(safe-area-inset-bottom)" }}
    >
      <div className="mx-auto grid max-w-lg grid-cols-5">
        {TABS.map((tab) => {
          const active =
            tab.href === "/"
              ? pathname === "/"
              : pathname.startsWith(tab.href);
          return (
            <Link
              key={tab.href}
              href={tab.href}
              className={cn(
                "flex min-h-[3.25rem] flex-col items-center justify-center gap-0.5 px-1 py-2 text-[10px] font-medium leading-none",
                active
                  ? "text-emerald-400"
                  : "text-emerald-100/60 active:text-emerald-300",
              )}
            >
              <TabIcon href={tab.href} active={active} />
              <span>{tab.label}</span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}

function TabIcon({ href, active }: { href: string; active: boolean }) {
  const className = cn("h-5 w-5", active ? "stroke-emerald-400" : "stroke-current");

  switch (href) {
    case "/":
      return (
        <svg viewBox="0 0 24 24" fill="none" strokeWidth="1.75" className={className} aria-hidden>
          <path d="M4 10.5 12 4l8 6.5V20a1 1 0 0 1-1 1h-5v-6H10v6H5a1 1 0 0 1-1-1v-9.5Z" />
        </svg>
      );
    case "/matches":
      return (
        <svg viewBox="0 0 24 24" fill="none" strokeWidth="1.75" className={className} aria-hidden>
          <circle cx="12" cy="12" r="9" />
          <path d="M12 3a12 12 0 0 1 0 18M12 3a12 12 0 0 0 0 18M3 12h18" />
        </svg>
      );
    case "/groups":
      return (
        <svg viewBox="0 0 24 24" fill="none" strokeWidth="1.75" className={className} aria-hidden>
          <rect x="3" y="3" width="7" height="7" rx="1" />
          <rect x="14" y="3" width="7" height="7" rx="1" />
          <rect x="3" y="14" width="7" height="7" rx="1" />
          <rect x="14" y="14" width="7" height="7" rx="1" />
        </svg>
      );
    case "/markets":
      return (
        <svg viewBox="0 0 24 24" fill="none" strokeWidth="1.75" className={className} aria-hidden>
          <path d="M4 19V5M4 19h16M8 19V9M12 19V13M16 19V7" />
        </svg>
      );
    default:
      return (
        <svg viewBox="0 0 24 24" fill="none" strokeWidth="1.75" className={className} aria-hidden>
          <circle cx="12" cy="8" r="3.5" />
          <path d="M5 20c0-3.3 3.1-6 7-6s7 2.7 7 6" />
        </svg>
      );
  }
}
