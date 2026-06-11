import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function slugifyTeamName(name: string): string {
  return encodeURIComponent(name.trim());
}

export function teamNameFromSlug(slug: string): string {
  return decodeURIComponent(slug);
}
