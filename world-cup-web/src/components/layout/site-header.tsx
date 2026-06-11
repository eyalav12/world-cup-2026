"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { cn } from "@/lib/utils";

const NAV = [
  { href: "/", label: "Home" },
  { href: "/matches", label: "Matches" },
  { href: "/groups", label: "Groups" },
  { href: "/teams", label: "Teams" },
  { href: "/odds", label: "Odds" },
  { href: "/markets", label: "Markets" },
  { href: "/news", label: "News" },
  { href: "/leaderboard", label: "Leaderboard" },
  { href: "/agent", label: "AI" },
] as const;

function isActive(pathname: string, href: string) {
  return href === "/" ? pathname === "/" : pathname.startsWith(href);
}

function NavLink({
  href,
  label,
  active,
  onClick,
  className,
}: {
  href: string;
  label: string;
  active: boolean;
  onClick?: () => void;
  className?: string;
}) {
  return (
    <Link
      href={href}
      onClick={onClick}
      className={cn(
        "rounded-xl px-4 py-3 text-base font-medium transition-colors sm:rounded-full sm:px-3 sm:py-1.5 sm:text-sm",
        active
          ? "bg-emerald-500 text-[#0b1f14]"
          : "text-emerald-100/80 hover:bg-white/10 hover:text-white",
        className,
      )}
    >
      {label}
    </Link>
  );
}

export function SiteHeader() {
  const pathname = usePathname();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    setMenuOpen(false);
  }, [pathname]);

  useEffect(() => {
    document.body.style.overflow = menuOpen ? "hidden" : "";
    return () => {
      document.body.style.overflow = "";
    };
  }, [menuOpen]);

  return (
    <header className="sticky top-0 z-50 border-b border-white/10 bg-[#0b1f14]/90 backdrop-blur-md">
      <div className="mx-auto flex h-14 max-w-6xl items-center justify-between gap-3 px-4 sm:h-16 sm:px-6">
        <Link href="/" className="flex shrink-0 items-center gap-2">
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-emerald-500 text-lg font-bold text-[#0b1f14]">
            WC
          </span>
          <div>
            <p className="text-sm font-semibold leading-none text-white">
              World Cup 2026
            </p>
            <p className="text-xs text-emerald-200/70 sm:block">Hub</p>
          </div>
        </Link>

        <nav className="hidden items-center gap-1 lg:flex">
          {NAV.map((item) => (
            <NavLink
              key={item.href}
              href={item.href}
              label={item.label}
              active={isActive(pathname, item.href)}
            />
          ))}
        </nav>

        <button
          type="button"
          aria-expanded={menuOpen}
          aria-label={menuOpen ? "Close menu" : "Open menu"}
          onClick={() => setMenuOpen((open) => !open)}
          className="flex h-10 w-10 items-center justify-center rounded-xl border border-white/15 text-white hover:bg-white/10 lg:hidden"
        >
          {menuOpen ? (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              className="h-5 w-5"
              aria-hidden
            >
              <path d="M6 6l12 12M18 6L6 18" />
            </svg>
          ) : (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              className="h-5 w-5"
              aria-hidden
            >
              <path d="M4 7h16M4 12h16M4 17h16" />
            </svg>
          )}
        </button>
      </div>

      {menuOpen ? (
        <div className="fixed inset-0 top-14 z-40 lg:hidden">
          <button
            type="button"
            aria-label="Close menu"
            className="absolute inset-0 bg-black/50"
            onClick={() => setMenuOpen(false)}
          />
          <nav className="relative max-h-[calc(100dvh-3.5rem)] overflow-y-auto border-b border-white/10 bg-[#0b1f14] px-4 py-4 pb-[max(1rem,env(safe-area-inset-bottom))]">
            <div className="grid gap-1">
              {NAV.map((item) => (
                <NavLink
                  key={item.href}
                  href={item.href}
                  label={item.label}
                  active={isActive(pathname, item.href)}
                  onClick={() => setMenuOpen(false)}
                />
              ))}
            </div>
          </nav>
        </div>
      ) : null}
    </header>
  );
}
